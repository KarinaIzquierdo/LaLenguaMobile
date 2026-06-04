package com.marents.app.ui.productos

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.marents.app.Navigator
import com.marents.app.R
import com.marents.app.databinding.FragmentProductosCategoriaBinding
import com.marents.app.ui.carrito.CarritoFragment
import kotlinx.coroutines.launch

class ProductosCategoriaFragment : Fragment() {

    private var _binding: FragmentProductosCategoriaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosCategoriaViewModel by viewModels()
    private lateinit var productoAdapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriaNombre = arguments?.getString("categoriaNombre") ?: "Productos"
        binding.tvTituloCategoria.text = categoriaNombre

        // Mostrar botón de WhatsApp solo si es Personalizados
        if (categoriaNombre.lowercase().contains("personalizado")) {
            binding.fabWhatsapp.visibility = View.VISIBLE
            binding.fabWhatsapp.setOnClickListener {
                val phone = "+573000000000" // Reemplaza con el número real
                val message = "Hola Marents! Me interesa personalizar unos zapatos."
                val url = "https://api.whatsapp.com/send?phone=$phone&text=${android.net.Uri.encode(message)}"
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        viewModel.cargarProductosPorCategoria(categoriaNombre)
    }

    override fun onResume() {
        super.onResume()
        // Recargar productos cuando el fragmento se vuelve visible (después de editar)
        val categoriaNombre = arguments?.getString("categoriaNombre") ?: "Productos"
        viewModel.cargarProductosPorCategoria(categoriaNombre)
    }

    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            onProductoClick = { productoUI ->
                // Navegar a detalle del producto usando el nuevo sistema
                val bundle = Bundle().apply {
                    putInt("productoId", productoUI.id)
                    putString("productoNombre", productoUI.nombre)
                    putString("productoPrecio", productoUI.precio)
                    putString("productoImagen", productoUI.imagenUrl)
                    putString("productoCategoria", productoUI.categoria)
                    putStringArrayList("productoTallas", ArrayList(productoUI.tallas))
                }
                
                try {
                    findNavController().navigate(R.id.productoDetalleFragment, bundle)
                } catch (e: Exception) {
                    android.util.Log.e("ProductosCategoria", "Error al navegar: ${e.message}")
                }
            },
            onFavoritoClick = { productoUI ->
                // Guardar en favoritos de forma persistente
                FavoritosManager.toggleFavorito(requireContext(), productoUI)
                // Actualizar la UI del corazón en la lista
                viewModel.actualizarEstadoFavoritos(requireContext())
            }
        )

        binding.recyclerViewProductos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productoAdapter
        }
    }

    private fun setupClickListeners() {
        // Botón Volver a categorías
        binding.btnVolverCategorias.setOnClickListener {
            findNavController().popBackStack()
        }

        // Botón Carrito (Bolsa) - Navega a la pestaña del carrito
        binding.btnCarrito.setOnClickListener {
            findNavController().navigate(R.id.nav_new_carrito)
        }

        binding.etBuscar.setOnClickListener {
            Toast.makeText(requireContext(), "Búsqueda en construcción", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productos.collect { productos ->
                        productoAdapter.submitList(productos)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.limpiarError()
                        }
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
