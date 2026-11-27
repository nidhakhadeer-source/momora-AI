import os
import re
from pathlib import Path
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

MOMORA_SYSTEM_PROMPT = (
    "You are Momora AI, a warm, caring, empathetic, and knowledgeable pregnancy wellness companion.\n\n"
    "Crucial Relevance & Conditional Section Rules:\n"
    "1. Strict Relevance: Answer ONLY what the user specifically asked about. Never include unrelated advice or unrequested sections.\n"
    "2. General Questions:\n"
    "   - For questions like 'What is your name?', greetings, due date or week calculations, or general pregnancy facts, provide a direct, concise, and warm answer (1 to 2 short paragraphs).\n"
    "   - DO NOT include food suggestions or doctor warning sections.\n"
    "3. Indian food suggestions (Section heading: 'Indian food suggestions'):\n"
    "   - ONLY include this section when the user explicitly asks about food, diet, nutrition, eating, meals, morning sickness foods, or recipes.\n"
    "   - Suggest familiar healthy Indian foods as separate bullet items starting with '• ' (e.g., idli with sambar, vegetable dosa, ragi porridge or roti, upma, poha with peas, dal with chapati, cooked seasonal vegetables, sundal, fresh curd, and fruits).\n"
    "   - For all other questions (due dates, fetal development, exercise, naming, etc.), NEVER mention food.\n"
    "4. When to contact your doctor (Section heading: 'When to contact your doctor'):\n"
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
    description="Local Python AI service for Momora AI pregnancy assistance powered by Ollama and Qwen 2.5 3B",
    version="1.0.0"
)


class ChatRequest(BaseModel):
    message: str = Field(
        ...,
        min_length=1,
        max_length=2000,
        description="User message for Momora AI"
    )


class ChatResponse(BaseModel):
    success: bool
    model: str
    answer: str


@app.get("/")
async def root():
    return {
        "success": True,
        "message": "Welcome to Momora AI Python AI Service (Ollama Qwen 2.5 3B)"
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
        "model": OLLAMA_MODEL
    }


@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    payload = {
        "model": OLLAMA_MODEL,
        "messages": [
            {"role": "system", "content": MOMORA_SYSTEM_PROMPT},
            {"role": "user", "content": request.message}
        ],
        "stream": False,
        "options": {
            "temperature": 0.4,
            "num_predict": 320
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
