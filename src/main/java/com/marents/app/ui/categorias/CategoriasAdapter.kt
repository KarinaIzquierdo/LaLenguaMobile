package com.marents.app.ui.categorias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marents.app.CategoriaItem
import com.marents.app.R

/**
 * Adapter para mostrar categorías (Dama, Caballero, Niña, Niño)
 * Usa ListAdapter con DiffUtil para actualizaciones eficientes
 */
class CategoriasAdapter(
    private val onCategoriaClick: (CategoriaItem) -> Unit
) : ListAdapter<CategoriaItem, CategoriasAdapter.CategoriaViewHolder>(CategoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view, onCategoriaClick)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoriaViewHolder(
        itemView: View,
        private val onCategoriaClick: (CategoriaItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivCategoria: ImageView = itemView.findViewById(R.id.ivCategoria)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val cvImagen: CardView = itemView.findViewById(R.id.cvImagen)

        fun bind(categoria: CategoriaItem) {
            tvNombre.text = categoria.nombre
            
            // Cargar imagen si existe, si no usar placeholder
            if (categoria.iconoResId != 0) {
                ivCategoria.setImageResource(categoria.iconoResId)
            }
            
            // Color de fondo del círculo (si se proporciona)
            try {
                cvImagen.setCardBackgroundColor(
                    android.graphics.Color.parseColor(categoria.colorFondo)
                )
            } catch (e: Exception) {
                // Color por defecto si hay error
                cvImagen.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.white)
                )
            }

            itemView.setOnClickListener {
                onCategoriaClick(categoria)
            }
        }
    }

    class CategoriaDiffCallback : DiffUtil.ItemCallback<CategoriaItem>() {
        override fun areItemsTheSame(oldItem: CategoriaItem, newItem: CategoriaItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoriaItem, newItem: CategoriaItem): Boolean {
            return oldItem == newItem
        }
    }
}
