package com.marents.app

import com.google.gson.annotations.SerializedName

// Request para enviar al API
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// Response recibida del API
data class LoginResponse(
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("message")
    val message: String? = null
)

data class User(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("nombres")
    val nombres: String? = null,
    @SerializedName("apellidos")
    val apellidos: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("rol")
    val rol: String? = null
)
