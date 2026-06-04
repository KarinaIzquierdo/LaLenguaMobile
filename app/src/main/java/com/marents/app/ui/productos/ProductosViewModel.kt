package com.marents.app.ui.productos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.Producto
import com.marents.app.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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
                Log.d("ProductosViewModel", "Iniciando carga de productos...")
                
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getProductos().execute()
                }
                
                Log.d("ProductosViewModel", "Respuesta recibida: ${response.code()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ProductosViewModel", "Body recibido: ${body?.size} productos")
                    if (body != null) {
                        todosLosProductos = body
                        Log.d("ProductosViewModel", "Primer producto: ${body.firstOrNull()?.modelo?.nombre}")
                    } else {
                        todosLosProductos = emptyList()
                    }
                    _totalPaginas.value = (todosLosProductos.size + itemsPorPagina - 1) / itemsPorPagina
                    aplicarPaginacion()
                    Log.d("ProductosViewModel", "Productos cargados: ${todosLosProductos.size}")
                } else {
                    _error.value = "Error al cargar productos: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("ProductosViewModel", "Error: ${response.code()}")
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

    fun ordenarPorStock(mayorAMenor: Boolean) {
        val listaActual = todosLosProductos.toMutableList()
        if (mayorAMenor) {
            listaActual.sortByDescending { producto ->
                producto.stock ?: producto.variaciones?.sumOf { it.stock ?: 0 } ?: 0
            }
        } else {
            listaActual.sortBy { producto ->
                producto.stock ?: producto.variaciones?.sumOf { it.stock ?: 0 } ?: 0
            }
        }
        todosLosProductos = listaActual
        _paginaActual.value = 1
        aplicarPaginacion()
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

    fun crearProducto(nombre: String, categoriaId: Int, color: String, talla: Double, precio: Double, costo: Double, stock: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.crearProducto(
                        nombre, categoriaId, color, talla, precio, costo, stock
                    ).execute()
                }
                if (response.isSuccessful) {
                    cargarProductos() // Recargar lista al tener éxito
                } else {
                    _error.value = "Error al crear: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearProductoFinal(
        nombre: String,
        categoriaNombre: String,
        color: String,
        talla: Double,
        precio: Double,
        costo: Double,
        stock: Int,
        imageFile: java.io.File?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoriaPart = categoriaNombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val colorPart = color.toRequestBody("text/plain".toMediaTypeOrNull())
                val tallaPart = talla.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val costoPart = costo.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagenPart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("imagen", it.name, requestFile)
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.crearProductoFinal(
                        nombrePart, categoriaPart, colorPart, tallaPart, precioPart, costoPart, stockPart, imagenPart
                    ).execute()
                }

                if (response.isSuccessful) {
                    cargarProductos()
                } else {
                    _error.value = "Error al crear: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearProductoConImagen(
        nombre: String,
        categoriaId: Int,
        color: String,
        talla: Double,
        precio: Double,
        costo: Double,
        stock: Int,
        imageFile: java.io.File?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoriaPart = categoriaId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val colorPart = color.toRequestBody("text/plain".toMediaTypeOrNull())
                val tallaPart = talla.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val costoPart = costo.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagenPart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("imagen", it.name, requestFile)
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.crearProductoConImagen(
                        nombrePart, categoriaPart, colorPart, tallaPart, precioPart, costoPart, stockPart, imagenPart
                    ).execute()
                }

                if (response.isSuccessful) {
                    cargarProductos()
                } else {
                    _error.value = "Error al crear: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Llamamos a la API con la ID directamente
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarProducto(id).execute()
                }
                if (response.isSuccessful) {
                    cargarProductos()
                } else {
                    _error.value = "Fallo: ${response.code()} - Revise su servidor"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión al servidor"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
