package com.marents.app.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.LoginRequest
import com.marents.app.LoginResponse
import com.marents.app.RetrofitClient
import com.marents.app.ui.admin.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun login(email: String, password: String) {
        Log.d("LoginViewModel", "Intentando login con email: $email")
        
        if (email.isBlank() || password.isBlank()) {
            Log.d("LoginViewModel", "Email o contraseña vacíos")
            _error.value = "Por favor ingrese email y contraseña"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            Log.d("LoginViewModel", "Usando datos simulados para login")

            try {
                // Simular delay de red
                kotlinx.coroutines.delay(1000)
                
                // Validar credenciales simuladas
                if (email == "admin@marents.com" && password == "admin123") {
                    val adminUser = User(
                        id = 1,
                        name = "Administrador",
                        email = "admin@marents.com",
                        role = "admin",
                        status = "Activo",
                        createdAt = "2024-01-15"
                    )
                    Log.d("LoginViewModel", "Login exitoso como admin: $adminUser")
                    _user.value = adminUser
                    _loginSuccess.value = true
                } else if (email == "cliente@marents.com" && password == "cliente123") {
                    val clienteUser = User(
                        id = 2,
                        name = "Cliente",
                        email = "cliente@marents.com",
                        role = "cliente",
                        status = "Activo",
                        createdAt = "2024-01-15"
                    )
                    Log.d("LoginViewModel", "Login exitoso como cliente: $clienteUser")
                    _user.value = clienteUser
                    _loginSuccess.value = true
                } else {
                    Log.d("LoginViewModel", "Credenciales incorrectas")
                    _error.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción en login", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}
