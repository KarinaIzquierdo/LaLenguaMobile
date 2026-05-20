package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarUsuarios() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getUsuarios().execute()
                }
                
                if (response.isSuccessful) {
                    val usuarios = response.body() ?: emptyList()
                    _users.value = usuarios
                } else {
                    _error.value = "Error al cargar usuarios: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                // Si falla la API, mostrar lista vacía
                _users.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarUsuario(userId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarUsuario(userId).execute()
                }

                if (response.isSuccessful) {
                    // Eliminar de la lista local solo si la API tuvo éxito
                    val usuariosActuales = _users.value?.toMutableList() ?: mutableListOf()
                    usuariosActuales.removeAll { it.id == userId }
                    _users.value = usuariosActuales
                } else {
                    when (response.code()) {
                        403 -> _error.value = "No se puede eliminar el administrador principal"
                        404 -> _error.value = "Usuario no encontrado"
                        else -> _error.value = "Error al eliminar: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión al eliminar: ${e.message}"
            }
        }
    }

    fun actualizarUsuario(userId: Int, nombres: String, apellidos: String, email: String, documento: String?, celular: String?, rol: String, password: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarUsuario(userId, nombres, apellidos, email, documento, celular, rol, password).execute()
                }

                if (response.isSuccessful) {
                    // Recargar la lista de usuarios
                    cargarUsuarios()
                } else {
                    when (response.code()) {
                        422 -> _error.value = "Error de validación en los datos"
                        404 -> _error.value = "Usuario no encontrado"
                        else -> _error.value = "Error al actualizar: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión al actualizar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
