package com.marents.app

/**
 * Modelo de datos para una categoría/género
 * Usado en la pantalla de selección de categorías
 */
data class CategoriaItem(
    val id: Int,
    val nombre: String,
    val iconoResId: Int, // Referencia al drawable del icono
    val colorFondo: String // Color de fondo del círculo (hex)
)
