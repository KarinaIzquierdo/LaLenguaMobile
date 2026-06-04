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
    @SerializedName("documento")
    val documento: String? = null,
    @SerializedName("celular")
    val celular: String? = null,
    @SerializedName("rol")
    val rol: String? = null
)

// Request para registro
data class RegisterRequest(
    @SerializedName("nombres")
    val nombres: String,
    @SerializedName("apellidos")
    val apellidos: String,
    @SerializedName("documento")
    val documento: String,
    @SerializedName("celular")
    val celular: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// Response de registro
data class RegisterResponse(
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)

// Modelos para el Carrito
data class CartItem(
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("producto_id")
    val productoId: Int,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("precio")
    val precio: String,
    @SerializedName("talla")
    val talla: String,
    @SerializedName("cantidad")
    val cantidad: Int
)

data class AddToCartRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("producto_id")
    val productoId: Int,
    @SerializedName("talla")
    val talla: String,
    @SerializedName("cantidad")
    val cantidad: Int = 1
)

data class CartResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)
