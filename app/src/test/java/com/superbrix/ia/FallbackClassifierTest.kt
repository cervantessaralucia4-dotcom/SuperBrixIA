package com.superbrix.ia

import com.superbrix.ia.core.ai.FallbackClassifier
import com.superbrix.ia.data.model.IACategorias
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para el clasificador heurístico de respaldo industrial de BRIX360.
 *
 * Valida:
 * 1. Normalización de tildes, signos y mayúsculas.
 * 2. Prioridad estricta y absoluta a Salud y Seguridad sobre cualquier otra categoría.
 * 3. Cobertura determinista de las 8 categorías operativas oficiales.
 */
class FallbackClassifierTest {

    @Test
    fun normalize_eliminaTildesYCaracteresEspeciales() {
        val entrada = "¡ATENCIÓN! La máquina se sobrecalentó y corté el cable."
        val normalizado = FallbackClassifier.normalize(entrada)
        assertEquals("atencion la maquina se sobrecalento y corte el cable", normalizado)
    }

    @Test
    fun saludYSeguridad_tienePrioridadAbsolutaSobreFallaTecnica() {
        // Caso crítico: incluye falla técnica ("se fue la luz") pero también lesión humana ("me corté")
        val mensaje = "Se fue la luz repentinamente y me corté la mano con la fresa"
        val resultado = FallbackClassifier.clasificar(mensaje)

        assertEquals(
            "Una novedad con lesión o riesgo humano DEBE ser clasificada como Salud y Seguridad",
            IACategorias.SALUD_SEGURIDAD,
            resultado.categoria
        )
        assertTrue(resultado.confianza >= 0.85)
        assertTrue(resultado.esFallback)
    }

    @Test
    fun saludYSeguridad_detectaAccidentesHeridasYQuimicos() {
        val casos = listOf(
            "Accidente en prensa, operario herido",
            "Cayó una esquirla en el ojo, solicito primeros auxilios",
            "Derrame de químico peligroso en pasillo",
            "El operario se fracturó el brazo",
            "Humo tóxico y principio de conato de incendio"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.SALUD_SEGURIDAD, res.categoria)
        }
    }

    @Test
    fun fallaTecnica_detectaAveriasYMantenimiento() {
        val casos = listOf(
            "El husillo de la fresadora está recalentado",
            "Vibración extraña en el motor del torno CNC",
            "Hubo un cortocircuito en el tablero eléctrico",
            "Se rompió la banda de transmisión mecánica"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.FALLA_TECNICA, res.categoria)
        }
    }

    @Test
    fun logisticaMateriales_detectaFaltaDeInsumos() {
        val casos = listOf(
            "No ha llegado la lámina de acero inoxidable",
            "Esperando que el montacargas traiga el pallet",
            "Faltan tornillos y pernos en almacén",
            "Desabastecimiento de materia prima para la orden"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.LOGISTICA_MATERIALES, res.categoria)
        }
    }

    @Test
    fun calidad_detectaDefectosYNoConformidades() {
        val casos = listOf(
            "La pieza salió fuera de tolerancia geométrica",
            "Rayón superficial en la pintura de la tolva",
            "Rechazado por metrología en control de calidad",
            "La rebaba excede los límites de rugosidad permitidos"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.CALIDAD, res.categoria)
        }
    }

    @Test
    fun setup_detectaMontajeYCalibracion() {
        val casos = listOf(
            "Montando mordaza y centrando herramienta",
            "Alistamiento y preparación de la máquina para nuevo lote",
            "Calibrando el cero pieza en el CNC",
            "Puesta a punto de los parámetros de corte"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.SETUP, res.categoria)
        }
    }

    @Test
    fun coordinacion_detectaEsperasOperativas() {
        val casos = listOf(
            "Esperando autorización del supervisor para continuar",
            "Reunión de 5 minutos de inicio de turno",
            "Revisando planos con el líder de manufactura",
            "Validando cambio de turno con el relevo"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.COORDINACION, res.categoria)
        }
    }

    @Test
    fun produccionActiva_detectaOperacionNormal() {
        val casos = listOf(
            "Mecanizado continuo de la orden",
            "Soldando cordones en estructura de tolva",
            "Operación en marcha según cronograma",
            "Producción activa a buen ritmo"
        )

        for (caso in casos) {
            val res = FallbackClassifier.clasificar(caso)
            assertEquals("Fallo para caso: '$caso'", IACategorias.PRODUCCION_ACTIVA, res.categoria)
        }
    }

    @Test
    fun fraseDesconocida_asignaOtrosConConfianzaBase() {
        val mensaje = "xyz abc prueba indefinida 12345"
        val res = FallbackClassifier.clasificar(mensaje)
        assertEquals(IACategorias.OTROS, res.categoria)
        assertEquals(0.60, res.confianza, 0.01)
    }
}
