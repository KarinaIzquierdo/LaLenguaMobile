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
import com.marents.app.CategoriaImages
import coil.load
import coil.transform.CircleCropTransformation

/**
 * Fragment de Selección de Categorías
 * Muestra: Mujer, Hombre, Niña, Niño distribuidas uniformemente
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

        cargarImagenes()
        setupClickListeners()
    }

    private fun cargarImagenes() {
        binding.ivMujer.load(CategoriaImages.MUJER) {
            crossfade(true)
            placeholder(android.R.drawable.progress_indeterminate_horizontal)
        }
        binding.ivHombre.load(CategoriaImages.HOMBRE) {
            crossfade(true)
            placeholder(android.R.drawable.progress_indeterminate_horizontal)
        }
        
        // Uso de links directos para evitar errores de compilación con archivos locales
        binding.ivNinos.load(CategoriaImages.NINOS) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        
        binding.ivPisahuevos.load(CategoriaImages.PISAHUEVOS) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        
        binding.ivPersonalizados.load(CategoriaImages.PERSONALIZADOS) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        
        binding.ivOutlet.load(CategoriaImages.OUTLET) {
            crossfade(true)
        }
    }

    /**
     * Configura los click listeners para cada categoría
     */
    private fun setupClickListeners() {
        // Botón Volver al inicio - regresa a pantalla principal
        binding.btnVolverInicio.setOnClickListener {
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.btnMujer.setOnClickListener {
            // Navegar a la pantalla de productos de Mujer
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Mujer")
        }

        binding.btnHombre.setOnClickListener {
            // Navegar a la pantalla de productos de Hombre
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Hombre")
        }

        binding.btnNinos.setOnClickListener {
            // Navegar a la pantalla de productos de Niños (Niño en BD)
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Niño")
        }

        binding.btnPisahuevos.setOnClickListener {
            // Navegar a la pantalla de productos de Pisa huevos (Ortografía correcta para el título)
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Pisa huevos")
        }

        binding.btnPersonalizados.setOnClickListener {
            // Navegar a la pantalla de Personalización
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToPersonalizados()
        }

        binding.btnOutlet.setOnClickListener {
            // Navegar a la pantalla de Outlet (podemos usar una categoría específica o filtro)
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToProductosCategoria("Outlet")
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
