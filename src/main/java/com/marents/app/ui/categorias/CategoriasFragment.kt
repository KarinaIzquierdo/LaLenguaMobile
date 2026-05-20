package com.marents.app.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.launch

/**
 * Fragment de Selección de Categorías
 * Muestra: Dama, Caballero, Niña, Niño
 * Navega a Home al seleccionar una categoría
 */
class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriasViewModel by viewModels()
    private lateinit var adapter: CategoriasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    /**
     * Configura el RecyclerView con adapter y layout manager
     */
    private fun setupRecyclerView() {
        adapter = CategoriasAdapter { categoria ->
            // Al hacer click en una categoría
            viewModel.seleccionarCategoria(categoria)
            Toast.makeText(
                requireContext(),
                "Seleccionado: ${categoria.nombre}",
                Toast.LENGTH_SHORT
            ).show()
            
            // Navegar a Home (pasando la categoría seleccionada)
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToHome()
        }

        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoriasFragment.adapter
        }
    }

    /**
     * Observa los StateFlows del ViewModel
     * - categorias: Lista de categorías a mostrar
     * - categoriaSeleccionada: Cuando se selecciona una
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                // Observa lista de categorías
                launch {
                    viewModel.categorias.collect { categorias ->
                        adapter.submitList(categorias)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
