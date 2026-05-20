package com.marents.app.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.marents.app.Producto
import com.marents.app.RetrofitClient
import com.marents.app.databinding.FragmentEditarProductoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarProductoFragment : Fragment() {

    private var _binding: FragmentEditarProductoBinding? = null
    private val binding get() = _binding!!
    private var productoAEditar: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productoAEditar = arguments?.getSerializable("producto") as? Producto
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupClickListeners()
        
        productoAEditar?.let {
            prellenarDatos()
        } ?: run {
            Toast.makeText(requireContext(), "Error: No se recibió información del producto", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun prellenarDatos() {
        productoAEditar?.let { producto ->
            binding.etNombreModelo.setText(producto.modelo?.nombre)
            
            val variacion = producto.variaciones?.firstOrNull()
            binding.etColor.setText(variacion?.colorPrimario?.nombre)
            
            // Stock total
            val stockTotal = producto.variaciones?.sumOf { it.stock ?: 0 } ?: 0
            binding.etStock.setText(stockTotal.toString())
            
            // Precios y costos
            val precio = producto.variaciones?.mapNotNull { it.precio?.toDoubleOrNull() }?.average() ?: 0.0
            val costo = producto.variaciones?.mapNotNull { it.costo?.toDoubleOrNull() }?.average() ?: 0.0
            
            binding.etPrecio.setText(if (precio > 0) String.format("%.2f", precio) else "")
            binding.etCosto.setText(if (costo > 0) String.format("%.2f", costo) else "")
            
            // Seleccionar categoría
            val categoriaNombre = producto.modelo?.categoria?.nombre ?: ""
            val categorias = arrayOf("Seleccionar categoría", "Dama", "Caballero", "Niños")
            val index = categorias.indexOfFirst { it.equals(categoriaNombre, ignoreCase = true) }
            if (index != -1) binding.spinnerCategoria.setSelection(index)
            
            // Talla (solo lectura o la primera disponible)
            val tallaNum = variacion?.talla?.numero?.toString() ?: ""
            val tallas = arrayOf("Seleccionar talla", "T21", "T22", "T23", "T24", "T25", "T26", "T27", "T28", "T29", "T30")
            val tallaIndex = tallas.indexOfFirst { it.contains(tallaNum) }
            if (tallaIndex != -1) binding.spinnerTalla.setSelection(tallaIndex)
        }
    }

    private fun setupSpinners() {
        val categorias = arrayOf("Seleccionar categoría", "Dama", "Caballero", "Niños")
        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = categoriaAdapter

        val tallas = arrayOf("Seleccionar talla", "T21", "T22", "T23", "T24", "T25", "T26", "T27", "T28", "T29", "T30")
        val tallaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tallas)
        tallaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTalla.adapter = tallaAdapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun guardarCambios() {
        val nombre = binding.etNombreModelo.text.toString().trim()
        val color = binding.etColor.text.toString().trim()
        val stock = binding.etStock.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val costo = binding.etCosto.text.toString().trim()
        val categoria = binding.spinnerCategoria.selectedItem.toString()

        if (nombre.isEmpty()) {
            binding.etNombreModelo.error = "Ingrese el nombre"
            return
        }

        if (categoria == "Seleccionar categoría") {
            Toast.makeText(requireContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.text = "Actualizando..."

        lifecycleScope.launch {
            try {
                val categoriaId = when (categoria) {
                    "Caballero" -> 1
                    "Dama" -> 2
                    "Niños" -> 3
                    else -> 1
                }

                val requestBody = hashMapOf(
                    "modelo_nombre" to nombre,
                    "categoria_id" to categoriaId.toString(),
                    "color_primario" to (if (color.isEmpty()) "Negro" else color),
                    "precio" to precio,
                    "costo" to costo,
                    "stock" to stock
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarProducto(productoAEditar!!.id!!, requestBody).execute()
                }

                if (isAdded) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                        binding.btnGuardar.isEnabled = true
                        binding.btnGuardar.text = "Actualizar producto"
                    }
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnGuardar.isEnabled = true
                    binding.btnGuardar.text = "Actualizar producto"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(producto: Producto): EditarProductoFragment {
            val fragment = EditarProductoFragment()
            val args = Bundle()
            args.putSerializable("producto", producto)
            fragment.arguments = args
            return fragment
        }
    }
}
