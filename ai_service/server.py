from fastapi import FastAPI
from pydantic import BaseModel
import requests

app = FastAPI()

OLLAMA_URL = "http://127.0.0.1:11434/api/generate"
MODEL = "qwen3:1.7b"

class ChatRequest(BaseModel):
    prompt: str

class ChatResponse(BaseModel):
    response: str

@app.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest):

    ollama_response = requests.post(
        OLLAMA_URL,
        json={
            "model": MODEL,
            "prompt": request.prompt,
            "stream": False
        }
    )

    ollama_response.raise_for_status()

    data = ollama_response.json()

    return ChatResponse(
        response=data["response"]
    )
