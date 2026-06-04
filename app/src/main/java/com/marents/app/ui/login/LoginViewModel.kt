package com.marents.app.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.LoginRequest
import com.marents.app.LoginResponse
import com.marents.app.RetrofitClient
import com.marents.app.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

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
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Por favor ingrese email y contraseña"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.login(LoginRequest(email, password)).execute()
                }

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null) {
                        _user.value = loginResponse.user
                        _loginSuccess.value = true
                    } else {
                        _error.value = "Error en la respuesta del servidor"
                    }
                } else {
                    _error.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error de conexión", e)
                _error.value = "Error de conexión: ${e.message}"
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
