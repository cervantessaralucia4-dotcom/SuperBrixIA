"""
Módulo de rutas de la API de SuperBrix IA.
"""
from .salud import router as salud_router
from .ia import router as ia_router
from .eventos import router as eventos_router

__all__ = ["salud_router", "ia_router", "eventos_router"]
