package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.ui.admin.data.AdminRepository
import com.marents.app.ui.admin.mapper.AdminMapper
import com.marents.app.ui.admin.model.AdminUiState
import com.marents.app.ui.admin.model.ProductoVendido
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository(RetrofitClient.apiService)

    private val _uiState = MutableLiveData(AdminUiState())
    val uiState: LiveData<AdminUiState> = _uiState

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true, error = null)

            val result = repository.obtenerEstadisticas()

            result.onSuccess { statsResponse ->
                val productosMock = emptyList<ProductoVendido>()
                _uiState.value = AdminUiState(
                    isLoading = false,
                    stats = AdminMapper.toAdminStats(statsResponse),
                    productosVendidos = productosMock,
                    pedidosRecientes = AdminMapper.toPedidosRecientes(statsResponse)
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "Error de conexión: ${error.message}"
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value?.copy(error = null)
    }
}
