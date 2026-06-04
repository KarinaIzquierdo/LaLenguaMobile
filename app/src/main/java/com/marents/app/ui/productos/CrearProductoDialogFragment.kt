package com.marents.app.ui.productos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.marents.app.RetrofitClient
import com.marents.app.databinding.DialogCrearProductoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearProductoDialogFragment : DialogFragment() {

    private var _binding: DialogCrearProductoBinding? = null
    private val binding get() = _binding!!
    private var productoAEditar: com.marents.app.Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productoAEditar = arguments?.getSerializable("producto") as? com.marents.app.Producto
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCrearProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupClickListeners()
        
        if (productoAEditar != null) {
            prellenarDatos()
            binding.tvTituloDialog.text = "Editar Producto"
            binding.btnGuardar.text = "Actualizar producto"
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
            
            // Promedio de precios y costos
            val precio = producto.variaciones?.mapNotNull { it.precio?.toDoubleOrNull() }?.average() ?: 0.0
            val costo = producto.variaciones?.mapNotNull { it.costo?.toDoubleOrNull() }?.average() ?: 0.0
            
            binding.etPrecio.setText(precio.toInt().toString())
            binding.etCosto.setText(costo.toInt().toString())
            
            // Seleccionar categoría
            val categoriaNombre = producto.modelo?.categoria?.nombre ?: ""
            val categorias = arrayOf("Seleccionar categoría", "Dama", "Caballero", "Niños")
            val index = categorias.indexOfFirst { it.equals(categoriaNombre, ignoreCase = true) }
            if (index != -1) binding.spinnerCategoria.setSelection(index)
            
            // Tallas (deshabilitar en edición por simplicidad del backend actual)
            binding.spinnerTalla.isEnabled = false
        }
    }

    private fun setupSpinners() {
        // Categorías: Dama, Caballero, Niños
        val categorias = arrayOf("Seleccionar categoría", "Dama", "Caballero", "Niños")
        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = categoriaAdapter

        // Tallas
        val tallas = arrayOf("Seleccionar talla", "T21", "T22", "T23", "T24", "T25", "T26", "T27", "T28", "T29", "T30")
        val tallaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tallas)
        tallaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTalla.adapter = tallaAdapter
    }

    private fun setupClickListeners() {
        binding.btnCerrar.setOnClickListener {
            dismiss()
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            Toast.makeText(requireContext(), "Seleccionar imagen", Toast.LENGTH_SHORT).show()
        }

        binding.btnGuardar.setOnClickListener {
            guardarProducto()
        }
    }

    private fun guardarProducto() {
        val nombre = binding.etNombreModelo.text.toString().trim()
        val color = binding.etColor.text.toString().trim()
        val stock = binding.etStock.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val costo = binding.etCosto.text.toString().trim()
        val categoria = binding.spinnerCategoria.selectedItem.toString()
        val talla = binding.spinnerTalla.selectedItem.toString()

        if (nombre.isEmpty()) {
            binding.etNombreModelo.error = "Ingrese el nombre"
            return
        }

        if (categoria == "Seleccionar categoría") {
            Toast.makeText(requireContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        if (stock.isEmpty()) {
            binding.etStock.error = "Ingrese el stock"
            return
        }

        if (precio.isEmpty()) {
            binding.etPrecio.error = "Ingrese el precio"
            return
        }

        if (costo.isEmpty()) {
            binding.etCosto.error = "Ingrese el costo"
            return
        }

        // Mostrar progreso
        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.text = "Guardando..."

        lifecycleScope.launch {
            try {
                val categoriaId = when (categoria) {
                    "Caballero" -> 1
                    "Dama" -> 2
                    "Niños" -> 3
                    else -> 1
                }

                val tallaNumero = talla.replace("T", "").toIntOrNull() ?: 25

                // Crear request body como Map para enviar como form-data
                val requestBody = hashMapOf(
                    "modelo_nombre" to nombre,
                    "categoria_id" to categoriaId.toString(),
                    "color_primario" to (if (color.isEmpty()) "Negro" else color),
                    "talla_numero" to tallaNumero.toString(),
                    "precio" to precio,
                    "costo" to costo,
                    "stock" to stock
                )

                val response = withContext(Dispatchers.IO) {
                    if (productoAEditar != null) {
                        // Actualizar producto existente
                        RetrofitClient.apiService.actualizarProducto(productoAEditar!!.id!!, requestBody as Map<String, String>).execute()
                    } else {
                        // Crear nuevo producto
                        RetrofitClient.apiService.crearProductoSimple(requestBody).execute()
                    }
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                    (parentFragment as? ProductosAdminFragment)?.recargarProductos()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    binding.btnGuardar.isEnabled = true
                    binding.btnGuardar.text = "Guardar producto"
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnGuardar.isEnabled = true
                binding.btnGuardar.text = "Guardar producto"
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(producto: com.marents.app.Producto? = null): CrearProductoDialogFragment {
            val fragment = CrearProductoDialogFragment()
            producto?.let {
                val args = Bundle()
                args.putSerializable("producto", it as java.io.Serializable)
                fragment.arguments = args
            }
            return fragment
        }
    }
}
