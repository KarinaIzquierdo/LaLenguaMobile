package com.marents.app.ui.productos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.Variacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Modelo UI para mostrar en la lista
data class ProductoUI(
    val id: Int,
    val nombre: String,
    val precio: String,
    val categoria: String,
    val subcategoria: String,
    val tallas: List<String>,
    val imagenUrl: String? = null,
    val esFavorito: Boolean = false
)

class ProductosCategoriaViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoUI>>(emptyList())
    val productos: StateFlow<List<ProductoUI>> = _productos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarProductosPorCategoria(categoria: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getProductosPorCategoria(categoria).execute()
                }

                if (response.isSuccessful) {
                    val productosApi = response.body() ?: emptyList()
                    Log.d("ProductosCategoria", "Productos recibidos: ${productosApi.size}")

                    // Transformar productos de API a formato UI
                    val productosUi = productosApi.map { producto ->
                        val variaciones = producto.variaciones ?: emptyList()
                        val primeraVariacion = variaciones.firstOrNull()

                        // Usar tallas del campo 'tallas' de la API si está disponible
                        val tallas = if (producto.tallas != null && producto.tallas.isNotEmpty()) {
                            producto.tallas
                        } else {
                            // Fallback: extraer de variaciones
                            variaciones.mapNotNull { it.talla?.numero?.toString() }.distinct().sorted()
                        }

                        // Calcular precio promedio
                        val precioPromedio = if (variaciones.isNotEmpty()) {
                            val precios = variaciones.mapNotNull { it.precio?.toDoubleOrNull() }
                            if (precios.isNotEmpty()) {
                                "\$${"%.2f".format(precios.average())}"
                            } else "\$0"
                        } else "\$0"

                        ProductoUI(
                            id = producto.id ?: 0,
                            nombre = producto.modelo?.nombre ?: "Sin nombre",
                            precio = precioPromedio,
                            categoria = producto.modelo?.categoria?.nombre ?: categoria,
                            subcategoria = variaciones.firstOrNull()?.colorPrimario?.nombre ?: "General",
                            tallas = tallas.ifEmpty { listOf("N/A") },
                            imagenUrl = producto.imagen,
                            esFavorito = false
                        )
                    }

                    _productos.value = productosUi
                } else {
                    _error.value = "Error al cargar productos: ${response.code()}"
                    Log.e("ProductosCategoria", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("ProductosCategoria", "Error de conexión", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorito(producto: ProductoUI) {
        val productosActualizados = _productos.value.map {
            if (it.id == producto.id) {
                it.copy(esFavorito = !it.esFavorito)
            } else {
                it
            }
        }
        _productos.value = productosActualizados
    }

    fun limpiarError() {
        _error.value = null
    }
}
