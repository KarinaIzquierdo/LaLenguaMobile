package com.marents.app.ui.productos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.Producto
import com.marents.app.RetrofitClient
import kotlinx.coroutines.launch

class ProductosViewModel : ViewModel() {

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> = _productos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var todosLosProductos: List<Producto> = emptyList()

    private val _paginaActual = MutableLiveData(1)
    val paginaActual: LiveData<Int> = _paginaActual

    private val _totalPaginas = MutableLiveData(1)
    val totalPaginas: LiveData<Int> = _totalPaginas

    private val itemsPorPagina = 10

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val call = RetrofitClient.apiService.getProductos()
                val response = call.execute()
                if (response.isSuccessful) {
                    todosLosProductos = response.body() ?: emptyList()
                    _totalPaginas.value = (todosLosProductos.size + itemsPorPagina - 1) / itemsPorPagina
                    aplicarPaginacion()
                    Log.d("ProductosViewModel", "Productos cargados: ${todosLosProductos.size}")
                } else {
                    _error.value = "Error al cargar productos: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("ProductosViewModel", "Error cargando productos", e)
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarProductos(query: String) {
        if (query.isBlank()) {
            aplicarPaginacion()
            return
        }

        val filtrados = todosLosProductos.filter { producto ->
            producto.modelo?.nombre?.contains(query, ignoreCase = true) == true ||
            producto.modelo?.categoria?.nombre?.contains(query, ignoreCase = true) == true
        }
        _productos.value = filtrados
    }

    fun cambiarPagina(pagina: Int) {
        val total = _totalPaginas.value ?: 1
        if (pagina in 1..total) {
            _paginaActual.value = pagina
            aplicarPaginacion()
        }
    }

    private fun aplicarPaginacion() {
        val pagina = _paginaActual.value ?: 1
        val inicio = (pagina - 1) * itemsPorPagina
        val fin = minOf(inicio + itemsPorPagina, todosLosProductos.size)
        
        if (inicio < todosLosProductos.size) {
            _productos.value = todosLosProductos.subList(inicio, fin)
        } else {
            _productos.value = emptyList()
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
