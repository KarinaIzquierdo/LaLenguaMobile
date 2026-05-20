package com.marents.app.ui.admin

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marents.app.AppRoutes
import com.marents.app.MainActivity
import com.marents.app.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AdminViewModel by viewModels()
    private var isMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupFloatingMenu()
        observeViewModel()
        
        // Cargar estadísticas al iniciar
        viewModel.cargarEstadisticas()
    }

    private fun observeViewModel() {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            stats?.let {
                actualizarMetricas(it)
            }
        }

        viewModel.productosVendidos.observe(viewLifecycleOwner) { productos ->
            productos?.let {
                mostrarProductosVendidos(it)
            }
        }

        viewModel.pedidosRecientes.observe(viewLifecycleOwner) { pedidos ->
            pedidos?.let {
                mostrarPedidosRecientes(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Mostrar/ocultar indicador de carga si es necesario
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.limpiarError()
            }
        }
    }

    private fun actualizarMetricas(stats: AdminStats) {
        binding.tvPedidosPendientes.text = stats.pedidosPendientes.toString()
        binding.tvPedidosCompletados.text = stats.pedidosCompletados.toString()
        binding.tvTotalVentas.text = formatCurrency(stats.totalVentas)
        binding.tvStockBajo.text = stats.stockBajo.toString()
    }

    private fun mostrarProductosVendidos(productos: List<ProductoVendido>) {
        binding.layoutProductosVendidos.removeAllViews()

        for (producto in productos) {
            val row = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                setPadding(0, 8.dpToPx(), 0, 8.dpToPx())
            }

            val nombreView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = producto.nombre
                textSize = 14f
            }

            val cantidadView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = producto.cantidad.toString()
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            row.addView(nombreView)
            row.addView(cantidadView)
            binding.layoutProductosVendidos.addView(row)
        }
    }

    private fun mostrarPedidosRecientes(pedidos: List<PedidoReciente>) {
        binding.layoutPedidosRecientes.removeAllViews()

        for (pedido in pedidos) {
            val row = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                setPadding(0, 12.dpToPx(), 0, 12.dpToPx())
            }

            val estadoColor = if (pedido.estado == "Pendiente") "#F39C12" else "#27AE60"

            val idView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.5f
                )
                text = pedido.id
                textSize = 12f
                gravity = android.view.Gravity.CENTER
            }

            val clienteView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = pedido.cliente
                textSize = 12f
            }

            val totalView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.8f
                )
                text = pedido.total
                textSize = 12f
            }

            val estadoView = android.widget.TextView(requireContext()).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.8f
                )
                text = pedido.estado
                textSize = 10f
                gravity = android.view.Gravity.CENTER
                setPadding(8.dpToPx(), 4.dpToPx(), 8.dpToPx(), 4.dpToPx())
                setBackgroundColor(android.graphics.Color.parseColor(estadoColor))
                setTextColor(android.graphics.Color.WHITE)
            }

            row.addView(idView)
            row.addView(clienteView)
            row.addView(totalView)
            row.addView(estadoView)
            binding.layoutPedidosRecientes.addView(row)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun formatCurrency(value: Int): String {
        return if (value >= 1000000) {
            "$${value / 1000000}.${(value % 1000000) / 100000}M"
        } else if (value >= 1000) {
            "$${value / 1000}K"
        } else {
            "$$value"
        }
    }

    private fun setupClickListeners() {
        // Pedidos
        binding.cardPedidos.setOnClickListener {
            Toast.makeText(requireContext(), "Ver detalles de pedidos - Próximamente", Toast.LENGTH_SHORT).show()
            closeFloatingMenu()
        }

        // Ventas
        binding.cardVentas.setOnClickListener {
            Toast.makeText(requireContext(), "Ver Ventas y Pedidos - Próximamente", Toast.LENGTH_SHORT).show()
            closeFloatingMenu()
        }

        // Productos
        binding.cardProductos.setOnClickListener {
            Toast.makeText(requireContext(), "Ver Productos con stock bajo - Próximamente", Toast.LENGTH_SHORT).show()
            closeFloatingMenu()
        }

        // Configuración
        binding.cardConfiguracion.setOnClickListener {
            Toast.makeText(requireContext(), "Configuración - Próximamente", Toast.LENGTH_SHORT).show()
            closeFloatingMenu()
        }
    }

    private fun setupFloatingMenu() {
        // FAB principal para abrir/cerrar menú
        binding.fabMenu.setOnClickListener {
            toggleFloatingMenu()
        }

        // Opción Ver Usuarios
        binding.fabUsuarios.setOnClickListener {
            closeFloatingMenu()
            (activity as? MainActivity)?.navigateToFragment(AppRoutes.USERS)
        }

        // Opción Ver Productos
        binding.fabProductos.setOnClickListener {
            closeFloatingMenu()
            (activity as? MainActivity)?.navigateToFragment(AppRoutes.PRODUCTOS)
        }

        // Cerrar menú al hacer clic fuera
        binding.root.setOnClickListener {
            if (isMenuOpen) {
                closeFloatingMenu()
            }
        }
    }

    private fun toggleFloatingMenu() {
        if (isMenuOpen) {
            closeFloatingMenu()
        } else {
            openFloatingMenu()
        }
    }

    private fun openFloatingMenu() {
        isMenuOpen = true
        binding.floatingMenu.visibility = View.VISIBLE
        
        // Animar FAB rotación
        val rotateFab = ObjectAnimator.ofFloat(binding.fabMenu, "rotation", 0f, 45f)
        rotateFab.duration = 300
        rotateFab.start()
        
        // Animar aparición del menú
        val fadeIn = ObjectAnimator.ofFloat(binding.floatingMenu, "alpha", 0f, 1f)
        fadeIn.duration = 300
        fadeIn.start()
        
        // Animar cada opción del menú
        val slideInUsuarios = ObjectAnimator.ofFloat(binding.fabUsuarios, "translationY", 100f, 0f)
        slideInUsuarios.duration = 300
        slideInUsuarios.startDelay = 100
        slideInUsuarios.start()
        
        val slideInProductos = ObjectAnimator.ofFloat(binding.fabProductos, "translationY", 100f, 0f)
        slideInProductos.duration = 300
        slideInProductos.startDelay = 200
        slideInProductos.start()
    }

    private fun closeFloatingMenu() {
        if (!isMenuOpen) return
        
        isMenuOpen = false
        
        // Animar FAB rotación inversa
        val rotateFab = ObjectAnimator.ofFloat(binding.fabMenu, "rotation", 45f, 0f)
        rotateFab.duration = 300
        rotateFab.start()
        
        // Animar desaparición del menú
        val fadeOut = ObjectAnimator.ofFloat(binding.floatingMenu, "alpha", 1f, 0f)
        fadeOut.duration = 300
        fadeOut.start()
        
        // Ocultar después de la animación
        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Verificar que el binding no sea nulo antes de acceder
                _binding?.let { binding ->
                    binding.floatingMenu.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
