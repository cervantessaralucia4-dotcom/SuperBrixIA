from typing import List, Optional
from fastapi import APIRouter, HTTPException, Query, status
from datetime import datetime
import uuid
from app.models.evento import Evento, TipoEvento, CategoriaIA
from app.services.sheets_service import sheets_service
from app.services.ia_service import ia_service

router = APIRouter(prefix="/eventos", tags=["Eventos y Novedades"])


@router.get("", response_model=List[Evento])
def listar_eventos(
    operario_id: Optional[str] = Query(None, description="Filtrar por ID de operario"),
    maquina_id: Optional[str] = Query(None, description="Filtrar por ID de máquina"),
    orden_id: Optional[str] = Query(None, description="Filtrar por ID de orden"),
    fecha: Optional[str] = Query(None, description="Filtrar por fecha YYYY-MM-DD"),
    categoria: Optional[str] = Query(None, description="Filtrar por categoría IA"),
    tipo_evento: Optional[str] = Query(None, description="Filtrar por tipo de evento")
):
    """
    Obtiene la lista de eventos de producción e interrupciones registradas.
    """
    return sheets_service.obtener_todos(
        operario_id=operario_id,
        maquina_id=maquina_id,
        orden_id=orden_id,
        fecha=fecha,
        categoria=categoria,
        tipo_evento=tipo_evento
    )


@router.get("/{evento_id}", response_model=Evento)
def detalle_evento(evento_id: str):
    """
    Obtiene el detalle completo de un evento por su ID único.
    """
    evento = sheets_service.obtener_por_id(evento_id)
    if not evento:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Evento con ID '{evento_id}' no encontrado."
        )
    return evento


@router.post("", response_model=Evento, status_code=status.HTTP_201_CREATED)
def registrar_evento(evento: Evento):
    """
    Registra un nuevo evento o novedad de producción. Si no se provee confianza de IA,
    se consulta al servicio de IA para auto-clasificar la novedad según la descripción.
    """
    if not evento.id:
        evento.id = f"EVT-{uuid.uuid4().hex[:6].upper()}"

    # Clasificar con IA si el evento es INTERRUPCION y tiene mensaje pero no fue clasificado
    if evento.tipo_evento == TipoEvento.INTERRUPCION and evento.mensaje and not evento.categoria:
        res_ia = ia_service.clasificar(mensaje=evento.mensaje)
        evento.categoria = res_ia.categoria
        evento.descripcion = res_ia.descripcion
        evento.confianza_ia = res_ia.confianza

    return sheets_service.registrar_evento(evento)


@router.post("/sync/{evento_id}")
def forzar_sincronizacion(evento_id: str):
    """
    Fuerza el reintento de sincronización de un evento específico con Google Sheets.
    """
    evento = sheets_service.obtener_por_id(evento_id)
    if not evento:
        raise HTTPException(status_code=404, detail="Evento no encontrado.")
    ok = sheets_service._sincronizar_google_sheets(evento)
    return {"id": evento_id, "sincronizado": ok}
