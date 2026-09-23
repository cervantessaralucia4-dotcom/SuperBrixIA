from fastapi import APIRouter, HTTPException
from app.models.evento import MensajeClasificacion, ResultadoClasificacionIA
from app.services.ia_service import ia_service

router = APIRouter(prefix="/ia", tags=["Inteligencia Artificial"])


@router.post("/clasificar", response_model=ResultadoClasificacionIA)
def clasificar_novedad(req: MensajeClasificacion):
    """
    Analiza una descripción de texto libre reportada por un operario y devuelve
    la categoría industrial inferida por IA, el porcentaje de confianza y la recomendación técnica.
    """
    if not req.mensaje or not req.mensaje.strip():
        raise HTTPException(
            status_code=400,
            detail="El mensaje no puede estar vacío."
        )

    return ia_service.clasificar(mensaje=req.mensaje)
