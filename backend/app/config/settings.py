import os
from functools import lru_cache
from typing import List, Union
from pydantic import field_validator
from dotenv import load_dotenv

# Cargar variables de entorno desde .env si existe
load_dotenv()

try:
    from pydantic_settings import BaseSettings, SettingsConfigDict
    HAS_PYDANTIC_SETTINGS = True
except ImportError:
    from pydantic import BaseModel as BaseSettings
    HAS_PYDANTIC_SETTINGS = False


class Settings(BaseSettings):
    PROJECT_NAME: str = "SuperBrixIA API"
    VERSION: str = "1.0.0"
    DESCRIPTION: str = "API Backend y orquestador para SuperBrix IA: clasificación de novedades operativas con IA y sincronización con Google Sheets."
    API_V1_STR: str = "/api/v1"
    PORT: int = 8000
    HOST: str = "127.0.0.1"

    # Proveedor de Inteligencia Artificial (abstraído para permitir cualquier proveedor)
    IA_API_KEY: str = os.getenv("IA_API_KEY") or os.getenv("GEMINI_API_KEY") or ""
    IA_MODEL: str = os.getenv("IA_MODEL", "gemini-3.1-flash-lite")

    # Google Sheets / Apps Script Integration
    GOOGLE_SCRIPT_URL: str = os.getenv("GOOGLE_SCRIPT_URL") or os.getenv("GOOGLE_APPS_SCRIPT_URL") or ""
    GOOGLE_SHEET_ID: str = os.getenv("GOOGLE_SHEET_ID", "")
    GOOGLE_SHEET_NAME: str = os.getenv("GOOGLE_SHEET_NAME", "Eventos_Produccion")

    # CORS
    CORS_ORIGINS: Union[List[str], str] = ["*"]

    @field_validator("CORS_ORIGINS", mode="before")
    @classmethod
    def assemble_cors_origins(cls, v: Union[str, List[str]]) -> List[str]:
        if isinstance(v, str) and not v.startswith("["):
            return [i.strip() for i in v.split(",")]
        elif isinstance(v, list):
            return v
        return ["*"]

    if HAS_PYDANTIC_SETTINGS:
        model_config = SettingsConfigDict(
            env_file=".env",
            env_file_encoding="utf-8",
            extra="ignore"
        )


@lru_cache()
def get_settings() -> Settings:
    return Settings(
        IA_API_KEY=os.getenv("IA_API_KEY") or os.getenv("GEMINI_API_KEY") or "",
        GOOGLE_SCRIPT_URL=os.getenv("GOOGLE_SCRIPT_URL") or os.getenv("GOOGLE_APPS_SCRIPT_URL") or "",
        GOOGLE_SHEET_ID=os.getenv("GOOGLE_SHEET_ID", ""),
    )


settings = get_settings()
