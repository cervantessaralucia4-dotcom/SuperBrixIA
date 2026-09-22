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
    val operario: Operario = Operario(),
    val ordenActiva: OrdenProduccion = OrdenProduccion(),
    val maquinaActiva: Maquina = Maquina(),
    val isLaborActive: Boolean = false,
    val laborEstadoText: String = "Sin labor activa",
    val tipoOperacionActiva: String = "Producción"
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun startLabor(ordenCodigo: String, maquinaNombre: String, tipoOperacion: String) {
        _uiState.update { current ->
            current.copy(
                ordenActiva = current.ordenActiva.copy(codigo = ordenCodigo),
                maquinaActiva = current.maquinaActiva.copy(nombre = maquinaNombre),
                isLaborActive = true,
                laborEstadoText = "Labor activa: $tipoOperacion",
                tipoOperacionActiva = tipoOperacion
            )
        }
    }

    fun markInterruption() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = false,
                laborEstadoText = "Interrupción registrada"
            )
        }
    }

    fun resumeLabor() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = true,
                laborEstadoText = "Labor activa: ${current.tipoOperacionActiva}"
            )
        }
    }

    fun finishLabor() {
        _uiState.update { current ->
            current.copy(
                isLaborActive = false,
                laborEstadoText = "Labor finalizada"
            )
        }
    }

    fun startQuickScheduledLabor() {
        startLabor(
            ordenCodigo = _uiState.value.ordenActiva.codigo,
            maquinaNombre = _uiState.value.maquinaActiva.nombre,
            tipoOperacion = "Producción"
        )
    }
}
