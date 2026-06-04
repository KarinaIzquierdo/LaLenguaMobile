package com.marents.app.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.marents.app.R

class UsersAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val tvUserRole: TextView = itemView.findViewById(R.id.tvUserRole)
        private val btnEdit: android.widget.ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: android.widget.ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(user: User) {
            // Mostrar información del usuario
            tvUserName.text = user.name
            tvUserEmail.text = user.email
            tvUserRole.text = "Rol: ${user.role}"
            
            // Configurar color según rol
            val roleColor = when (user.role.lowercase()) {
                "admin" -> "#E74C3C"
                "cliente" -> "#3498DB"
                else -> "#95A5A6"
            }
            tvUserRole.setTextColor(android.graphics.Color.parseColor(roleColor))
            
            // Click listeners para los botones de acción
            btnEdit.setOnClickListener {
                onEditClick(user)
            }
            
            btnDelete.setOnClickListener {
                onDeleteClick(user)
            }
            
            // Click en toda la tarjeta
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Seleccionado: ${user.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
