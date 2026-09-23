import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config.settings import settings
from app.routes import salud_router, ia_router, eventos_router

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("superbrix-ia")

# Inicialización de la aplicación FastAPI con los metadatos requeridos
app = FastAPI(
    title="SuperBrixIA API",
    version="1.0.0",
    description="Backend y orquestador para SuperBrix IA: clasificación de novedades operativas con IA y sincronización con Google Sheets.",
    docs_url="/docs",
    redoc_url="/redoc"
)

# Configuración de CORS para desarrollo y consumo desde Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def root():
    """Endpoint raíz requerido para verificación del backend."""
    return {
        "proyecto": "SuperBrixIA",
        "mensaje": "Backend funcionando correctamente",
        "version": "1.0.0"
    }


@app.get("/api/salud")
def api_salud():
    """Endpoint de salud del backend requerido."""
    return {
        "estado": "ok",
        "mensaje": "SuperBrixIA Backend funcionando"
    }


# Inclusión de rutas modulares adicionales
app.include_router(salud_router)
app.include_router(ia_router, prefix=settings.API_V1_STR)
app.include_router(eventos_router, prefix=settings.API_V1_STR)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host=settings.HOST, port=settings.PORT, reload=True)
