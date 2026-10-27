package com.superbrix.ia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superbrix.ia.core.ai.FallbackClassifier
import com.superbrix.ia.core.notifications.PlantNotificationHelper
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.data.model.SyncStatus
import com.superbrix.ia.data.remote.SuperBrixApiClient
import com.superbrix.ia.data.remote.models.ClasificacionRequest
import com.superbrix.ia.data.repository.EventoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EventoUiState(
    val eventosList: List<Evento> = emptyList(),
    val isAnalyzing: Boolean = false,
    val ultimoEventoReportado: Evento? = null,
    val eventoSeleccionado: Evento? = null,
    val alertaSeguridadActiva: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel central de Eventos, Reportes y Auditoría de BRIX360.
 *
 * Implementa persistencia local en SQLite (EventoRepository),
 * clasificación con Backend FastAPI y Respaldo Heurístico de Alta Prioridad
 * a Salud y Seguridad, y notificaciones industriales push inmediatas.
 */
class EventoViewModel(application: Application) : AndroidViewModel(application) {

    private val eventoRepository = EventoRepository(application)

    private val _uiState = MutableStateFlow(EventoUiState())
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    init {
        cargarHistorialLocal()
    }

    fun cargarHistorialLocal() {
        viewModelScope.launch {
            val guardados = eventoRepository.getAllEventos()
            _uiState.update { it.copy(eventosList = guardados) }
        }
    }

    fun reportarNovedad(
        descripcionText: String,
        ordenCodigo: String = "OP-001",
        maquinaNombre: String = "Fresadora 01",
        operarioNombre: String = "Sara",
        fotoUri: String? = null,
        onAnalyzed: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, errorMessage = null) }

            val horaActual = getCurrentTime()
            val (duracionMinutosCalculada, duracionSegundosCalculada) = calcularDuraciones(horaActual)
            val finalDescripcion = if (descripcionText.isBlank()) "Paré la máquina" else descripcionText

            // 1. Clasificación con Backend / Fallback heurístico seguro
            val resultado = withContext(Dispatchers.IO) {
                clasificarNovedadConBackend(finalDescripcion)
            }

            val esSeguridad = resultado.categoria == IACategorias.SALUD_SEGURIDAD

            // Si es un evento crítico de seguridad, emitir alerta de planta inmediata
            if (esSeguridad) {
                PlantNotificationHelper.showSafetyAlert(
                    getApplication(),
                    "¡ALERTA DE SEGURIDAD EN PLANTA!",
                    "Operario $operarioNombre reportó en $maquinaNombre: ${resultado.descripcion}"
                )
            }

            val nuevoEvento = Evento(
                id = "EVT-${(1000..9999).random()}",
                fecha = getCurrentDate(),
                horaInicio = horaActual,
                horaFin = horaActual,
                operario = operarioNombre,
                ordenProduccion = ordenCodigo,
                maquina = maquinaNombre,
                tipoEvento = "Interrupción",
                descripcion = resultado.descripcion,
                categoria = resultado.categoria,
                categoriaOriginalIA = resultado.categoria,
                categoriaCorregida = false,
                duracionMinutos = 0,
                duracionSegundos = 0,
                confianzaIA = "${(resultado.confianza * 100).toInt()}%",
                syncStatus = SyncStatus.PENDIENTE,
                fotoUri = fotoUri,
                esNovedadSeguridad = esSeguridad
            )

            // 2. Persistencia en SQLite local y encolado en WorkManager
            val lastEvent = _uiState.value.eventosList.firstOrNull()
            if (lastEvent != null && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                val completedEvent = lastEvent.copy(
                    horaFin = horaActual,
                    duracionMinutos = duracionMinutosCalculada,
                    duracionSegundos = duracionSegundosCalculada
                )
                eventoRepository.updateEvento(completedEvent)
            }
            eventoRepository.insertEvento(nuevoEvento)

            // 3. Actualizar UI con la lista actualizada desde base de datos local
            val listaActualizada = eventoRepository.getAllEventos()

            _uiState.update { state ->
                state.copy(
                    isAnalyzing = false,
                    ultimoEventoReportado = nuevoEvento,
                    alertaSeguridadActiva = esSeguridad,
                    eventosList = listaActualizada
                )
            }

            onAnalyzed()
        }
    }

    /**
     * Permite al operario corregir la categoría inferida por la IA antes o después de guardar.
     */
    fun corregirCategoriaUltimoEvento(nuevaCategoria: String) {
        viewModelScope.launch {
            val ultimo = _uiState.value.ultimoEventoReportado ?: return@launch
            val corregido = eventoRepository.corregirCategoria(ultimo.id, nuevaCategoria) ?: return@launch

            if (nuevaCategoria == IACategorias.SALUD_SEGURIDAD) {
                PlantNotificationHelper.showSafetyAlert(
                    getApplication(),
                    "¡ALERTA DE SEGURIDAD EN PLANTA!",
                    "Categoría corregida a Salud y Seguridad: ${corregido.descripcion}"
                )
            }

            val listaActualizada = eventoRepository.getAllEventos()

            _uiState.update { state ->
                state.copy(
                    ultimoEventoReportado = corregido,
                    eventosList = listaActualizada,
                    alertaSeguridadActiva = (nuevaCategoria == IACategorias.SALUD_SEGURIDAD)
                )
            }
        }
    }

    /**
     * Permite anular un evento registrado con un motivo explícito.
     */
    fun anularEvento(eventoId: String, motivo: String) {
        viewModelScope.launch {
            val anulado = eventoRepository.anularEvento(eventoId, motivo)
            val listaActualizada = eventoRepository.getAllEventos()

            _uiState.update { state ->
                state.copy(
                    eventosList = listaActualizada,
                    eventoSeleccionado = if (state.eventoSeleccionado?.id == eventoId) anulado else state.eventoSeleccionado
                )
            }
        }
    }

    fun dismissAlertaSeguridad() {
        _uiState.update { it.copy(alertaSeguridadActiva = false) }
    }

    fun reportarEventoDirecto(
        ordenCodigo: String,
        maquinaNombre: String,
        tipoEvento: String,
        descripcion: String,
        categoria: String,
        operarioNombre: String = "Sara"
    ) {
        viewModelScope.launch {
            val horaActual = getCurrentTime()
            val (duracionMinutosCalculada, duracionSegundosCalculada) = calcularDuraciones(horaActual)

            val nuevoEvento = Evento(
                id = "EVT-${(1000..9999).random()}",
                fecha = getCurrentDate(),
                horaInicio = horaActual,
                horaFin = horaActual,
                operario = operarioNombre,
                ordenProduccion = ordenCodigo,
                maquina = maquinaNombre,
                tipoEvento = tipoEvento,
                descripcion = descripcion,
                categoria = categoria,
                categoriaOriginalIA = categoria,
                duracionMinutos = 0,
                duracionSegundos = 0,
                confianzaIA = "100%",
                syncStatus = SyncStatus.PENDIENTE
            )

            val lastEvent = _uiState.value.eventosList.firstOrNull()
            if (lastEvent != null && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                val completedEvent = lastEvent.copy(
                    horaFin = horaActual,
                    duracionMinutos = duracionMinutosCalculada,
                    duracionSegundos = duracionSegundosCalculada
                )
                eventoRepository.updateEvento(completedEvent)
            }
            eventoRepository.insertEvento(nuevoEvento)

            val listaActualizada = eventoRepository.getAllEventos()

            _uiState.update { state ->
                state.copy(
                    ultimoEventoReportado = nuevoEvento,
                    eventosList = listaActualizada
                )
            }
        }
    }

    fun sincronizarPendientes() {
        eventoRepository.triggerSync()
        // Refrescar estado local tras unos instantes
        viewModelScope.launch {
            cargarHistorialLocal()
        }
    }

    fun limpiarHistorial() {
        viewModelScope.launch {
            eventoRepository.clearAllEventos()
            _uiState.update { it.copy(eventosList = emptyList(), ultimoEventoReportado = null, eventoSeleccionado = null) }
        }
    }

    /**
     * Clasifica el mensaje mediante el Backend FastAPI seguro (sin exponer API key en la app).
     * Si el backend no responde o hay fallo de red, recurre al clasificador heurístico
     * con estricta prioridad a Salud y Seguridad.
     */
    private suspend fun clasificarNovedadConBackend(mensaje: String): com.superbrix.ia.core.ai.ClasificacionResult {
        return try {
            val response = SuperBrixApiClient.apiService.clasificarNovedad(ClasificacionRequest(mensaje))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                com.superbrix.ia.core.ai.ClasificacionResult(
                    categoria = body.categoria,
                    descripcion = body.descripcion,
                    confianza = body.confianza,
                    esFallback = false
                )
            } else {
                Log.w("IA_Backend", "Fallo HTTP ${response.code()}, usando clasificador de respaldo")
                FallbackClassifier.clasificar(mensaje)
            }
        } catch (e: Exception) {
            Log.e("IA_Backend", "Excepción conectando al backend: ${e.message}, usando fallback")
            FallbackClassifier.clasificar(mensaje)
        }
    }

    fun seleccionarEvento(evento: Evento) {
        _uiState.update { it.copy(eventoSeleccionado = evento) }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun calcularDuraciones(horaActual: String): Pair<Int, Int> {
        val lastEvent = _uiState.value.eventosList.firstOrNull() ?: return Pair(0, 0)
        return try {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            val d1 = sdf.parse(lastEvent.horaInicio)
            val d2 = sdf.parse(horaActual)
            if (d1 != null && d2 != null) {
                val diffMs = d2.time - d1.time
                val totalSeconds = (diffMs / 1000).coerceAtLeast(0).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                Pair(minutes, seconds)
            } else Pair(0, 0)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }
}
