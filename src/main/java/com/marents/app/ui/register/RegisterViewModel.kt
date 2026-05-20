package com.marents.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Modelo de datos para el registro
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

// Respuesta del registro
data class RegisterResponse(
    val user: User? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)

class RegisterViewModel : ViewModel() {

    // Estados de UI expuestos como StateFlow (reactivos)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Función de registro
    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        // Validaciones locales
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _error.value = "Todos los campos son obligatorios"
            return
        }

        if (password != passwordConfirmation) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        if (password.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Llamada a la API (usaremos el endpoint de login por ahora como placeholder)
                // TODO: Crear endpoint /register en Laravel
                val response = RetrofitClient.apiService.login(
                    com.marents.app.LoginRequest(email, password)
                ).execute()

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null) {
                        _user.value = loginResponse.user
                        _registerSuccess.value = true
                    } else {
                        _error.value = "Registro exitoso pero sin datos de usuario"
                    }
                } else {
                    when (response.code()) {
                        422 -> _error.value = "Datos inválidos o usuario ya existe"
                        else -> _error.value = "Error del servidor: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun resetRegisterSuccess() {
        _registerSuccess.value = false
    }
}
