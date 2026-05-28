package com.marents.app.ui.admin.model

data class AdminUiState(
    val isLoading: Boolean = false,
    val stats: AdminStats = AdminStats(),
    val productosVendidos: List<ProductoVendido> = emptyList(),
    val pedidosRecientes: List<PedidoReciente> = emptyList(),
    val error: String? = null
)
