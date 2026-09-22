package com.superbrix.ia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EventoUiState(
    val eventosList: List<Evento> = emptyList(),
    val isAnalyzing: Boolean = false,
    val ultimoEventoReportado: Evento? = null,
    val eventoSeleccionado: Evento? = null
)

class EventoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventoUiState())
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    init {
        loadMockEvents()
    }

    private fun loadMockEvents() {
        val initialEvents = listOf(
            Evento(
                id = "EVT-1001",
                fecha = "2026-09-22",
                horaInicio = "10:00 AM",
                horaFin = "10:30 AM",
                operario = "Sara",
                ordenProduccion = "OP-001",
                maquina = "Fresadora 01",
                tipoEvento = "Inicio de labor",
                descripcion = "Inicio de ciclo de mecanizado en Fresadora 01",
                categoria = IACategorias.PRODUCCION_ACTIVA,
                duracionMinutos = 30,
                confianzaIA = "98%"
            ),
            Evento(
                id = "EVT-1002",
                fecha = "2026-09-22",
                horaInicio = "10:32 AM",
                horaFin = "10:47 AM",
                operario = "Sara",
                ordenProduccion = "OP-001",
                maquina = "Fresadora 01",
                tipoEvento = "Interrupción",
                descripcion = "Paré la fresadora porque estoy esperando la broca de 1/2 pulgada.",
                categoria = IACategorias.LOGISTICA_MATERIALES,
                duracionMinutos = 15,
                confianzaIA = "95%"
            )
        )
        _uiState.update { it.copy(eventosList = initialEvents) }
    }

    fun reportarNovedad(
        descripcionText: String,
        ordenCodigo: String = "OP-001",
        maquinaNombre: String = "Fresadora 01",
        onAnalyzed: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }

            // Simular tiempo de procesamiento de IA (1.5 segundos)
            delay(1500)

            val horaActual = getCurrentTime()
            val textLower = descripcionText.lowercase()

            val categoriaClassificada = when {
                textLower.contains("broca") || textLower.contains("material") || textLower.contains("esperan") || textLower.contains("insumo") -> IACategorias.LOGISTICA_MATERIALES
                textLower.contains("falla") || textLower.contains("ruido") || textLower.contains("mecan") || textLower.contains("aceite") || textLower.contains("motor") -> IACategorias.FALLA_TECNICA
                textLower.contains("setup") || textLower.contains("ajuste") || textLower.contains("calibra") -> IACategorias.SETUP
                textLower.contains("calidad") || textLower.contains("revis") || textLower.contains("medida") -> IACategorias.CALIDAD
                textLower.contains("instrucc") || textLower.contains("supervis") || textLower.contains("reun") -> IACategorias.COORDINACION
                else -> IACategorias.LOGISTICA_MATERIALES
            }

            val nuevoEvento = Evento(
                id = "EVT-${(1000..9999).random()}",
                fecha = getCurrentDate(),
                horaInicio = horaActual,
                operario = "Sara",
                ordenProduccion = ordenCodigo,
                maquina = maquinaNombre,
                tipoEvento = "Interrupción",
                descripcion = if (descripcionText.isBlank()) "Paré la fresadora porque estoy esperando la broca de 1/2 pulgada." else descripcionText,
                categoria = categoriaClassificada,
                duracionMinutos = 15,
                confianzaIA = "94%"
            )

            _uiState.update { state ->
                state.copy(
                    isAnalyzing = false,
                    ultimoEventoReportado = nuevoEvento,
                    eventosList = listOf(nuevoEvento) + state.eventosList
                )
            }

            onAnalyzed()
        }
    }

    fun seleccionarEvento(evento: Evento) {
        _uiState.update { it.copy(eventoSeleccionado = evento) }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
