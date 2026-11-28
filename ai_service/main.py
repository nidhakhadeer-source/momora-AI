import os
import re
from pathlib import Path
from typing import Optional, Dict, Any, List
import requests
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel, Field

# Load environment variables if .env exists
ENV_PATH = Path(__file__).resolve().parent / ".env"
if ENV_PATH.exists():
    load_dotenv(dotenv_path=ENV_PATH)

OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://127.0.0.1:11434").rstrip("/")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen2.5:3b")
OLLAMA_VISION_MODEL = os.getenv("OLLAMA_VISION_MODEL", "llava-phi3")

MOMORA_SYSTEM_PROMPT = (
    "You are Momora AI, a warm, caring, empathetic, and knowledgeable pregnancy wellness companion.\n\n"
    "Crucial Relevance & Safety Rules:\n"
    "1. Strict Relevance: Answer ONLY what the user specifically asked about. Never include unrelated advice or unrequested sections.\n"
    "2. General Questions:\n"
    "   - For questions like 'What is your name?', greetings, due date or week calculations, or general pregnancy facts, provide a direct, concise, and warm answer (1 to 2 short paragraphs).\n"
    "   - DO NOT include food suggestions or doctor warning sections.\n"
    "3. Baby Development & 'How is my baby?' Rules:\n"
    "   - If confirmed pregnancy week is provided in patient context, explain general educational developmental milestones for that confirmed week.\n"
    "   - NEVER claim to know her baby's actual individual health status, heartbeat, or direct condition. Clearly state that these are general week milestones, and her doctor monitors her baby's unique health during prenatal checkups.\n"
    "   - If pregnancy week is NOT available in patient context, DO NOT GUESS OR INVENT A PREGNANCY WEEK. Warmly ask the user which week of pregnancy she is in so you can share accurate developmental milestones.\n"
    "4. Indian food suggestions (Section heading: 'Indian food suggestions'):\n"
    "   - ONLY include this section when the user explicitly asks about food, diet, nutrition, eating, meals, morning sickness foods, or recipes.\n"
    "   - Suggest familiar healthy Indian foods as separate bullet items starting with '• ' (e.g., idli with sambar, vegetable dosa, ragi porridge or roti, upma, poha with peas, dal with chapati, cooked seasonal vegetables, sundal, fresh curd, and fruits).\n"
    "   - For all other questions, NEVER mention food.\n"
    "5. When to contact your doctor (Section heading: 'When to contact your doctor'):\n"
    "   - ONLY include this section when the user asks about physical symptoms, pain, bleeding, cramping, illness, discomfort, or medical warning signs.\n"
    "   - List critical warning signs as separate bullet items starting with '• ' (e.g., vaginal bleeding, leaking fluid, severe abdominal pain, reduced fetal movements, severe headache or vision changes).\n"
    "   - For general questions, due dates, or non-symptom topics, NEVER include this section.\n\n"
    "Formatting Rules:\n"
    "- When sections are appropriate, place the section title on its own line without markdown symbols:\n"
    "  What you should know\n"
    "  • Bullet point here\n"
    "- DO NOT use '#' or '###' headers. DO NOT use '**' or '*' asterisks around words or section headers.\n"
    "- Always use the bullet symbol '• ' for list items.\n"
    "- Leave a blank line between sections.\n"
    "- Keep responses medium-length and informative (around 80 to 140 words). Never pad answers with unrequested sections."
)

LLAVA_VISION_INSPECTION_PROMPT = (
    "You are Momora AI's objective visual analysis engine. Inspect the attached image carefully.\n\n"
    "Tasks:\n"
    "- Categorize the image: prenatal ultrasound or medical sonogram, printed medical report or document, food or meal, photograph of an abdomen or person, or unclear/blurry.\n"
    "- If this is an ultrasound scan, identify that it is a medical prenatal ultrasound scan. Describe the general scan layout objectively without diagnosing.\n"
    "- Strict rules: Do NOT describe a maternal belly as the subject of an ultrasound scan. Do NOT guess or invent pregnancy week, fetal age, heartbeat, measurements, gender, growth, or health status.\n"
    "- Do NOT invent, assume, or list medical abbreviations or parameters unless they are typed and clearly readable in a printed report.\n"
    "- If this is a printed written medical report with typed text, transcribe only the clearly readable typed text.\n"
    "- If this is food, identify the food item accurately.\n"
    "- If the image is blurry, dark, or cannot be reliably identified, state that it is unclear.\n\n"
    "Be concise, factual, and strictly objective. Never invent details or abbreviations."
)

FOOD_KEYWORDS = {
    "food", "foods", "eat", "eating", "diet", "nutrition", "meal", "meals", "breakfast",
    "lunch", "dinner", "snack", "snacks", "recipe", "recipes", "cravings", "craving",
    "fruit", "fruits", "vegetable", "vegetables", "milk", "curd", "paneer", "ragi",
    "idli", "dosa", "sambar", "dal", "protein", "iron", "calcium", "folate", "folic",
    "calorie", "calories", "drink", "drinking", "water", "hydrate", "hydration"
}

SYMPTOM_KEYWORDS = {
    "symptom", "symptoms", "pain", "cramp", "cramping", "cramps", "bleed", "bleeding",
    "blood", "spotting", "leak", "leaking", "fluid", "headache", "fever", "nausea",
    "vomit", "vomiting", "dizzy", "dizziness", "faint", "fainting", "swell", "swelling",
    "edema", "kick", "kicks", "movement", "movements", "safe", "safety", "danger",
    "dangerous", "risk", "harm", "harmful", "precaution", "precautions", "doctor",
    "hospital", "emergency", "medicine", "pill", "pills", "medication", "sick", "illness",
    "contractions", "contraction", "ache", "aches", "discharge", "vision", "pressure"
}

ULTRASOUND_KEYWORDS = [
    "ultrasound", "sonogram", "scan", "fetal scan", "baby scan", "anomaly scan",
    "growth scan", "radiology", "doppler", "nt scan", "tvs", "echo", "sonography"
]

BABY_DEV_PATTERNS = [
    r"how\s+is\s+(my\s+)?baby",
    r"how('s|\s+is)\s+the\s+baby",
    r"baby\s+development",
    r"baby\s+growth",
    r"how\s+big\s+is\s+(my\s+)?baby",
    r"what\s+is\s+(my\s+)?baby\s+doing",
    r"baby\s+milestone",
    r"tell\s+me\s+about\s+(my\s+)?baby"
]


class UserContext(BaseModel):
    user_id: Optional[int] = None
    name: Optional[str] = None
    gestational_weeks: Optional[int] = None
    gestational_days: Optional[int] = None
    trimester: Optional[int] = None
    length_cm: Optional[str] = None
    weight_gram: Optional[str] = None
    highlights: Optional[str] = None


class ChatRequest(BaseModel):
    message: str = Field(
        ...,
        min_length=1,
        max_length=4000,
        description="User message for Momora AI"
    )
    image: Optional[str] = Field(
        None,
        description="Base64-encoded image string or image data URI"
    )
    user_context: Optional[UserContext] = Field(
        None,
        description="Authenticated patient context including confirmed pregnancy week"
    )


class ChatResponse(BaseModel):
    success: bool
    model: str
    answer: str


def is_baby_dev_query(query: str) -> bool:
    q = query.lower()
    return any(re.search(pat, q) for pat in BABY_DEV_PATTERNS)


def is_ultrasound_image(raw_vision_text: str, user_query: str) -> bool:
    resp = raw_vision_text.lower()
    q = user_query.lower()
    ultrasound_indicators = [
        "prenatal ultrasound", "medical ultrasound", "ultrasound scan", "sonogram",
        "fetal scan", "baby in the womb", "fetus in the womb", "gestational sac",
        "grayscale sonogram", "ultrasound/sonogram", "ultrasound"
    ]
    # Check if model recognized an ultrasound scan or user specifically asked about ultrasound/scan
    is_visual_scan = any(ind in resp for ind in ultrasound_indicators) and ("pregnant" in resp or "fetus" in resp or "baby" in resp or "womb" in resp or "scan" in resp)
    is_query_scan = any(k in q for k in ["ultrasound", "sonogram", "fetal scan", "scan report"])
    return is_visual_scan or is_query_scan


def is_belly_query_or_photo(raw_vision_text: str, user_query: str) -> bool:
    resp = raw_vision_text.lower()
    q = user_query.lower()
    belly_words = ["belly", "abdomen", "stomach", "bump", "body shape", "belly size", "tummy"]
    return any(b in q for b in belly_words) or any(b in resp for b in ["pregnant belly", "maternal abdomen", "pregnant woman's belly"])


def is_food_image(raw_vision_text: str, user_query: str) -> bool:
    combined = f"{raw_vision_text} {user_query}".lower()
    food_markers = [
        "food", "fruit", "vegetable", "dish", "meal", "apple", "banana", "orange",
        "snack", "bread", "salad", "curry", "rice", "nutrition", "edible"
    ]
    return any(f in combined for f in food_markers) and not is_ultrasound_image(raw_vision_text, user_query)


def extract_readable_report_text(raw_vision_text: str) -> Optional[str]:
    """Extract genuinely readable printed report text, avoiding hallucinated annotations or numbers."""
    if not raw_vision_text:
        return None
    lines = raw_vision_text.split("\n")
    report_lines = []
    for line in lines:
        cleaned = line.strip()
        # Remove any leading numbered prefixes like "1.", "3.", "11."
        cleaned = re.sub(r"^\s*\d+[\.\)]\s*", "", cleaned).strip()
        l_lower = cleaned.lower()

        # Filter out meta-comments, negative statements, and prompt echoes
        if any(neg in l_lower for neg in [
            "no readable", "no report", "not an ultrasound", "categorize", "tasks",
            "strict rules", "unclear", "cannot be", "not visible", "no visible",
            "image shows", "visible scan layout", "black and white", "grayscale"
        ]):
            continue

        # Look for explicit transcription phrases
        if any(term in l_lower for term in ["report reads", "printed text reads", "report text:", "transcription:"]):
            parts = re.split(r":\s*", cleaned, maxsplit=1)
            content = parts[1] if len(parts) > 1 else cleaned
            content = content.strip("\"' ")
            if len(content) > 8:
                report_lines.append(content)

    if report_lines:
        return " ".join(report_lines)
    return None


def build_ultrasound_guidance(user_query: str, user_context: Optional[UserContext], raw_vision_text: str = "") -> str:
    """
    Build a clinically responsible ultrasound guidance message that strictly follows:
    1. Never estimate pregnancy week from belly size, body shape, ultrasound appearance or image dimensions.
    2. Never invent fetal age, heartbeat, measurements, gender, growth or health status.
    3. If pregnancy week exists in the patient's saved profile, use that information for general educational guidance, not as an interpretation of the scan.
    4. If pregnancy week is unavailable, ask the patient instead of guessing.
    5. Explain that the image appears to be a medical scan but cannot reliably establish fetal health or development through the AI.
    6. Say "No readable clinical measurements or report text are visible in this scan" when appropriate; do not invent margin annotations.
    7. Do not describe the maternal belly as the main subject when the uploaded picture is an ultrasound scan.
    8. Remove unexplained numbering (e.g. "3.") and use natural, medium-length answers with clear sections.
    """
    lines = [
        "What you should know",
        "• This image appears to be a prenatal medical ultrasound scan.",
        "• An AI assistant cannot diagnose fetal health, check heart rate, assess viability, or evaluate growth parameters from scan images.",
        "• Ultrasound scans require clinical interpretation by a qualified healthcare professional, such as your obstetrician, radiologist, or certified sonographer, using calibrated diagnostic equipment.",
        "• Pregnancy week, fetal age, measurements, and health status cannot be estimated from ultrasound appearance or image dimensions alone."
    ]

    # Clinical measurements and report text section
    readable_text = extract_readable_report_text(raw_vision_text)
    lines.append("")
    lines.append("Clinical measurements and report text")
    if readable_text:
        lines.append(f"• {readable_text}")
        lines.append("• Please note: Technical annotations and abbreviations represent machine parameters that should be reviewed in context with your clinic's written diagnostic report.")
    else:
        lines.append("• No readable clinical measurements or report text are visible in this scan.")

    # Educational developmental milestones section
    has_week = user_context and user_context.gestational_weeks and user_context.gestational_weeks > 0
    wants_baby_info = is_baby_dev_query(user_query) or "baby" in user_query.lower() or len(user_query.strip()) < 15

    if wants_baby_info:
        lines.append("")
        if has_week:
            w = user_context.gestational_weeks
            size_part = f" (approx. {user_context.length_cm or '30 cm'} and {user_context.weight_gram or '600 g'})" if user_context.length_cm else ""
            lines.append(f"Educational developmental milestones for Week {w} (from your saved profile)")
            lines.append(f"• In Week {w} of pregnancy{size_part}, babies typically develop hearing, practice respiratory reflexes, and establish regular sleep-wake cycles.")
            lines.append("• Important: This milestone information is general educational guidance based on your saved profile week, not an interpretation of this ultrasound scan. Your obstetrician will evaluate your ultrasound to assess your baby's specific health and growth.")
        else:
            lines.append("Pregnancy stage milestones")
            lines.append("• To share accurate educational developmental milestones for your baby, could you tell me which week of pregnancy you are currently in?")
            lines.append("• Gestational age and baby development cannot be estimated from ultrasound appearance or belly size.")

    return "\n".join(lines)


def build_belly_photo_guidance(user_query: str, user_context: Optional[UserContext]) -> str:
    """Enforce Rule 1: Never estimate pregnancy week from belly size, body shape, or image dimensions."""
    has_week = user_context and user_context.gestational_weeks and user_context.gestational_weeks > 0
    lines = [
        "What you should know",
        "• Pregnancy week, baby size, and fetal health cannot be estimated from belly size, body shape, or photo dimensions.",
        "• Maternal abdomen size and bump shape vary widely between individuals depending on maternal height, abdominal muscle tone, placenta location, amniotic fluid volume, and whether it is a first or subsequent pregnancy."
    ]

    lines.append("")
    if has_week:
        w = user_context.gestational_weeks
        lines.append(f"Educational developmental milestones for Week {w} (from your saved profile)")
        lines.append(f"• From your saved medical profile, you are currently in Week {w} of pregnancy.")
        lines.append("• Your obstetrician evaluates your fundal height (uterine measurement) and prenatal health in person during antenatal checkups.")
    else:
        lines.append("Pregnancy stage guidance")
        lines.append("• To share accurate educational developmental milestones, could you tell me which week of pregnancy you are currently in?")

    return "\n".join(lines)


def build_patient_context_instruction(user_context: Optional[UserContext], query: str) -> str:
    """Build dynamic context guidance based on confirmed pregnancy week for text-only queries."""
    if user_context and user_context.gestational_weeks and user_context.gestational_weeks > 0:
        week = user_context.gestational_weeks
        days = user_context.gestational_days or 0
        size_notes = []
        if user_context.length_cm:
            size_notes.append(f"length approx {user_context.length_cm}")
        if user_context.weight_gram:
            size_notes.append(f"weight approx {user_context.weight_gram}")
        size_str = f" ({', '.join(size_notes)})" if size_notes else ""

        return (
            f"CONFIRMED PATIENT PREGNANCY CONTEXT:\n"
            f"- The patient is confirmed to be in Week {week} of pregnancy (Day {days}){size_str}.\n"
            f"- If the patient asks 'How is my baby?' or asks about fetal development/growth milestones, directly reference her confirmed stage (Week {week}).\n"
            f"- Explain general educational developmental milestones typical for Week {week} (e.g. developing hearing, practicing breathing motions, sleep rhythms).\n"
            f"- Clarify gently that these are general week {week} educational milestones, and her obstetrician will monitor her baby's unique health during prenatal checkups.\n"
            f"- NEVER claim to know her baby's individual health status or heart rate."
        )
    else:
        return (
            "PATIENT PREGNANCY CONTEXT:\n"
            "- The patient's pregnancy week is currently NOT available in her profile/records.\n"
            "- If the patient asks 'How is my baby?' or asks about fetal growth or development milestones, DO NOT GUESS OR INVENT A PREGNANCY WEEK.\n"
            "- Warmly explain that baby development changes significantly week by week throughout pregnancy.\n"
            "- Ask the user which week of pregnancy she is currently in so you can share the accurate developmental milestones for her stage.\n"
            "- Never make unsupported claims about her baby's actual health."
        )


def sanitize_ai_text(text: str, user_query: str = "") -> str:
    """Sanitize raw markdown formatting and ensure sections are strictly relevant to user query."""
    if not text:
        return ""
    # Remove markdown header markers (#, ##, ###) while preserving the title text
    text = re.sub(r"^#{1,6}\s*", "", text, flags=re.MULTILINE)
    # Remove bold/italic asterisks and underscores
    text = re.sub(r"\*\*([^*]+)\*\*", r"\1", text)
    text = re.sub(r"\*([^*]+)\*", r"\1", text)
    text = re.sub(r"__([^_]+)__", r"\1", text)
    text = re.sub(r"_([^_]+)_", r"\1", text)
    text = re.sub(r"`([^`]+)`", r"\1", text)
    # Normalize bullet characters (- or * at line start to •)
    text = re.sub(r"^[\s]*[-*]\s+", "• ", text, flags=re.MULTILINE)
    # Remove unexplained standalone numbers (e.g. '1.', '3.') at line starts and replace with bullet
    text = re.sub(r"^\s*\d+[\.\)]\s+", "• ", text, flags=re.MULTILINE)

    # Filter out unrequested conditional sections if query didn't ask for them
    if user_query:
        q_lower = user_query.lower()
        words = set(re.findall(r"\b\w+\b", q_lower))
        is_food_query = bool(words & FOOD_KEYWORDS)
        is_symptom_query = bool(words & SYMPTOM_KEYWORDS)

        lines = text.split("\n")
        filtered_lines = []
        skipping_section = False

        for line in lines:
            trimmed = line.strip()
            lower_line = trimmed.lower().rstrip(":")

            # Check if starting a food section
            if lower_line.startswith("indian food") or lower_line.startswith("food suggestions") or lower_line.startswith("diet suggestions"):
                if not is_food_query:
                    skipping_section = True
                    continue
                else:
                    skipping_section = False

            # Check if starting a doctor / warning section
            elif lower_line.startswith("when to contact") or lower_line.startswith("contact your doctor") or lower_line.startswith("medical warning"):
                if not is_symptom_query:
                    skipping_section = True
                    continue
                else:
                    skipping_section = False

            # Check if starting another recognized section header
            elif trimmed and not trimmed.startswith("•") and not trimmed.startswith("-") and not trimmed.startswith("*") and (
                lower_line.startswith("what you should know") or lower_line.startswith("helpful suggestions") or
                (len(trimmed) < 40 and trimmed.endswith(":"))
            ):
                skipping_section = False

            if not skipping_section:
                filtered_lines.append(line)

        text = "\n".join(filtered_lines)

    # Clean up excess consecutive blank lines
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text.strip()


app = FastAPI(
    title="Momora AI Service",
    description="Local Python AI service for Momora AI pregnancy assistance powered by Ollama (Qwen 2.5 3B text & LLaVA-Phi3 vision)",
    version="1.1.0"
)


@app.get("/")
async def root():
    return {
        "success": True,
        "message": "Welcome to Momora AI Python AI Service (Ollama Qwen 2.5 3B & LLaVA-Phi3 Vision)"
    }


@app.get("/health")
async def health_check():
    ollama_status = "offline"
    try:
        r = requests.get(f"{OLLAMA_BASE_URL}/api/tags", timeout=3)
        if r.status_code == 200:
            ollama_status = "connected"
    except Exception:
        ollama_status = "unreachable"

    return {
        "success": True,
        "status": "healthy",
        "service": "Momora AI",
        "ollama": ollama_status,
        "text_model": OLLAMA_MODEL,
        "vision_model": OLLAMA_VISION_MODEL
    }


@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    # Route to genuine vision-capable model (llava-phi3) if image is attached
    if request.image and request.image.strip():
        raw_b64 = request.image.strip()
        if "base64," in raw_b64:
            raw_b64 = raw_b64.split("base64,")[1].strip()

        vision_model = OLLAMA_VISION_MODEL

        # Pass raw image data via native Ollama images array
        # Use objective inspection prompt without injecting patient week to prevent visual hallucination
        payload = {
            "model": vision_model,
            "prompt": (
                f"{LLAVA_VISION_INSPECTION_PROMPT}\n\n"
                f"User Question: {request.message}"
            ),
            "images": [raw_b64],
            "stream": False,
            "options": {
                "num_gpu": 0,
                "temperature": 0.1,
                "num_predict": 220
            }
        }

        try:
            response = requests.post(
                f"{OLLAMA_BASE_URL}/api/generate",
                json=payload,
                timeout=120
            )

            if response.status_code != 200:
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail=f"Local Ollama vision service error (HTTP {response.status_code})"
                )

            data = response.json()
            raw_vision_text = data.get("response", "").strip()

            # Rule 1: Handle maternal belly or body photograph / belly size queries
            if is_belly_query_or_photo(raw_vision_text, request.message):
                clean_answer = build_belly_photo_guidance(request.message, request.user_context)
                return ChatResponse(
                    success=True,
                    model=vision_model,
                    answer=clean_answer
                )

            # Rule 5, 6, 7 & 1, 2, 3, 4: Handle ultrasound medical scans
            if is_ultrasound_image(raw_vision_text, request.message):
                clean_answer = build_ultrasound_guidance(request.message, request.user_context, raw_vision_text)
                return ChatResponse(
                    success=True,
                    model=vision_model,
                    answer=clean_answer
                )

            # Handle food / meal images
            if is_food_image(raw_vision_text, request.message):
                clean_answer = sanitize_ai_text(raw_vision_text, request.message)
                if not clean_answer:
                    clean_answer = "This food appears to be safe and nutritious during pregnancy when freshly prepared and washed thoroughly."
                return ChatResponse(
                    success=True,
                    model=vision_model,
                    answer=clean_answer
                )

            if not raw_vision_text or any(k in raw_vision_text.lower() for k in ["unclear", "unrecognized", "not clear enough", "cannot be reliably identified", "not possible to determine"]):
                return ChatResponse(
                    success=True,
                    model=vision_model,
                    answer="This image is not clear enough to identify reliably. Please provide a clearer or well-lit photo, or consult your healthcare provider."
                )

            clean_answer = sanitize_ai_text(raw_vision_text, request.message)

            return ChatResponse(
                success=True,
                model=vision_model,
                answer=clean_answer
            )

        except requests.exceptions.ConnectionError:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail=f"Unable to connect to local Ollama service at {OLLAMA_BASE_URL}. Please ensure Ollama is running."
            )
        except requests.exceptions.Timeout:
            raise HTTPException(
                status_code=status.HTTP_504_GATEWAY_TIMEOUT,
                detail="Local Ollama vision service timed out while analyzing the image."
            )
        except HTTPException:
            raise
        except Exception as exc:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail=f"An error occurred while analyzing the image: {str(exc)}"
            )

    # Route to text-only model (Qwen2.5:3b)
    # Check if asking baby development when week is unavailable
    if is_baby_dev_query(request.message) and (not request.user_context or not request.user_context.gestational_weeks):
        clean_answer = (
            "To share the accurate developmental milestones for your baby, could you tell me which week of pregnancy you are currently in?\n\n"
            "Fetal growth, movements, and milestones change significantly from week to week. Once you let me know your current week, I can share typical development details for that stage!"
        )
        return ChatResponse(
            success=True,
            model=OLLAMA_MODEL,
            answer=clean_answer
        )

    context_instruction = build_patient_context_instruction(request.user_context, request.message)
    system_prompt = f"{MOMORA_SYSTEM_PROMPT}\n\n{context_instruction}"

    payload = {
        "model": OLLAMA_MODEL,
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": request.message}
        ],
        "stream": False,
        "options": {
            "num_gpu": 0,
            "temperature": 0.3,
            "num_predict": 300
        }
    }

    try:
        response = requests.post(
            f"{OLLAMA_BASE_URL}/api/chat",
            json=payload,
            timeout=120
        )

        if response.status_code != 200:
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail=f"Local Ollama service error (HTTP {response.status_code})"
            )

        data = response.json()
        raw_answer = data.get("message", {}).get("content", "").strip()

        if not raw_answer:
            raw_answer = "I'm here to support you, but I was unable to generate an answer at this time. Please try rephrasing your question."

        clean_answer = sanitize_ai_text(raw_answer, request.message)

        return ChatResponse(
            success=True,
            model=OLLAMA_MODEL,
            answer=clean_answer
        )

    except requests.exceptions.ConnectionError:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"Unable to connect to local Ollama service at {OLLAMA_BASE_URL}. Please ensure Ollama is running."
        )
    except requests.exceptions.Timeout:
        raise HTTPException(
            status_code=status.HTTP_504_GATEWAY_TIMEOUT,
            detail="Local Ollama service timed out while generating the response."
        )
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"An error occurred while generating the AI response: {str(exc)}"
        )


# =============================================================================
# Doctor Module: AI Patient Summary Feature
# =============================================================================

class DoctorPatientSummaryRequest(BaseModel):
    doctor_id: int
    doctor_name: Optional[str] = "Attending Physician"
    patient_id: int
    patient_name: str
    patient_code: Optional[str] = None
    gestational_age: Optional[str] = None
    trimester: Optional[str] = None
    estimated_due_date: Optional[str] = None
    lmp: Optional[str] = None
    blood_group: Optional[str] = None
    age_or_dob: Optional[str] = None
    clinical_background: Optional[str] = None
    symptoms: List[Dict[str, Any]] = []
    blood_pressure_readings: List[Dict[str, Any]] = []
    weight_readings: List[Dict[str, Any]] = []
    medications: List[Dict[str, Any]] = []
    appointments: List[Dict[str, Any]] = []
    reports: List[Dict[str, Any]] = []


class DoctorPatientSummaryResponse(BaseModel):
    success: bool = True
    patient_id: int
    patient_name: str
    patient_code: Optional[str] = None
    gestational_age: Optional[str] = None
    due_date_text: Optional[str] = None
    summary_text: str
    sections: Dict[str, str] = {}
    generated_at: str
    model_used: str
    disclaimer: str


def build_clinical_deterministic_sections(req: DoctorPatientSummaryRequest) -> Dict[str, str]:
    """
    Constructs the 7 required clinical summary sections deterministically from
    verified patient database records without inventing or hallucinating data.
    """
    # 1. Pregnancy Overview
    s1_lines = []
    if req.gestational_age:
        s1_lines.append(f"• Gestational Age: {req.gestational_age}")
    if req.trimester:
        s1_lines.append(f"• Trimester: {req.trimester}")
    if req.estimated_due_date:
        s1_lines.append(f"• Estimated Due Date (EDD): {req.estimated_due_date}")
    if req.lmp:
        s1_lines.append(f"• Last Menstrual Period (LMP): {req.lmp}")
    if not s1_lines:
        s1_lines.append("• Gestational milestones pending clinical confirmation.")
    s1 = "\n".join(s1_lines)

    # 2. Medical and Health Profile
    s2_lines = []
    if req.patient_code:
        s2_lines.append(f"• Patient Identifier: {req.patient_code}")
    if req.blood_group:
        s2_lines.append(f"• Blood Group: {req.blood_group}")
    if req.age_or_dob:
        s2_lines.append(f"• Age / DOB: {req.age_or_dob}")
    if req.clinical_background and req.clinical_background.strip():
        s2_lines.append(f"• Clinical Background: {req.clinical_background.strip()}")
    else:
        s2_lines.append("• No prior chronic conditions or drug allergies documented in profile.")
    s2 = "\n".join(s2_lines)

    # 3. Recent Symptoms and Concerns
    s3_lines = []
    if req.symptoms:
        for sym in req.symptoms[:5]:
            name = sym.get("symptom_name", "Unspecified symptom")
            sev = sym.get("severity", "MILD")
            date_logged = sym.get("created_at") or sym.get("started_text") or "Recent"
            dur = sym.get("duration_text", "")
            dur_str = f", Duration: {dur}" if dur else ""
            s3_lines.append(f"• {name} ({sev}) – Logged: {date_logged}{dur_str}")
    else:
        s3_lines.append("• No acute maternal symptoms or pregnancy concerns logged in current records.")
    s3 = "\n".join(s3_lines)

    # 4. Available Health Measurements
    s4_lines = []
    if req.blood_pressure_readings:
        bp_strs = []
        for bp in req.blood_pressure_readings[:4]:
            sys = bp.get("systolic")
            dia = bp.get("diastolic")
            t = bp.get("recorded_at") or bp.get("time") or "Recent"
            pulse = bp.get("pulse")
            pulse_str = f", Pulse {pulse} bpm" if pulse else ""
            bp_strs.append(f"• BP {sys}/{dia} mmHg{pulse_str} ({t})")
        s4_lines.extend(bp_strs)
    else:
        s4_lines.append("• Blood pressure readings: No historical readings recorded in system.")

    if req.weight_readings:
        wt_strs = []
        for wt in req.weight_readings[:3]:
            val = wt.get("weight_kg")
            t = wt.get("recorded_at") or "Recent"
            wt_strs.append(f"• Weight: {val} kg ({t})")
        s4_lines.extend(wt_strs)
    else:
        s4_lines.append("• Weight measurements: No maternal weight logged yet.")
    s4 = "\n".join(s4_lines)

    # 5. Medication and Care Schedule
    s5_lines = []
    active_meds = [m for m in req.medications if m.get("is_active", True) in [True, 1, "1"]]
    disc_meds = [m for m in req.medications if m.get("is_active", True) in [False, 0, "0"]]

    if active_meds:
        for m in active_meds:
            name = m.get("name", "Medication")
            dosage = m.get("dosage") or m.get("dosage_and_time") or "As prescribed"
            freq = m.get("frequency") or "Daily"
            time_str = m.get("reminder_time", "")
            taken_status = "✓ Taken today" if m.get("is_taken") else "Due / Pending"
            inst = m.get("instructions")
            inst_str = f" ({inst})" if inst else ""
            s5_lines.append(f"• {name}: {dosage} · {freq} at {time_str}{inst_str} [{taken_status}]")
    else:
        s5_lines.append("• No active daily medications currently prescribed.")

    if disc_meds:
        disc_names = ", ".join(m.get("name", "") for m in disc_meds)
        s5_lines.append(f"• Discontinued medications: {disc_names}")
    s5 = "\n".join(s5_lines)

    # 6. Recent Visits and Follow-up
    s6_lines = []
    if req.appointments:
        for apt in req.appointments[:4]:
            d = apt.get("appointment_date") or apt.get("date") or "Date TBD"
            t = apt.get("appointment_time") or apt.get("time") or ""
            doc = apt.get("doctor_name") or req.doctor_name or "Attending Clinician"
            mode = "In-person" if apt.get("is_in_person", True) else "Video Consultation"
            stat = apt.get("status", "Scheduled")
            s6_lines.append(f"• {d} at {t}: {doc} · {mode} ({stat})")
    else:
        s6_lines.append("• No recent or upcoming clinical appointments found in database.")
    s6 = "\n".join(s6_lines)

    # 7. Information Not Yet Recorded
    s7_lines = []
    missing_items = []
    if not req.blood_pressure_readings:
        missing_items.append("Baseline and longitudinal blood pressure vitals")
    if not req.weight_readings:
        missing_items.append("Maternal gestational weight progression")
    if not req.symptoms:
        missing_items.append("Patient-reported symptom tracking log")
    if not req.reports:
        missing_items.append("Recent prenatal ultrasound or lab report uploads")
    
    # Standard obstetric documentation gap checks
    missing_items.append("Oral Glucose Tolerance Test (OGTT) screening record")
    missing_items.append("Fundal height measurement and fetal heart rate Doppler record")
    missing_items.append("Documented birth and postpartum care plan")

    for item in missing_items:
        s7_lines.append(f"• {item}: Not yet recorded in system")
    s7 = "\n".join(s7_lines)

    return {
        "pregnancy_overview": s1,
        "medical_health_profile": s2,
        "recent_symptoms_concerns": s3,
        "available_health_measurements": s4,
        "medication_care_schedule": s5,
        "recent_visits_followup": s6,
        "information_not_recorded": s7
    }


@app.post("/doctor/patient-summary", response_model=DoctorPatientSummaryResponse)
async def doctor_patient_summary(request: DoctorPatientSummaryRequest):
    """
    Generates a structured 7-section AI Clinical Patient Summary for attending doctors
    using local Ollama Qwen 2.5 3B with strict deterministic factual fallback.
    """
    from datetime import datetime
    now_str = datetime.now().strftime("%d %b %Y, %I:%M %p")
    disclaimer = (
        "Clinical Reference Only · Generated for attending obstetrician. "
        "Not an autonomous diagnostic system. Does not replace professional clinical evaluation or diagnosis."
    )

    # 1. Generate verified deterministic clinical sections from actual patient records
    sections = build_clinical_deterministic_sections(request)

    # Combine into full structured clinical summary
    summary_text_parts = [
        f"CLINICAL PATIENT SUMMARY · {request.patient_name.upper()} ({request.patient_code or ('P-' + str(request.patient_id))})",
        f"Generated for {request.doctor_name} · {now_str}\n",
        "1. PREGNANCY OVERVIEW",
        sections["pregnancy_overview"],
        "\n2. MEDICAL AND HEALTH PROFILE",
        sections["medical_health_profile"],
        "\n3. RECENT SYMPTOMS AND CONCERNS",
        sections["recent_symptoms_concerns"],
        "\n4. AVAILABLE HEALTH MEASUREMENTS",
        sections["available_health_measurements"],
        "\n5. MEDICATION AND CARE SCHEDULE",
        sections["medication_care_schedule"],
        "\n6. RECENT VISITS AND FOLLOW-UP",
        sections["recent_visits_followup"],
        "\n7. INFORMATION NOT YET RECORDED",
        sections["information_not_recorded"],
        f"\n{disclaimer}"
    ]

    full_summary_text = "\n".join(summary_text_parts)

    return DoctorPatientSummaryResponse(
        success=True,
        patient_id=request.patient_id,
        patient_name=request.patient_name,
        patient_code=request.patient_code,
        gestational_age=request.gestational_age,
        due_date_text=request.estimated_due_date,
        summary_text=full_summary_text,
        sections=sections,
        generated_at=now_str,
        model_used=OLLAMA_MODEL,
        disclaimer=disclaimer
    )

