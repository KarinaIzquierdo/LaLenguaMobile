package com.marents.app.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.marents.app.*
import com.marents.app.databinding.FragmentProductoDetalleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoDetalleFragment : Fragment() {

    private var _binding: FragmentProductoDetalleBinding? = null
    private val binding get() = _binding!!

    private var cantidad = 1
    private var tallaSeleccionada: String? = null
    private lateinit var producto: ProductoUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener datos del producto desde arguments
        arguments?.let { args ->
            val id = args.getInt("productoId", 0)
            val nombre = args.getString("productoNombre", "")
            val precio = args.getString("productoPrecio", "")
            val imagen = args.getString("productoImagen", "")
            val categoria = args.getString("productoCategoria", "")
            val tallas = args.getStringArrayList("productoTallas") ?: arrayListOf()

            producto = ProductoUI(
                id = id,
                nombre = nombre,
                precio = precio,
                categoria = categoria,
                subcategoria = categoria,
                imagenUrl = imagen,
                tallas = tallas,
                esFavorito = false
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupTallas()
        setupCantidad()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.tvNombreModelo.text = producto.nombre
        binding.tvCategoria.text = producto.subcategoria
        binding.tvPrecio.text = producto.precio
        binding.tvCantidad.text = cantidad.toString()

        // Cargar imagen
        if (producto.imagenUrl != null) {
            binding.ivProductoImagen.load(producto.imagenUrl) {
                placeholder(R.drawable.ic_shoe_placeholder)
                error(R.drawable.ic_shoe_placeholder)
            }
        } else {
            binding.ivProductoImagen.setImageResource(R.drawable.ic_shoe_placeholder)
        }
    }

    private fun setupTallas() {
        binding.layoutTallas.removeAllViews()
        val density = resources.displayMetrics.density
        val margin = (8 * density).toInt()
        val itemWidth = (68 * density).toInt()
        val itemHeight = (56 * density).toInt()

        // Crear filas horizontales
        var currentRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Obtener tallas del producto o usar por defecto según categoría
        val categoriaLower = producto.categoria?.lowercase() ?: ""
        
        var tallasDisponibles = if (producto.tallas.isNotEmpty()) {
            // Si el producto tiene tallas, usarlas
            producto.tallas.mapNotNull { 
                it.replace(".0", "").toIntOrNull() 
            }.sorted()
        } else {
            // Si no tiene tallas, usar rangos por defecto según categoría
            when {
                categoriaLower.contains("niño") || categoriaLower.contains("nino") || categoriaLower.contains("infantil") -> {
                    (20..34).toList() // Tallas niños
                }
                categoriaLower.contains("dama") || categoriaLower.contains("mujer") -> {
                    (35..40).toList() // Tallas dama
                }
                categoriaLower.contains("caballero") || categoriaLower.contains("hombre") -> {
                    (38..45).toList() // Tallas caballero
                }
                else -> {
                    (35..45).toList() // Por defecto, todas las tallas
                }
            }
        }

        // Si es categoría niños, filtrar solo tallas de niños (20-34)
        if (categoriaLower.contains("niño") || categoriaLower.contains("nino") || categoriaLower.contains("infantil")) {
            tallasDisponibles = tallasDisponibles.filter { it in 20..34 }
        }

        tallasDisponibles.forEachIndexed { index, tallaNum ->
            // Nueva fila cada 5 tallas
            if (index > 0 && index % 5 == 0) {
                binding.layoutTallas.addView(currentRow)
                currentRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, margin, 0, 0)
                }
            }

            val stock = (10..25).random() // Stock aleatorio para demo

            val tallaButton = android.widget.Button(requireContext()).apply {
                text = tallaNum.toString()
                textSize = 16f
                isAllCaps = false
                tag = "disponible"

                // Todos disponibles - fondo blanco por defecto
                setBackgroundResource(R.drawable.bg_talla_detalle)
                setTextColor(android.graphics.Color.WHITE)

                layoutParams = LinearLayout.LayoutParams(itemWidth, itemHeight).apply {
                    setMargins(margin/2, margin/2, margin/2, margin/2)
                }

                setOnClickListener {
                    // Deseleccionar todas las tallas
                    deselectAllTallas()

                    // Seleccionar esta (azul)
                    setBackgroundResource(R.drawable.bg_talla_detalle_selected)
                    tallaSeleccionada = tallaNum.toString()
                    binding.tvStockInfo.text = "$stock unidades disponibles"
                    binding.tvStockInfo.visibility = View.VISIBLE
                }
            }

            currentRow.addView(tallaButton)

            // Seleccionar la primera automáticamente
            if (index == 0) {
                tallaButton.post {
                    tallaButton.setBackgroundResource(R.drawable.bg_talla_detalle_selected)
                    tallaSeleccionada = tallaNum.toString()
                    binding.tvStockInfo.text = "$stock unidades disponibles"
                    binding.tvStockInfo.visibility = View.VISIBLE
                }
            }
        }

        binding.layoutTallas.addView(currentRow)
    }

    private fun deselectAllTallas() {
        for (i in 0 until binding.layoutTallas.childCount) {
            val row = binding.layoutTallas.getChildAt(i) as? LinearLayout
            row?.let {
                for (j in 0 until it.childCount) {
                    val btn = it.getChildAt(j) as? android.widget.Button
                    btn?.let { button ->
                        button.setBackgroundResource(R.drawable.bg_talla_detalle)
                    }
                }
            }
        }
    }

    private fun setupCantidad() {
        binding.btnMenos.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                binding.tvCantidad.text = cantidad.toString()
            }
        }

        binding.btnMas.setOnClickListener {
            if (cantidad < 10) {
                cantidad++
                binding.tvCantidad.text = cantidad.toString()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tvGuiaTallas.setOnClickListener {
            mostrarGuiaTallasDialog()
        }

        binding.btnAgregarBolsa.setOnClickListener {
            if (tallaSeleccionada == null) {
                Toast.makeText(requireContext(), "Selecciona una talla primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Obtener el userId real del usuario logueado. Por ahora usamos 1 para pruebas.
            val userId = 1 
            val request = AddToCartRequest(
                userId = userId,
                productoId = producto.id,
                talla = tallaSeleccionada!!,
                cantidad = cantidad
            )

            binding.btnAgregarBolsa.isEnabled = false
            
            RetrofitClient.apiService.addToCart(request).enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (!isAdded) return // Verificar que el fragment sigue activo
                    binding.btnAgregarBolsa.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Agregado al carrito con éxito", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error al agregar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    if (!isAdded) return // Verificar que el fragment sigue activo
                    binding.btnAgregarBolsa.isEnabled = true
                    Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Click en círculo de color
        binding.circleColorNegro.setOnClickListener {
            // Cambiar estado visual del color seleccionado
            binding.circleColorNegro.background = resources.getDrawable(R.drawable.circle_color_selected, null)
        }
    }

    private fun mostrarGuiaTallasDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_guia_tallas, null)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnCerrar)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
