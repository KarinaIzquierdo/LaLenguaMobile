package com.marents.app.ui.favoritos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.marents.app.R
import com.marents.app.databinding.FragmentFavoritosBinding
import com.marents.app.ui.productos.ProductoAdapter
import com.marents.app.ui.productos.ProductoDetalleFragment
import com.marents.app.utils.FavoritosManager

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritosManager: FavoritosManager
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritosManager = FavoritosManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        cargarFavoritos()
    }

    private fun setupRecyclerView() {
        adapter = ProductoAdapter(
            onProductoClick = { productoUI ->
                // Navegar al detalle del producto
                val bundle = Bundle().apply {
                    putInt("productoId", productoUI.id)
                    putString("productoNombre", productoUI.nombre)
                    putString("productoPrecio", productoUI.precio)
                    putString("productoImagen", productoUI.imagenUrl)
                    putString("productoCategoria", productoUI.categoria)
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
                // Eliminar de favoritos y recargar lista
                favoritosManager.toggleFavorito(productoUI)
                cargarFavoritos()
            }
        )

        binding.rvFavoritos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FavoritosFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnExplorar.setOnClickListener {
            // Regresar a categorías
            parentFragmentManager.popBackStack()
        }
    }

    private fun cargarFavoritos() {
        val lista = favoritosManager.getFavoritos()
        if (lista.isEmpty()) {
            binding.rvFavoritos.visibility = View.GONE
            binding.layoutVacio.visibility = View.VISIBLE
        } else {
            binding.rvFavoritos.visibility = View.VISIBLE
            binding.layoutVacio.visibility = View.GONE
            adapter.submitList(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
