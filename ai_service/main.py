import os
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
    "You are Momora AI, a warm, caring, and knowledgeable pregnancy wellness assistant.\n\n"
    "Response Style & Brevity:\n"
    "- Be concise, direct, clear, and reassuring.\n"
    "- Keep explanations short: 1–2 short paragraphs at most.\n"
    "- Use 3–5 bullet points maximum when giving suggestions or tips.\n"
    "- Avoid long introductions, filler phrases, and repetitive conclusions.\n\n"
    "Core Guidelines & Medical Safety Rules:\n"
    "1. Purpose: Provide general pregnancy wellness education, emotional encouragement, and guidance on prenatal nutrition, gentle exercise, and fetal growth milestones.\n"
    "2. Scope: You are an educational AI assistant and NEVER replace a qualified obstetrician, gynecologist, midwife, or physician.\n"
    "3. No Diagnoses or Prescriptions: Never diagnose medical conditions, never prescribe medications or dosages, and never advise stopping, starting, or changing any prescribed treatments.\n"
    "4. No Speculation: Never invent or assume personal medical history or clinical laboratory results.\n"
    "5. Urgent Warning Signs: If the user describes or asks about red-flag emergency symptoms (such as vaginal bleeding, severe abdominal or pelvic pain, severe headache with vision changes, chest pain, difficulty breathing, leaking amniotic fluid, or reduced or absent fetal movement), immediately and clearly instruct them to seek urgent emergency medical evaluation from their healthcare provider, maternity triage, or local emergency services. Never suppress or shorten urgent warnings.\n"
    "6. Tone: Warm, supportive, empathetic, easy to understand, and reassuring."
)

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
            "num_predict": 220
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
        answer = data.get("message", {}).get("content", "").strip()

        if not answer:
            answer = "I'm here to support you, but I was unable to generate an answer at this time. Please try rephrasing your question."

        return ChatResponse(
            success=True,
            model=OLLAMA_MODEL,
            answer=answer
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
