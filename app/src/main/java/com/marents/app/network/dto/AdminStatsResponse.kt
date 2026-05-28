package com.marents.app.network.dto

data class AdminStatsResponse(
    val pendientes: Int,
    val ventas: Int,
    val stock_bajo: Int,
    val completados: Int,
    val pedidos_recientes: List<PedidoResumenResponse>? = null
)

data class PedidoResumenResponse(
    val id: Int,
    val cliente: String,
    val total: Double,
    val estado: String
)
