package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.AdminStatsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

data class AdminStats(
    val pedidosPendientes: Int = 0,
    val pedidosCompletados: Int = 0,
    val totalVentas: Int = 0,
    val stockBajo: Int = 0
)

data class ProductoVendido(
    val nombre: String,
    val cantidad: Int
)

data class PedidoReciente(
    val id: String,
    val cliente: String,
    val total: String,
    val estado: String
)

class AdminViewModel : ViewModel() {

    private val _stats = MutableLiveData<AdminStats>()
    val stats: LiveData<AdminStats> = _stats

    private val _productosVendidos = MutableLiveData<List<ProductoVendido>>()
    val productosVendidos: LiveData<List<ProductoVendido>> = _productosVendidos

    private val _pedidosRecientes = MutableLiveData<List<PedidoReciente>>()
    val pedidosRecientes: LiveData<List<PedidoReciente>> = _pedidosRecientes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Llamada real a la API para obtener estadísticas usando enqueue para evitar bloqueos
                RetrofitClient.apiService.getAdminStats().enqueue(object : retrofit2.Callback<AdminStatsResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<AdminStatsResponse>,
                        response: retrofit2.Response<AdminStatsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val statsResponse = response.body()
                            if (statsResponse != null) {
                                _stats.postValue(AdminStats(
                                    pedidosPendientes = statsResponse.pendientes,
                                    pedidosCompletados = statsResponse.completados,
                                    totalVentas = statsResponse.ventas,
                                    stockBajo = statsResponse.stock_bajo
                                ))

                                // Mapear los pedidos reales de la respuesta
                                val pedidosReales = statsResponse.pedidos_recientes?.map { 
                                    PedidoReciente(
                                        id = "#${it.id}",
                                        cliente = it.cliente,
                                        total = "$${java.text.NumberFormat.getInstance(java.util.Locale("es", "CO")).format(it.total)}",
                                        estado = it.estado
                                    )
                                } ?: emptyList()
                                _pedidosRecientes.postValue(pedidosReales)
                            }
                        } else {
                            _error.postValue("Error del servidor: ${response.code()}")
                        }
                        _isLoading.postValue(false)
                    }

                    override fun onFailure(call: Call<AdminStatsResponse>, t: Throwable) {
                        _error.postValue("Error de conexión: ${t.message}")
                        _isLoading.postValue(false)
                    }
                })

                // El resto sigue igual (productos vendidos mock por ahora)
                val productosMock = emptyList<ProductoVendido>()
                _productosVendidos.postValue(productosMock)

            } catch (e: Exception) {
                _error.postValue("Excepción: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
