package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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
                // Mostrar solo el usuario admin
                val usuariosMock = listOf(
                    User(
                        id = 1,
                        name = "Administrador",
                        email = "admin@marents.com",
                        role = "admin",
                        status = "Activo",
                        createdAt = "2024-01-15",
                        phone = "3001234567",
                        document = "12345678"
                    )
                )
                
                _users.value = usuariosMock
                
            } catch (e: Exception) {
                _error.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarUsuario(userId: Int) {
        viewModelScope.launch {
            try {
                val usuariosActuales = _users.value?.toMutableList() ?: mutableListOf()
                usuariosActuales.removeAll { it.id == userId }
                _users.value = usuariosActuales
                // TODO: Llamar a API para eliminar usuario
            } catch (e: Exception) {
                _error.value = "Error al eliminar usuario: ${e.message}"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
