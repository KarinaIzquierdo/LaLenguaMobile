package com.marents.app.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.marents.app.MainActivity
import com.marents.app.R
import com.marents.app.databinding.FragmentProductosAdminBinding

class ProductosAdminFragment : Fragment() {

    private var _binding: FragmentProductosAdminBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductosViewModel by viewModels()
    private lateinit var adapter: ProductosAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinners()
        setupClickListeners()
        observeViewModel()
        
        // Cargar productos inmediatamente al entrar
        viewModel.cargarProductos()
    }

    private fun setupRecyclerView() {
        adapter = ProductosAdminAdapter(
            onVerClick = { producto ->
                mostrarFichaInformativa(producto)
            },
            onEditClick = { producto ->
                // Cambiar a fragmento de edición en pantalla completa
                val fragment = EditarProductoFragment.newInstance(producto)
                
                // Escuchar cuando se cierra el fragmento para recargar la lista
                parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->
                    val updated = bundle.getBoolean("updated", false)
                    if (updated) {
                        viewModel.cargarProductos()
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { producto ->
                // Confirmar eliminación
                mostrarConfirmacionEliminar(producto)
            }
        )

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductosAdminFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSpinners() {
        // Spinner de categorías
        val categorias = arrayOf("Todas las categorías", "Hombre", "mujer", "Niños", "Pisa huevos", "Outlet")
        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategorias.adapter = categoriaAdapter

        binding.spinnerCategorias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categoria = categorias[position]
                if (categoria == "Todas las categorías") {
                    viewModel.cargarProductos()
                } else {
                    viewModel.buscarProductos(categoria)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Spinner de ordenamiento
        val ordenamientos = arrayOf("Ordenar stock", "Mayor stock", "Menor stock")
        val ordenAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ordenamientos)
        ordenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOrdenar.adapter = ordenAdapter

        binding.spinnerOrdenar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> viewModel.ordenarPorStock(true) // Mayor stock
                    2 -> viewModel.ordenarPorStock(false) // Menor stock
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClickListeners() {
        binding.etBuscar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.buscarProductos(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnGestionExistencias.setOnClickListener {
            val intent = Intent(requireContext(), GestionStockGlobalActivity::class.java)
            startActivity(intent)
        }

        binding.btnNuevoProducto.setOnClickListener {
            // Cambiar a fragmento de creación en pantalla completa para que sea espacioso
            val fragment = CrearProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnAnterior.setOnClickListener {
            val pagina = viewModel.paginaActual.value ?: 1
            viewModel.cambiarPagina(pagina - 1)
        }

        binding.btnSiguiente.setOnClickListener {
            val pagina = viewModel.paginaActual.value ?: 1
            viewModel.cambiarPagina(pagina + 1)
        }
    }

    private fun observeViewModel() {
        viewModel.productos.observe(viewLifecycleOwner) { productos ->
            adapter.submitList(productos)
            binding.layoutEmpty.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewProductos.visibility = if (productos.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.limpiarError()
            }
        }

        viewModel.paginaActual.observe(viewLifecycleOwner) { pagina ->
            binding.tvPaginaActual.text = pagina.toString()
            actualizarInfoPaginacion()
        }

        viewModel.totalPaginas.observe(viewLifecycleOwner) { total ->
            actualizarInfoPaginacion()
        }
    }

    private fun mostrarConfirmacionEliminar(producto: com.marents.app.Producto) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar el producto \"${producto.modelo?.nombre}\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                producto.id?.let { id ->
                    viewModel.eliminarProducto(id)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarFichaInformativa(producto: com.marents.app.Producto) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_info_producto, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Vincular vistas
        val ivProducto = dialogView.findViewById<android.widget.ImageView>(R.id.ivProductoInfo)
        val tvNombre = dialogView.findViewById<android.widget.TextView>(R.id.tvNombreInfo)
        val tvCategoria = dialogView.findViewById<android.widget.TextView>(R.id.tvCategoriaInfo)
        val tvPrecio = dialogView.findViewById<android.widget.TextView>(R.id.tvPrecioInfo)
        val tvCosto = dialogView.findViewById<android.widget.TextView>(R.id.tvCostoInfo)
        val tvStock = dialogView.findViewById<android.widget.TextView>(R.id.tvStockInfo)
        val tvTallas = dialogView.findViewById<android.widget.TextView>(R.id.tvTallasInfo)
        val btnCerrar = dialogView.findViewById<android.widget.Button>(R.id.btnCerrarInfo)

        // Asignar datos
        tvNombre.text = producto.modelo?.nombre ?: "Sin nombre"
        tvCategoria.text = producto.modelo?.categoria?.nombre ?: "Sin categoría"
        
        val formatter = java.text.DecimalFormat("$ #,###")
        val precio = producto.precio?.toDoubleOrNull() ?: 0.0
        val costo = producto.costo?.toDoubleOrNull() ?: 0.0
        tvPrecio.text = formatter.format(precio)
        tvCosto.text = formatter.format(costo)
        
        val stock = producto.stock ?: producto.variaciones?.sumOf { it.stock ?: 0 } ?: 0
        tvStock.text = stock.toString()

        val tallas = producto.tallas?.joinToString(", ") 
            ?: producto.variaciones?.mapNotNull { it.talla?.numero?.toString() }?.distinct()?.joinToString(", ")
            ?: "N/A"
        tvTallas.text = tallas

        // Cargar imagen si existe
        if (!producto.imagen.isNullOrEmpty()) {
            // Aquí puedes usar Glide o Coil para cargar la imagen
            // Por ahora dejamos el placeholder
        }

        btnCerrar.setOnClickListener { dialog.dismiss() }
        
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
    }

    private fun actualizarInfoPaginacion() {
        val pagina = viewModel.paginaActual.value ?: 1
        val total = viewModel.totalPaginas.value ?: 1
        val productos = viewModel.productos.value?.size ?: 0
        binding.tvInfoPaginacion.text = "Mostrando página $pagina de $total"
    }

    fun recargarProductos() {
        viewModel.cargarProductos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
