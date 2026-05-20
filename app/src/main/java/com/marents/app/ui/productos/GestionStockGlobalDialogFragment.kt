package com.marents.app.ui.productos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import android.widget.LinearLayout
import android.widget.ToggleButton
import com.marents.app.R
import com.marents.app.RetrofitClient
import com.marents.app.databinding.DialogGestionStockGlobalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class GestionStockGlobalDialogFragment : DialogFragment() {

    private var _binding: DialogGestionStockGlobalBinding? = null
    private val binding get() = _binding!!

    private var categorias: List<com.marents.app.Categoria> = emptyList()
    private var modelos: List<com.marents.app.Modelo> = emptyList()
    private var colores: List<String> = listOf("Negro", "Blanco", "Rojo", "Azul", "Café")
    private var tallas: List<Int> = (20..45).toList()
    private var tallasSeleccionadas: MutableSet<Int> = mutableSetOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogGestionStockGlobalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCerrarButton()
        setupSpinners()
        setupTallasChips()
        setupBotones()
        cargarCategorias()
    }

    private fun setupCerrarButton() {
        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }

    private fun setupSpinners() {
        // Categoría
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && categorias.isNotEmpty()) {
                    val categoriaId = categorias[position - 1].id
                    cargarModelosPorCategoria(categoriaId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Color
        val colorAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, colores)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = colorAdapter
    }

    private fun setupTallasChips() {
        binding.layoutTallas.removeAllViews()
        tallasSeleccionadas.clear()

        tallas.forEach { talla ->
            val toggle = ToggleButton(requireContext()).apply {
                text = "T$talla"
                textOn = "T$talla"
                textOff = "T$talla"
                isChecked = false
                setBackgroundResource(R.drawable.bg_talla)
                setPadding(16, 8, 16, 8)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 8, 0)
                layoutParams = params
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tallasSeleccionadas.add(talla)
                        setBackgroundResource(R.drawable.bg_talla_selected)
                    } else {
                        tallasSeleccionadas.remove(talla)
                        setBackgroundResource(R.drawable.bg_talla)
                    }
                }
            }
            binding.layoutTallas.addView(toggle)
        }
    }

    private fun setupBotones() {
        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        binding.btnGuardar.setOnClickListener {
            guardarStock()
        }
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getCategorias().await()
                }
                categorias = response

                val nombres = mutableListOf("Seleccionar...")
                nombres.addAll(categorias.map { it.nombre ?: "" })

                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarModelosPorCategoria(categoriaId: Int?) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getModelos().await()
                }

                modelos = response.filter { it.categoriaId == categoriaId }

                val nombres = mutableListOf("Selecciona categoría")
                if (modelos.isNotEmpty()) {
                    nombres.clear()
                    nombres.add("Seleccionar modelo...")
                    nombres.addAll(modelos.map { it.nombre ?: "" })
                }

                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerModelo.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando modelos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarStock() {
        val cantidad = binding.etCantidad.text.toString().toIntOrNull()
        val modeloIndex = binding.spinnerModelo.selectedItemPosition
        val color = binding.spinnerColor.selectedItem?.toString()

        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (modeloIndex <= 0 || modelos.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione un modelo", Toast.LENGTH_SHORT).show()
            return
        }

        if (tallasSeleccionadas.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione al menos una talla", Toast.LENGTH_SHORT).show()
            return
        }

        val modelo = modelos.getOrNull(modeloIndex - 1)

        // Aquí iría la llamada a la API para guardar el stock
        Toast.makeText(
            requireContext(),
            "Agregando $cantidad unidades a ${modelo?.nombre} (${tallasSeleccionadas.size} tallas)",
            Toast.LENGTH_LONG
        ).show()

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): GestionStockGlobalDialogFragment {
            return GestionStockGlobalDialogFragment()
        }
    }
}
