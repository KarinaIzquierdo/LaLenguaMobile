package com.marents.app.ui.carrito

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.marents.app.*
import com.marents.app.databinding.FragmentCarritoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!
    private lateinit var carritoAdapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        cargarCarrito()
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            onDeleteClick = { item ->
                eliminarItemDelCarrito(item)
            }
        )

        binding.recyclerViewCarrito.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carritoAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnIrAPagar.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de pago en construcción", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCarrito() {
        // Obtener el userId real desde SharedPreferences (donde se guarda al hacer login)
        val prefs = requireActivity().getSharedPreferences("marents_prefs", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            binding.tvCarritoVacio.text = "Inicia sesión para ver tu carrito"
            binding.tvCarritoVacio.visibility = View.VISIBLE
            return
        }

        Log.d("CarritoFragment", "Cargando carrito para usuario $userId")
        binding.tvCarritoVacio.text = "Cargando carrito..."
        binding.tvCarritoVacio.visibility = View.VISIBLE

        RetrofitClient.apiService.getCart(userId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Log.d("CarritoFragment", "Respuesta API: ${response.code()}, body: ${response.body()}")
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    Log.d("CarritoFragment", "Items recibidos: ${items.size}")
                    actualizarUI(items)
                } else {
                    Log.e("CarritoFragment", "Error API: ${response.code()}")
                    binding.tvCarritoVacio.text = "Error al cargar carrito"
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Log.e("CarritoFragment", "Error de red: ${t.message}", t)
                binding.tvCarritoVacio.text = "Error de conexión"
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun actualizarUI(items: List<CartItem>) {
        Log.d("CarritoFragment", "Actualizando UI con ${items.size} items")
        if (items.isEmpty()) {
            Log.d("CarritoFragment", "Carrito vacío - mostrando mensaje vacío")
            binding.tvCarritoVacio.text = "Tu carrito está vacío"
            binding.tvCarritoVacio.visibility = View.VISIBLE
            binding.recyclerViewCarrito.visibility = View.GONE
            binding.tvCantidadProductos.text = "0 productos"
            binding.tvSubtotal.text = "$0"
            binding.tvTotal.text = "$0"
        } else {
            Log.d("CarritoFragment", "Carrito con items - ocultando mensaje vacío, mostrando RecyclerView")
            binding.tvCarritoVacio.visibility = View.GONE
            binding.recyclerViewCarrito.visibility = View.VISIBLE
            
            carritoAdapter.submitList(items)
            
            var total = 0
            var cantidadTotal = 0
            
            for (item in items) {
                try {
                    cantidadTotal += item.cantidad
                    val precioLimpio = item.precio.replace(Regex("[^0-9.]"), "")
                    val precioNumerico = precioLimpio.toDoubleOrNull() ?: 0.0
                    total += (precioNumerico.toInt() * item.cantidad)
                } catch (e: Exception) {
                    Log.e("CarritoFragment", "Error procesando item: ${e.message}")
                }
            }
            
            val productosTexto = if (cantidadTotal == 1) "1 producto" else "$cantidadTotal productos"
            binding.tvCantidadProductos.text = productosTexto
            val precioFormateado = "$${formatPrecio(total)}"
            binding.tvSubtotal.text = precioFormateado
            binding.tvTotal.text = precioFormateado
        }
    }

    private fun eliminarItemDelCarrito(item: CartItem) {
        RetrofitClient.apiService.removeFromCart(item.itemId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
                    // Recargar el carrito para actualizar la UI
                    cargarCarrito()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatPrecio(precio: Int): String {
        return java.text.NumberFormat.getInstance(java.util.Locale("es", "CO")).format(precio)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
