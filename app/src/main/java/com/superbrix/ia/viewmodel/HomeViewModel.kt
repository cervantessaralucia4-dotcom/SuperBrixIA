package com.superbrix.ia.viewmodel

import androidx.lifecycle.ViewModel
import com.superbrix.ia.data.model.Maquina
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.data.model.OrdenProduccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val operario: Operario = Operario(nombre = "Sara (Ensamble 1)"),
    val ordenActiva: OrdenProduccion = OrdenProduccion(),
    val maquinaActiva: Maquina = Maquina(nombre = "Mesa de Soldadura A"),
    val isLaborActive: Boolean = false,
    val laborEstadoText: String = "Sin labor activa",
    val tipoOperacionActiva: String = "Producción",
    val areaActiva: String = "Ensamble 1",
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

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _operariosRegistrados = MutableStateFlow(
        listOf(
            Operario(id = "1", nombre = "Juan Pérez", codigo = "COD-100", rol = "Operario", area = "CNC"),
            Operario(id = "2", nombre = "Sara Gómez", codigo = "COD-101", rol = "Operario", area = "Ensamble 1")
        )
    )
    val operariosRegistrados: StateFlow<List<Operario>> = _operariosRegistrados.asStateFlow()

    fun agregarOperario(nombre: String, codigo: String, area: String) {
        val nuevoOperario = Operario(
            id = System.currentTimeMillis().toString(),
            nombre = nombre,
            codigo = codigo,
            rol = "Operario",
            area = area
        )
        _operariosRegistrados.update { current -> current + nuevoOperario }
    }

    fun editarOperario(id: String, nombre: String, codigo: String, area: String) {
        _operariosRegistrados.update { current ->
            current.map {
                if (it.id == id) it.copy(nombre = nombre, codigo = codigo, area = area) else it
            }
        }
    }

    fun eliminarOperario(operarioId: String) {
        _operariosRegistrados.update { current -> current.filter { it.id != operarioId } }
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
