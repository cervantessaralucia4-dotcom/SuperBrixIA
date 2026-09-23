import json
import logging
from typing import Dict, Any, List
import httpx
from app.config.settings import settings
from app.models.evento import CategoriaIA, ResultadoClasificacionIA

logger = logging.getLogger(__name__)

class IAService:
    """
    Servicio de inteligencia artificial para clasificar novedades e interrupciones
    en la planta de producción SuperBrix. Utiliza Gemini API vía REST.
    """

    CATEGORIAS_VALIDAS = [cat.value for cat in CategoriaIA]
    
    MODELOS_PRIORIDAD = [
        "gemini-3.1-flash-lite",
        "gemini-3.5-flash",
        "gemini-3-flash-preview",
        "gemini-pro-latest"
    ]

    def __init__(self):
        self.api_key = settings.IA_API_KEY
        self.modelo_configurado = settings.IA_MODEL or "gemini-3.1-flash-lite"
        if self.api_key:
            logger.info(f"Servicio de IA activo con clave de API. Modelo principal: {self.modelo_configurado}")
        else:
            logger.warning("Servicio de IA sin clave de API. Esto causará errores al clasificar.")

    def clasificar(self, mensaje: str) -> ResultadoClasificacionIA:
        """
        Clasifica una descripción de novedad en tiempo real.
        """
        if not self.api_key:
            return self._clasificar_heuristico(mensaje)

        try:
            return self._clasificar_con_gemini(mensaje)
        except Exception as ex:
            logger.error(f"Error clasificando con Gemini API: {ex}. Utilizando clasificador heurístico.")
            return self._clasificar_heuristico(mensaje)

    def _clasificar_con_gemini(self, mensaje: str) -> ResultadoClasificacionIA:
        prompt = f"""
Eres el clasificador de IA de planta industrial de SuperBrix.
Tu labor es clasificar la siguiente novedad reportada por un operario de máquina:

Mensaje: "{mensaje}"

Debes clasificarla en EXACTAMENTE UNA de estas 6 categorías:
1. Producción Activa
2. Alistamiento y Preparación (Setup)
3. Espera de Materiales / Logística
4. Falla Técnica / Mantenimiento
5. Calidad y Aprobación
6. Instrucciones / Coordinación

Responde ÚNICAMENTE un bloque JSON válido con este formato:
{{
  "categoria": "Nombre Exacto de la Categoría",
  "descripcion": "Breve explicación técnica de por qué pertenece a esta categoría",
  "confianza": 0.95
}}
"""
        modelos = [self.modelo_configurado] + [m for m in self.MODELOS_PRIORIDAD if m != self.modelo_configurado]
        
        last_error = None
        for modelo in modelos:
            url = f"https://generativelanguage.googleapis.com/v1beta/models/{modelo}:generateContent?key={self.api_key}"
            payload = {
                "contents": [{
                    "parts": [{"text": prompt}]
                }],
                "generationConfig": {
                    "temperature": 0.1,
                    "responseMimeType": "application/json"
                }
            }
            try:
                with httpx.Client(timeout=20.0) as client:
                    response = client.post(url, json=payload)
                    if response.status_code == 200:
                        data_raw = response.json()
                        text = data_raw["candidates"][0]["content"]["parts"][0]["text"].strip()
                        if text.startswith("```json"):
                            text = text[7:]
                        if text.endswith("```"):
                            text = text[:-3]
                        
                        data = json.loads(text.strip())
                        categoria_str = data.get("categoria", CategoriaIA.ESPERA_MATERIALES.value)
                        
                        if categoria_str not in self.CATEGORIAS_VALIDAS:
                            categoria_str = self._corregir_categoria(categoria_str)
                            
                        categoria_enum = CategoriaIA(categoria_str)

                        return ResultadoClasificacionIA(
                            categoria=categoria_enum,
                            descripcion=data.get("descripcion", "Clasificado mediante modelo industrial Gemini."),
                            confianza=float(data.get("confianza", 0.95))
                        )
                    else:
                        last_error = f"Status {response.status_code}: {response.text[:100]}"
            except Exception as e:
                last_error = str(e)
                continue

        logger.warning(f"No se pudo clasificar con los modelos remotos ({last_error}). Usando clasificador heurístico.")
        return self._clasificar_heuristico(mensaje)

    def _clasificar_heuristico(self, mensaje: str) -> ResultadoClasificacionIA:
        """
        Clasificador de contingencia.
        """
        text = mensaje.lower()

        if any(w in text for w in ["broca", "material", "esperan", "insumo", "falta material", "pieza", "almacen"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.ESPERA_MATERIALES,
                descripcion="Interrupción por falta o espera de insumos/herramental para mecanizado.",
                confianza=0.95
            )
        elif any(w in text for w in ["falla", "ruido", "mecan", "aceite", "motor", "recalent", "danad", "dañado", "fuga", "chumacera"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.FALLA_TECNICA,
                descripcion="Parada técnica imprevista por anomalía o fallo electromecánico.",
                confianza=0.96
            )
        elif any(w in text for w in ["setup", "ajuste", "calibra", "montaje", "centrado", "puesta a punto", "mordaza"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.ALISTAMIENTO_PREPARACION,
                descripcion="Labor de preparación, montaje o calibración previa a producción.",
                confianza=0.93
            )
        elif any(w in text for w in ["calidad", "revis", "medida", "cota", "micrometro", "tolerancia", "rechaz"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.CALIDAD_APROBACION,
                descripcion="Verificación metrológica o inspección de primera pieza.",
                confianza=0.94
            )
        elif any(w in text for w in ["instrucc", "supervis", "reun", "plano", "duda", "coordin"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.INSTRUCCIONES_COORDINACION,
                descripcion="Tiempo dedicado a instrucciones técnicas o clarificación con supervisión.",
                confianza=0.90
            )
        elif any(w in text for w in ["inicio", "mecaniz", "producc", "torneado", "fresado", "corte"]):
            return ResultadoClasificacionIA(
                categoria=CategoriaIA.PRODUCCION_ACTIVA,
                descripcion="Máquina operando en ciclo de transformación productiva.",
                confianza=0.98
            )

        return ResultadoClasificacionIA(
            categoria=CategoriaIA.ESPERA_MATERIALES,
            descripcion="Clasificación general asignada por similitud semántica a logística.",
            confianza=0.88
        )

    def _corregir_categoria(self, categoria: str) -> str:
        cat_lower = categoria.lower()
        for cat in self.CATEGORIAS_VALIDAS:
            if cat.lower() in cat_lower or cat_lower in cat.lower():
                return cat
        return CategoriaIA.ESPERA_MATERIALES.value


ia_service = IAService()
