package com.marents.app.ui.admin.mapper

import com.marents.app.network.dto.AdminStatsResponse
import com.marents.app.network.dto.PedidoResumenResponse
import com.marents.app.ui.admin.model.AdminStats
import com.marents.app.ui.admin.model.PedidoReciente
import java.text.NumberFormat
import java.util.Locale

object AdminMapper {
    fun toAdminStats(response: AdminStatsResponse): AdminStats {
        return AdminStats(
            pedidosPendientes = response.pendientes,
            pedidosCompletados = response.completados,
            totalVentas = response.ventas,
            stockBajo = response.stock_bajo
        )
    }

    fun toPedidosRecientes(response: AdminStatsResponse): List<PedidoReciente> {
        return response.pedidos_recientes
            ?.take(5)
            ?.map { it.toPedidoReciente() }
            ?: emptyList()
    }

    private fun PedidoResumenResponse.toPedidoReciente(): PedidoReciente {
        return PedidoReciente(
            id = "#$id",
            cliente = cliente,
            total = "$${NumberFormat.getInstance(Locale("es", "CO")).format(total)}",
            estado = estado
        )
    }
}
