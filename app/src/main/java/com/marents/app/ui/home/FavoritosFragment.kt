package com.marents.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.marents.app.R
import com.marents.app.databinding.FragmentFavoritosRealBinding
import com.marents.app.ui.productos.FavoritosManager
import com.marents.app.ui.productos.ProductoAdapter

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosRealBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosRealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        cargarFavoritos()
    }

    private fun setupRecyclerView() {
        adapter = ProductoAdapter(
            onProductoClick = { productoUI ->
                // Navegar al detalle igual que en la tienda
                val bundle = Bundle().apply {
                    putInt("productoId", productoUI.id)
                    putString("productoNombre", productoUI.nombre)
                    putString("productoPrecio", productoUI.precio)
                    putString("productoImagen", productoUI.imagenUrl)
                    putString("productoCategoria", productoUI.categoria)
                    putStringArrayList("productoTallas", ArrayList(productoUI.tallas))
                }
                findNavController().navigate(R.id.productoDetalleFragment, bundle)
            },
            onFavoritoClick = { productoUI ->
                // Eliminar de favoritos de forma persistente
                FavoritosManager.toggleFavorito(requireContext(), productoUI)
                cargarFavoritos() // Refrescar lista
            }
        )

        binding.rvFavoritos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FavoritosFragment.adapter
        }
    }

    private fun cargarFavoritos() {
        val listaFavs = FavoritosManager.getFavoritos(requireContext())
        if (listaFavs.isEmpty()) {
            binding.tvVacio.visibility = View.VISIBLE
            binding.rvFavoritos.visibility = View.GONE
        } else {
            binding.tvVacio.visibility = View.GONE
            binding.rvFavoritos.visibility = View.VISIBLE
            adapter.submitList(listaFavs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
