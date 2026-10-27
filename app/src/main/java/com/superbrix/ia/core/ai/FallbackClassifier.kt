package com.superbrix.ia.core.ai

import com.superbrix.ia.data.model.IACategorias
import java.text.Normalizer
import java.util.regex.Pattern

data class ClasificacionResult(
    val categoria: String,
    val descripcion: String,
    val confianza: Double,
    val esFallback: Boolean = false
)

/**
 * Clasificador heurístico industrial de respaldo para BRIX360.
 *
 * Se ejecuta de manera local y determinista cuando:
 * 1. No hay conexión de red con el backend.
 * 2. El modelo de IA remoto excede el timeout de 10 segundos.
 * 3. La respuesta del servidor es inválida o no parseable.
 *
 * REGLAS CRÍTICAS DE SEGURIDAD INDUSTRIAL:
 * 1. Prioridad absoluta: SALUD Y SEGURIDAD siempre se evalúa PRIMERO.
 *    (Ej: "se fue la luz y me corté" -> Salud y Seguridad).
 * 2. Normalización fonética y de tildes (á->a, é->e, etc.).
 * 3. Detección de palabras completas y frases compuestas.
 */
object FallbackClassifier {

    private val DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

    /**
     * Normaliza el texto a minúsculas, sin tildes ni signos de puntuación disruptivos.
     */
    fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
        val withoutAccents = DIACRITICS_PATTERN.matcher(decomposed).replaceAll("")
        // Reemplazar signos por espacios para separar palabras limpiamente
        return withoutAccents.replace(Regex("[^a-z0-9\\s]"), " ").replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Verifica si el texto normalizado contiene al menos una de las palabras o frases clave.
     */
    private fun containsAnyKeyword(normalizedText: String, keywords: List<String>): Boolean {
        return keywords.any { kw ->
            val pattern = Regex("(^|\\s)${Regex.escape(kw)}(\\s|$)")
            pattern.containsMatchIn(normalizedText)
        }
    }

    fun clasificar(mensajeOriginal: String): ClasificacionResult {
        val cleanMsg = normalize(mensajeOriginal)

        // 1. PRIORIDAD MÁXIMA: Salud y Seguridad (Accidentes, lesiones y emergencias humanas)
        val keywordsSaludSeguridad = listOf(
            "me corte", "cortada", "cortadura", "corte el dedo", "corte en la mano", "corte en",
            "corte profundo", "cortando el dedo", "accidente", "sangre", "sangrando", "herida",
            "herido", "ambulancia", "quemadura", "quemado", "queme", "fractura", "fracturado",
            "fracturo", "primeros auxilios", "golpe", "golpee", "trauma", "ojo", "esquirla en ojo",
            "asfixia", "caida", "cae", "atrapamiento", "aplastamiento", "emergencia medica",
            "botiquin", "enfermeria", "derrame", "quimico", "quimicos", "incendio", "fuego",
            "humo toxico", "gas", "evacuacion", "intoxicacion", "resbalon", "mareo", "desmayo",
            "lesion", "epp", "choque electrico", "electrocucion", "brazo", "pierna", "mano", "dedo"
        )
        if (containsAnyKeyword(cleanMsg, keywordsSaludSeguridad)) {
            return ClasificacionResult(
                categoria = IACategorias.SALUD_SEGURIDAD,
                descripcion = mensajeOriginal,
                confianza = 0.98,
                esFallback = true
            )
        }

        // 2. Alistamiento y Preparación (Setup - montaje, centrado, calibración de mordaza/máquina)
        val keywordsSetup = listOf(
            "setup", "alistamiento", "preparacion", "preparar", "calibracion", "calibrar",
            "calibrando", "mordaza", "ajuste de mordaza", "puesta a punto", "ajuste",
            "cero maquina", "cero pieza", "cambio de herramienta", "montaje", "montando",
            "desmontaje", "desmontando", "centrado", "centrando"
        )
        if (containsAnyKeyword(cleanMsg, keywordsSetup)) {
            return ClasificacionResult(
                categoria = IACategorias.SETUP,
                descripcion = mensajeOriginal,
                confianza = 0.90,
                esFallback = true
            )
        }

        // 3. Calidad y Aprobación
        val keywordsCalidad = listOf(
            "calidad", "inspector", "micrometro", "calibrador", "cota",
            "tolerancia", "medicion", "medir", "rechazo", "pieza defectuosa",
            "aprobacion", "inspeccion", "liberacion", "primera pieza", "defecto",
            "defectuoso", "defectuosa", "rayon", "rayones", "rebaba", "rugosidad",
            "no conformidad", "fuera de especificacion", "dimension", "desviacion",
            "porosidad", "grieta", "acabado superficial"
        )
        if (containsAnyKeyword(cleanMsg, keywordsCalidad)) {
            return ClasificacionResult(
                categoria = IACategorias.CALIDAD,
                descripcion = mensajeOriginal,
                confianza = 0.90,
                esFallback = true
            )
        }

        // 4. Falla Técnica / Mantenimiento
        val keywordsFallaTecnica = listOf(
            "falla", "fallo", "averia", "motor", "ruido extrano", "ruido", "humo",
            "recalentado", "recalento", "aceite", "fuga", "fuga de aceite", "hidraulico",
            "neumatico", "husillo", "alarma", "alarma cnc", "bloqueado", "atasco",
            "se trabo", "trabo", "se paro", "no enciende", "no arranca", "sensor",
            "reparacion", "mantenimiento", "corto circuito", "cortocircuito", "vibro", "vibracion",
            "electrico", "tablero electrico", "rompio", "se rompio", "rotura", "banda",
            "transmision", "mecanica"
        )
        if (containsAnyKeyword(cleanMsg, keywordsFallaTecnica)) {
            return ClasificacionResult(
                categoria = IACategorias.FALLA_TECNICA,
                descripcion = mensajeOriginal,
                confianza = 0.92,
                esFallback = true
            )
        }

        // 5. Espera de Materiales / Logística
        val keywordsLogistica = listOf(
            "material", "materia prima", "broca", "inserto", "insumo", "insumos",
            "almacen", "montacargas", "viruta", "retirar viruta", "falta material",
            "esperando material", "esperando pieza", "herramienta", "tornillos", "lamina",
            "perfil", "refrigerante", "solvente", "logistica", "desabastecimiento"
        )
        if (containsAnyKeyword(cleanMsg, keywordsLogistica)) {
            return ClasificacionResult(
                categoria = IACategorias.LOGISTICA_MATERIALES,
                descripcion = mensajeOriginal,
                confianza = 0.90,
                esFallback = true
            )
        }

        // 6. Instrucciones / Coordinación
        val keywordsCoordinacion = listOf(
            "supervisor", "jefe", "lider", "instruccion", "instrucciones", "reunion", "turno",
            "charla", "charla de seguridad", "coordinacion", "ingenieria", "aclaracion",
            "esperando instrucciones", "esperando orden", "cambio de turno", "relevo",
            "capacitacion", "induccion"
        )
        if (containsAnyKeyword(cleanMsg, keywordsCoordinacion)) {
            return ClasificacionResult(
                categoria = IACategorias.COORDINACION,
                descripcion = mensajeOriginal,
                confianza = 0.85,
                esFallback = true
            )
        }

        // 7. Producción Activa (ciclo de trabajo continuo)
        val keywordsProduccion = listOf(
            "mecanizado", "mecanizando", "torneado", "torneando", "fresado", "fresando",
            "soldadura", "soldando", "armado", "armando", "pintura", "pintando",
            "produccion", "produciendo", "operando", "en ciclo", "fabricacion", "corrida",
            "operacion", "en marcha", "cronograma"
        )
        if (containsAnyKeyword(cleanMsg, keywordsProduccion)) {
            return ClasificacionResult(
                categoria = IACategorias.PRODUCCION_ACTIVA,
                descripcion = mensajeOriginal,
                confianza = 0.80,
                esFallback = true
            )
        }

        // 8. Otros / Novedad Excepcional (interrupciones generales no técnicas)
        val keywordsOtros = listOf(
            "luz", "se fue la luz", "energia", "corte de energia", "bano", "banos",
            "personal", "permiso", "almuerzo", "pausa activa", "lluvia", "gotera",
            "internet", "red", "otro", "excepcional"
        )
        if (containsAnyKeyword(cleanMsg, keywordsOtros)) {
            return ClasificacionResult(
                categoria = IACategorias.OTROS,
                descripcion = mensajeOriginal,
                confianza = 0.85,
                esFallback = true
            )
        }

        // Predeterminado de seguridad industrial: Otros con confianza base
        return ClasificacionResult(
            categoria = IACategorias.OTROS,
            descripcion = mensajeOriginal,
            confianza = 0.60,
            esFallback = true
        )
    }
}
