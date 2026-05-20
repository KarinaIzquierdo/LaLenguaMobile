package com.marents.app.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        viewModel.cargarProductos()
    }

    private fun setupRecyclerView() {
        adapter = ProductosAdminAdapter { producto ->
            Toast.makeText(requireContext(), "Stock de ${producto.modelo?.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductosAdminFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSpinners() {
        // Spinner de categorías
        val categorias = arrayOf("Todas las categorías", "Caballero", "Dama", "Niño", "Deportivo")
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
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnGestionExistencias.setOnClickListener {
            Toast.makeText(requireContext(), "Gestión de existencias", Toast.LENGTH_SHORT).show()
        }

        binding.btnNuevoProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Crear nuevo producto", Toast.LENGTH_SHORT).show()
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

    private fun actualizarInfoPaginacion() {
        val pagina = viewModel.paginaActual.value ?: 1
        val total = viewModel.totalPaginas.value ?: 1
        val productos = viewModel.productos.value?.size ?: 0
        binding.tvInfoPaginacion.text = "Mostrando página $pagina de $total"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
