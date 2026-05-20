package com.marents.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.marents.app.MainActivity
import com.marents.app.Navigator
import com.marents.app.R
import com.marents.app.User
import com.marents.app.databinding.FragmentUsersBinding

class   UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UsersViewModel by viewModels()
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupRecyclerView()
            setupClickListeners()
            observeViewModel()
            
            // Cargar usuarios al iniciar
            viewModel.cargarUsuarios()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        try {
            usersAdapter = UsersAdapter(
                users = emptyList(),
                onEditClick = { user ->
                    showEditDialog(user)
                },
                onDeleteClick = { user ->
                    showDeleteConfirmation(user)
                }
            )

            binding.recyclerViewUsers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = usersAdapter
                setHasFixedSize(true)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en RecyclerView: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        try {
            // Botón atrás
            binding.btnBack.setOnClickListener {
                (activity as? MainActivity)?.onBackPressed()
            }

            // Botón agregar usuario
            binding.fabAddUser.setOnClickListener {
                Toast.makeText(requireContext(), "Agregar nuevo usuario - Próximamente", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en botones: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun observeViewModel() {
        try {
            viewModel.users.observe(viewLifecycleOwner) { users ->
                users?.let {
                    usersAdapter.updateUsers(it)
                    updateEmptyState(it.isEmpty())
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.recyclerViewUsers.visibility = if (isLoading) View.GONE else View.VISIBLE
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    viewModel.limpiarError()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en observadores: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        try {
            binding.tvNoUsers.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.recyclerViewUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en estado vacío: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteConfirmation(user: User) {
        try {
            val nombreCompleto = "${user.nombres ?: ""} ${user.apellidos ?: ""}".trim()
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a $nombreCompleto?")
                .setPositiveButton("Eliminar") { _, _ ->
                    user.id?.let { viewModel.eliminarUsuario(it) }
                    Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en diálogo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showEditDialog(user: User) {
        try {
            val context = requireContext()
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_usuario, null)

            // Referencias a los campos
            val etNombres = dialogView.findViewById<android.widget.EditText>(R.id.etNombres)
            val etApellidos = dialogView.findViewById<android.widget.EditText>(R.id.etApellidos)
            val etEmail = dialogView.findViewById<android.widget.EditText>(R.id.etEmail)
            val etDocumento = dialogView.findViewById<android.widget.EditText>(R.id.etDocumento)
            val etCelular = dialogView.findViewById<android.widget.EditText>(R.id.etCelular)
            val spinnerRol = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerRol)
            val etPassword = dialogView.findViewById<android.widget.EditText>(R.id.etPassword)

            // Llenar campos con datos actuales
            etNombres.setText(user.nombres ?: "")
            etApellidos.setText(user.apellidos ?: "")
            etEmail.setText(user.email ?: "")
            etDocumento.setText(user.documento ?: "")
            etCelular.setText(user.celular ?: "")

            // Configurar spinner de roles
            val roles = arrayOf("cliente", "admin")
            val adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_item, roles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRol.adapter = adapter
            spinnerRol.setSelection(roles.indexOf(user.rol ?: "cliente"))

            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val nombres = etNombres.text.toString()
                    val apellidos = etApellidos.text.toString()
                    val email = etEmail.text.toString()
                    val documento = etDocumento.text.toString()
                    val celular = etCelular.text.toString()
                    val rol = spinnerRol.selectedItem.toString()
                    val password = etPassword.text.toString().takeIf { it.isNotEmpty() }

                    user.id?.let { userId ->
                        viewModel.actualizarUsuario(userId, nombres, apellidos, email, documento, celular, rol, password)
                        Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)

            builder.show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
