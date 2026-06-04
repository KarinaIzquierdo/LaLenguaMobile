package com.marents.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import com.marents.app.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    // Función de registro con todos los campos
    fun register(nombres: String, apellidos: String, documento: String, celular: String, email: String, password: String, passwordConfirmation: String) {
        // Validaciones locales
        if (nombres.isBlank() || apellidos.isBlank() || email.isBlank() || password.isBlank()) {
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
                // Llamada al endpoint de registro en hilo de IO
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.register(
                        com.marents.app.RegisterRequest(
                            nombres = nombres,
                            apellidos = apellidos,
                            documento = documento,
                            celular = celular,
                            email = email,
                            password = password
                        )
                    ).execute()
                }

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.user != null) {
                        _user.value = registerResponse.user
                        _registerSuccess.value = true
                    } else {
                        _error.value = registerResponse?.message ?: "Registro exitoso"
                        _registerSuccess.value = true
                    }
                } else {
                    when (response.code()) {
                        422 -> _error.value = "Datos inválidos o usuario ya existe"
                        409 -> _error.value = "El email ya está registrado"
                        else -> _error.value = "Error del servidor: ${response.code()}"
                    }
                }
            } catch (e: java.net.ConnectException) {
                _error.value = "No se pudo conectar al servidor. Verifica que:\n1. El servidor Laravel esté corriendo (php artisan serve)\n2. Estés usando el emulador de Android"
            } catch (e: java.net.SocketTimeoutException) {
                _error.value = "El servidor tardó demasiado en responder. Intenta de nuevo."
            } catch (e: Exception) {
                _error.value = "Error: ${e.javaClass.simpleName} - ${e.message}"
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
