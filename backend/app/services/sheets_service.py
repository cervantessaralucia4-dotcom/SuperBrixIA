import uuid
import json
import logging
import urllib.request
import urllib.error
from datetime import datetime
from typing import List, Optional
from app.config.settings import settings
from app.models.evento import Evento, TipoEvento, CategoriaIA

logger = logging.getLogger(__name__)


class SheetsService:
    """
    Servicio de persistencia y sincronización con Google Sheets / Apps Script Webhook.
    """

    def __init__(self):
        self._eventos: List[Evento] = []

    def obtener_todos(
        self,
        operario_id: Optional[str] = None,
        maquina_id: Optional[str] = None,
        orden_id: Optional[str] = None,
        fecha: Optional[str] = None,
        categoria: Optional[str] = None,
        tipo_evento: Optional[str] = None
    ) -> List[Evento]:
        eventos = self._eventos
        if operario_id:
            eventos = [e for e in eventos if e.operario_id == operario_id]
        if maquina_id:
            eventos = [e for e in eventos if e.maquina_id == maquina_id]
        if orden_id:
            eventos = [e for e in eventos if e.orden_id == orden_id]
        if fecha:
            eventos = [e for e in eventos if e.fecha == fecha]
        if categoria:
            eventos = [e for e in eventos if e.categoria and e.categoria.value == categoria]
        if tipo_evento:
            eventos = [e for e in eventos if e.tipo_evento.value == tipo_evento]
        return eventos

    def obtener_por_id(self, evento_id: str) -> Optional[Evento]:
        for e in self._eventos:
            if e.id == evento_id:
                return e
        return None

    def registrar_evento(self, evento_in: Evento) -> Evento:
        # Guardar en memoria (al principio de la lista)
        self._eventos.insert(0, evento_in)

        # Intentar sincronizar con Google Sheets en segundo plano
        self._sincronizar_google_sheets(evento_in)

        return evento_in

    def _sincronizar_google_sheets(self, evento: Evento) -> bool:
        """
        Envía el registro a Google Apps Script Webhook si está configurado.
        """
        webhook_url = settings.GOOGLE_SCRIPT_URL
        if not webhook_url:
            logger.info(f"Registro {evento.id} guardado localmente (GOOGLE_SCRIPT_URL no configurado).")
            return False

        try:
            payload = json.dumps(evento.model_dump(mode='json')).encode("utf-8")
            req = urllib.request.Request(
                webhook_url,
                data=payload,
                headers={"Content-Type": "application/json"}
            )
            with urllib.request.urlopen(req, timeout=10) as response:
                if response.status in [200, 201, 302]:
                    logger.info(f"Registro {evento.id} sincronizado exitosamente con Google Sheets.")
                    return True
                else:
                    logger.warning(f"Error sincronizando con Apps Script: status {response.status}")
                    return False
        except Exception as e:
            logger.error(f"Excepción al conectar con Google Sheets Webhook: {e}")
            return False


sheets_service = SheetsService()
