package com.superbrix.ia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superbrix.ia.data.model.Maquina
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.data.model.OrdenProduccion
import com.superbrix.ia.data.repository.OperarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val operario: Operario = Operario(nombre = "Operario no asignado", area = "Sin área"),
    val ordenActiva: OrdenProduccion = OrdenProduccion(codigo = "OP-001", descripcion = "Orden activa"),
    val maquinaActiva: Maquina = Maquina(nombre = "Sin máquina asignada"),
    val isLaborActive: Boolean = false,
    val laborEstadoText: String = "Sin labor activa",
    val tipoOperacionActiva: String = "Producción",
    val areaActiva: String = "CNC",
    val interruptionStartTime: Long? = null
)

object DatosSuperBrix {
    val areasYOperaciones = mapOf(
        "CNC" to listOf("Mecanizado"),
        "Ensamble 1" to listOf("Soldadura", "Armado"),
        "Ensamble 2" to listOf("Soldadura", "Armado"),
        "Pintura" to listOf("Pintura")
    )
    val maquinasPorArea = mapOf(
        "CNC" to listOf("CNC 01", "CNC 02", "CNC 03", "Fresadora 01", "Fresadora 02", "Torno CNC"),
        "Ensamble 1" to listOf("Mesa de Soldadura A", "Mesa de Soldadura B", "Estación de Armado 1", "Estación de Armado 2"),
        "Ensamble 2" to listOf("Mesa de Soldadura C", "Mesa de Soldadura D", "Estación de Armado 3", "Estación de Armado 4"),
        "Pintura" to listOf("Cabina de Pintura", "Horno de Secado", "Zona de Preparación")
    )
    val ordenesProduccion = listOf("OP-001", "OP-002", "OP-003", "OP-4582", "OP-4583", "OP-4584")
}

/**
 * ViewModel del estado operativo principal y gestión del catálogo de operarios.
 * Persiste los cambios de operarios en SQLite local mediante OperarioRepository.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val operarioRepository = OperarioRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _operariosRegistrados = MutableStateFlow<List<Operario>>(emptyList())
    val operariosRegistrados: StateFlow<List<Operario>> = _operariosRegistrados.asStateFlow()

    init {
        loadOperarios()
    }

    private fun loadOperarios() {
        viewModelScope.launch {
            val ops = operarioRepository.getAllOperarios()
            _operariosRegistrados.value = ops
            if (ops.isNotEmpty()) {
                setOperarioActivo(ops.first())
            }
        }
    }

    fun agregarOperario(nombre: String, codigo: String, area: String) {
        val nuevoOperario = Operario(
            id = System.currentTimeMillis().toString(),
            nombre = nombre,
            codigo = codigo,
            rol = "Operario",
            area = area,
            centroTrabajo = "CT-$area"
        )
        viewModelScope.launch {
            operarioRepository.insertOperario(nuevoOperario)
            val updated = operarioRepository.getAllOperarios()
            _operariosRegistrados.value = updated
        }
    }

    fun editarOperario(id: String, nombre: String, codigo: String, area: String) {
        val operarioModificado = Operario(
            id = id,
            nombre = nombre,
            codigo = codigo,
            rol = "Operario",
            area = area,
            centroTrabajo = "CT-$area"
        )
        viewModelScope.launch {
            operarioRepository.insertOperario(operarioModificado)
            val updated = operarioRepository.getAllOperarios()
            _operariosRegistrados.value = updated
            if (_uiState.value.operario.id == id) {
                setOperarioActivo(operarioModificado)
            }
        }
    }

    fun eliminarOperario(operarioId: String) {
        viewModelScope.launch {
            operarioRepository.deleteOperario(operarioId)
            val updated = operarioRepository.getAllOperarios()
            _operariosRegistrados.value = updated
            if (_uiState.value.operario.id == operarioId && updated.isNotEmpty()) {
                setOperarioActivo(updated.first())
            }
        }
    }

    fun setOperarioActivo(operario: Operario) {
        val defaultMaquinaStr = DatosSuperBrix.maquinasPorArea[operario.area]?.firstOrNull() ?: "Sin máquina"
        val defaultOperacion = DatosSuperBrix.areasYOperaciones[operario.area]?.firstOrNull() ?: "Producción"
        _uiState.update { it.copy(
            operario = operario,
            areaActiva = operario.area,
            maquinaActiva = Maquina(nombre = defaultMaquinaStr),
            tipoOperacionActiva = defaultOperacion
        ) }
    }

    fun startLabor(ordenCodigo: String, maquinaNombre: String, tipoOperacion: String, area: String) {
        _uiState.update { current ->
            current.copy(
                ordenActiva = current.ordenActiva.copy(codigo = ordenCodigo),
                maquinaActiva = current.maquinaActiva.copy(nombre = maquinaNombre),
                areaActiva = area,
                isLaborActive = true,
                laborEstadoText = "Labor activa: $tipoOperacion en $area",
                tipoOperacionActiva = tipoOperacion
            )
        }
    }

    fun markInterruption() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = false,
                laborEstadoText = "Interrupción registrada",
                interruptionStartTime = System.currentTimeMillis()
            )
        }
    }

    fun resumeLabor() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = true,
                laborEstadoText = "Labor activa: ${current.tipoOperacionActiva} en ${current.areaActiva}",
                interruptionStartTime = null
            )
        }
    }

    fun finishLabor() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = false,
                laborEstadoText = "Labor finalizada",
                interruptionStartTime = null
            )
        }
    }

    fun startQuickScheduledLabor() {
        startLabor(
            ordenCodigo = _uiState.value.ordenActiva.codigo,
            maquinaNombre = _uiState.value.maquinaActiva.nombre,
            tipoOperacion = _uiState.value.tipoOperacionActiva,
            area = _uiState.value.areaActiva
        )
    }
}
