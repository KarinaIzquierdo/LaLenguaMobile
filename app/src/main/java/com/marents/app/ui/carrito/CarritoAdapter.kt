package com.marents.app.ui.carrito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.marents.app.CartItem
import com.marents.app.R

class CarritoAdapter(
    private val onDeleteClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CarritoAdapter.CarritoViewHolder>(CarritoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        private val tvNombreProducto: TextView = itemView.findViewById(R.id.tvNombreProducto)
        private val tvTalla: TextView = itemView.findViewById(R.id.tvTalla)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)

        fun bind(item: CartItem) {
            tvNombreProducto.text = item.nombre ?: "Producto"
            tvTalla.text = "Talla: ${item.talla ?: "N/A"}"
            tvCantidad.text = "Cantidad: ${item.cantidad}"
            
            // Formatear precio de forma segura
            val precioStr = item.precio ?: "0"
            val precioFormateado = if (precioStr.startsWith("$")) {
                precioStr
            } else {
                "$${precioStr}"
            }
            tvPrecio.text = precioFormateado

            // Cargar imagen con placeholder para evitar cierres
            ivProducto.setImageResource(R.drawable.ic_shoe_placeholder)

            // Configurar click en eliminar
            btnEliminar.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    class CarritoDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
