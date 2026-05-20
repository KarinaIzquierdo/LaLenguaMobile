package com.marents.app.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.marents.app.Producto
import com.marents.app.R

class ProductosAdapter(
    private val onProductoClick: (Int) -> Unit
) : ListAdapter<Producto, ProductosAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view, onProductoClick)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductoViewHolder(
        itemView: View,
        private val onProductoClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)

        fun bind(producto: Producto) {
            tvNombre.text = producto.modelo?.nombre ?: "Sin nombre"

            // Obtener precio de la primera variación si existe
            val primeraVariacion = producto.variaciones?.firstOrNull()
            tvPrecio.text = primeraVariacion?.precio?.let { "$ $it" } ?: "Precio no disponible"
            tvStock.text = "Stock: ${primeraVariacion?.stock ?: 0}"

            // Cargar imagen con Coil
            ivProducto.load(producto.imagen) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }

            itemView.setOnClickListener {
                producto.id?.let { id -> onProductoClick(id) }
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
