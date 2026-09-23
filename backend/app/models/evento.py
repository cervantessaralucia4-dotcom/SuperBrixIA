from enum import Enum
from typing import Optional, List
from datetime import datetime, date, time
from pydantic import BaseModel, Field

# FASE 5 — TIPOS DE EVENTOS
class TipoEvento(str, Enum):
    INICIO_LABOR = "INICIO_LABOR"
    INTERRUPCION = "INTERRUPCION"
    REANUDACION = "REANUDACION"
    FIN_LABOR = "FIN_LABOR"

# FASE 6 — CATEGORÍAS OFICIALES DE IA
class CategoriaIA(str, Enum):
    PRODUCCION_ACTIVA = "Producción Activa"
    ALISTAMIENTO_PREPARACION = "Alistamiento y Preparación (Setup)"
    ESPERA_MATERIALES = "Espera de Materiales / Logística"
    FALLA_TECNICA = "Falla Técnica / Mantenimiento"
    CALIDAD_APROBACION = "Calidad y Aprobación"
    INSTRUCCIONES_COORDINACION = "Instrucciones / Coordinación"

# FASE 4 — MODELOS DE DATOS
class Operario(BaseModel):
    id: str
    nombre: str
    codigo: str
    rol: str

class OrdenProduccion(BaseModel):
    id: str
    codigo: str
    descripcion: str
    estado: str

class Maquina(BaseModel):
    id: str
    nombre: str
    tipo: str
    estado: str

class Evento(BaseModel):
    id: str
    fecha: str
    hora_inicio: str
    hora_fin: Optional[str] = None
    operario_id: str
    orden_id: str
    maquina_id: str
    tipo_evento: TipoEvento
    mensaje: Optional[str] = None
    categoria: Optional[CategoriaIA] = None
    descripcion: Optional[str] = None
    confianza_ia: Optional[float] = None
    duracion_minutos: Optional[int] = None
    estado: str = "PROCESADO"

# FASE 7 - Modelos para Endpoint de Clasificación IA
class MensajeClasificacion(BaseModel):
    mensaje: str

class ResultadoClasificacionIA(BaseModel):
    categoria: CategoriaIA
    descripcion: str
    confianza: float
