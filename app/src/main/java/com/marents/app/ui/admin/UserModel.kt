package com.marents.app.ui.admin

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val status: String, // "Activo" o "Inactivo"
    val createdAt: String,
    val phone: String? = null,
    val document: String? = null
)
