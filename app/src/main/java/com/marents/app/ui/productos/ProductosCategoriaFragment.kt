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
                // Navegar a detalle del producto
                val bundle = Bundle().apply {
                    putInt("productoId", productoUI.id)
                    putString("productoNombre", productoUI.nombre)
                    putString("productoPrecio", productoUI.precio)
                    putString("productoImagen", productoUI.imagenUrl)
                    putString("productoCategoria", productoUI.subcategoria)
                    putStringArrayList("productoTallas", ArrayList(productoUI.tallas))
                }
                val detalleFragment = ProductoDetalleFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detalleFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onFavoritoClick = { productoUI ->
                viewModel.toggleFavorito(productoUI)
            }
        )

        binding.recyclerViewProductos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productoAdapter
        }
    }

    private fun setupClickListeners() {
        // Botón Volver a categorías - volver a pantalla de selección de categorías
        binding.btnVolverCategorias.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Botón Carrito (Bolsa)
        binding.btnCarrito.setOnClickListener {
            val carritoFragment = CarritoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, carritoFragment)
                .addToBackStack(null)
                .commit()
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
