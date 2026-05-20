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
import com.marents.app.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

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
                    Toast.makeText(requireContext(), "Editar usuario: ${user.name}", Toast.LENGTH_SHORT).show()
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
            binding.btnAddUser.setOnClickListener {
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
            binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.recyclerViewUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en estado vacío: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteConfirmation(user: User) {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a ${user.name}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarUsuario(user.id)
                    Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en diálogo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
