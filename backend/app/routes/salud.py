from datetime import datetime
from fastapi import APIRouter
from app.config.settings import settings

router = APIRouter(tags=["Salud"])


@router.get("/salud")
@router.get("/health")
def verificar_salud():
    """
    Endpoint de verificación de estado y salud del servicio SuperBrix IA.
    """
    return {
        "estado": "operativo",
        "servicio": settings.PROJECT_NAME,
        "timestamp": datetime.now().isoformat(),
        "version": "1.0.0",
        "ia_disponible": bool(settings.IA_API_KEY),
        "sheets_conectado": bool(settings.GOOGLE_SCRIPT_URL or settings.GOOGLE_SHEET_ID)
    }
