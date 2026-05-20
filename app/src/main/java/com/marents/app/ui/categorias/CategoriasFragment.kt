package com.marents.app.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentCategoriesBinding

/**
 * Fragment de Selección de Categorías
 * Muestra: Dama, Caballero, Niña, Niño distribuidas uniformemente
 * Navega a Home al seleccionar una categoría
 */
class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriasViewModel by viewModels()

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

        setupClickListeners()
    }

    /**
     * Configura los click listeners para cada categoría
     */
    private fun setupClickListeners() {
        // Botón Volver al inicio - regresa a pantalla principal
        binding.btnVolverInicio.setOnClickListener {
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.btnDama.setOnClickListener {
            // Navegar a la pantalla de productos de Dama
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Dama")
        }

        binding.btnCaballero.setOnClickListener {
            // Navegar a la pantalla de productos de Caballero
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Caballero")
        }

        binding.btnNinos.setOnClickListener {
            // Navegar a la pantalla de productos de Niños
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Niños")
        }

        binding.btnPersonalizados.setOnClickListener {
            // Navegar a la pantalla de Personalización
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToPersonalizados()
        }
    }

    /**
     * Navega a Home con la categoría seleccionada
     */
    private fun navegarACategoria(nombreCategoria: String) {
        Toast.makeText(
            requireContext(),
            "Seleccionado: $nombreCategoria",
            Toast.LENGTH_SHORT
        ).show()

        // Navegar a Home
        (activity as? Navigator.Provider)?.getNavigator()?.navigateToHome()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
