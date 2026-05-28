package com.marents.app.ui.admin.model

data class AdminStats(
    val pedidosPendientes: Int = 0,
    val pedidosCompletados: Int = 0,
    val totalVentas: Int = 0,
    val stockBajo: Int = 0
)

data class ProductoVendido(
    val nombre: String,
    val cantidad: Int
)

data class PedidoReciente(
    val id: String,
    val cliente: String,
    val total: String,
    val estado: String
)
