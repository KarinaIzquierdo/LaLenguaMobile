package com.marents.app.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.marents.app.Producto
import com.marents.app.R

class ProductosAdminAdapter(
    private val onStockClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductosAdminAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_tabla, parent, false)
        return ProductoViewHolder(view, onStockClick)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductoViewHolder(
        itemView: View,
        private val onStockClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvTallas: TextView = itemView.findViewById(R.id.tvTallas)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val tvCosto: TextView = itemView.findViewById(R.id.tvCosto)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvGanancia: TextView = itemView.findViewById(R.id.tvGanancia)
        private val btnStock: Button = itemView.findViewById(R.id.btnStock)

        fun bind(producto: Producto) {
            // Categoría
            tvCategoria.text = producto.modelo?.categoria?.nombre ?: "Sin categoría"

            // Nombre
            tvNombre.text = producto.modelo?.nombre ?: "Sin nombre"

            // Variaciones (tallas)
            val variaciones = producto.variaciones
            val color = variaciones?.firstOrNull()?.colorPrimario?.nombre ?: ""
            val tallasText = variaciones?.take(6)?.joinToString("\n") { variacion ->
                val talla = variacion.talla?.numero?.toString() ?: "-"
                val stock = variacion.stock ?: 0
                "T$talla ($stock)"
            } ?: "Sin variaciones"
            tvTallas.text = tallasText

            // Stock total
            val stockTotal = variaciones?.sumOf { it.stock ?: 0 } ?: 0
            tvStock.text = stockTotal.toString()

            // Costo (tomamos el primero)
            val costo = variaciones?.firstOrNull()?.costo ?: "0"
            tvCosto.text = "$$costo"

            // Precio (tomamos el primero)
            val precio = variaciones?.firstOrNull()?.precio ?: "0"
            tvPrecio.text = "$$precio"

            // Ganancia
            val costoNum = costo.replace(".", "").replace(",", "").toIntOrNull() ?: 0
            val precioNum = precio.replace(".", "").replace(",", "").toIntOrNull() ?: 0
            val ganancia = precioNum - costoNum
            tvGanancia.text = "$$ganancia"
            tvGanancia.setTextColor(
                if (ganancia > 0) 0xFF10B981.toInt() else 0xFFEF4444.toInt()
            )

            // Botón stock
            btnStock.setOnClickListener {
                onStockClick(producto)
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
