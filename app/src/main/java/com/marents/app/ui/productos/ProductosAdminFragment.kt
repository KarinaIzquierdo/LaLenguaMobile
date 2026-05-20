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
    }

    override fun onResume() {
        super.onResume()
        // Forzar recarga de productos al volver a la pantalla o entrar por primera vez
        viewModel.cargarProductos()
    }

    private fun setupRecyclerView() {
        adapter = ProductosAdminAdapter(
            onEditClick = { producto ->
                // Cambiar a fragmento de edición en pantalla completa
                val fragment = EditarProductoFragment.newInstance(producto)
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
        val categorias = arrayOf("Todas las categorías", "Hombre", "Mujer", "Niño", "Pisa huevos")
        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategorias.adapter = categoriaAdapter

        // Spinner de ordenamiento
        val ordenamientos = arrayOf("Ordenar stock", "Mayor stock", "Menor stock", "Mayor precio", "Menor precio")
        val ordenAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ordenamientos)
        ordenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOrdenar.adapter = ordenAdapter

        binding.spinnerOrdenar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO: Implementar ordenamiento
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

        binding.btnHome.setOnClickListener {
            // Regresar al inicio del nav_host o actividad principal
            requireActivity().finish()
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
