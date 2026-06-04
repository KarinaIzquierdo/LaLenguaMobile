package com.marents.app.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.marents.app.Navigator
import com.marents.app.R
import com.marents.app.databinding.FragmentCategoriesBinding
import com.marents.app.CategoriaImages
import coil.load
import coil.transform.CircleCropTransformation

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
        binding.ivMujer.load(CategoriaImages.MUJER) { crossfade(true) }
        binding.ivHombre.load(CategoriaImages.HOMBRE) { crossfade(true) }
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
        binding.ivOutlet.load(R.drawable.img_outlet) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    private fun setupClickListeners() {
        binding.btnVolverInicio.visibility = View.VISIBLE
        binding.btnVolverInicio.setOnClickListener {
            // Regresar a la pantalla de bienvenida (MainActivity)
            val intent = android.content.Intent(requireContext(), com.marents.app.MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnMujer.setOnClickListener {
            navegarACategoria("Mujer")
        }

        binding.btnHombre.setOnClickListener {
            navegarACategoria("Hombre")
        }

        binding.btnNinos.setOnClickListener {
            // Sincronizado con el nombre exacto en el servidor Laravel
            navegarACategoria("Niños")
        }

        binding.btnPisahuevos.setOnClickListener {
            navegarACategoria("Pisa huevos")
        }

        binding.btnPersonalizados.setOnClickListener {
            navegarACategoria("Personalizados")
        }

        binding.btnOutlet.setOnClickListener {
            navegarACategoria("Outlet")
        }
    }

    private fun navegarACategoria(nombre: String) {
        val bundle = Bundle().apply {
            putString("categoriaNombre", nombre)
        }
        
        try {
            // Método estándar de navegación de Jetpack Navigation
            // Este es el más fiable dentro de MainMenuActivity
            findNavController().navigate(R.id.productosCategoriaFragment, bundle)
        } catch (e: Exception) {
            android.util.Log.e("CategoriasFragment", "Error navegando a $nombre: ${e.message}")
            // Fallback en caso de que el controlador se pierda
            try {
                val navHostFragment = requireActivity().supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_main) as? androidx.navigation.fragment.NavHostFragment
                navHostFragment?.navController?.navigate(R.id.productosCategoriaFragment, bundle)
            } catch (e2: Exception) {
                Toast.makeText(requireContext(), "Error al abrir la categoría", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
