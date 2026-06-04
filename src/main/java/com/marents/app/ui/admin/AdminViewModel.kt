package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import kotlinx.coroutines.launch

data class AdminStats(
    val pedidosPendientes: Int = 0,
    val pedidosCompletados: Int = 0,
    val totalVentas: Int = 0,
    val stockBajo: Int = 0
)

class AdminViewModel : ViewModel() {

    private val _stats = MutableLiveData<AdminStats>()
    val stats: LiveData<AdminStats> = _stats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simular datos mientras creamos los endpoints reales
                // TODO: Reemplazar con llamadas reales a la API
                val statsMock = AdminStats(
                    pedidosPendientes = 5,
                    pedidosCompletados = 23,
                    totalVentas = 45,
                    stockBajo = 8
                )
                
                _stats.value = statsMock
                
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
