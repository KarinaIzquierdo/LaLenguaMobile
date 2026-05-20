package com.marents.app.ui.productos

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.marents.app.R
import com.marents.app.RetrofitClient
import com.marents.app.databinding.ActivityGestionStockGlobalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class GestionStockGlobalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionStockGlobalBinding
    private var categorias: List<com.marents.app.Categoria> = emptyList()
    private var modelos: List<com.marents.app.Modelo> = emptyList()
    private var colores: List<String> = listOf("Negro", "Blanco", "Rojo", "Azul", "Café", "Verde", "Amarillo", "Rosa")
    private var tallas: List<Int> = (20..45).toList()
    private var tallasSeleccionadas: MutableSet<Int> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionStockGlobalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupTallasGrid()
        setupBotones()
        cargarCategorias()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinners() {
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && categorias.isNotEmpty()) {
                    val categoriaId = categorias[position - 1].id
                    cargarModelosPorCategoria(categoriaId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val colorAdapter = ArrayAdapter(this, R.layout.item_spinner, colores)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = colorAdapter
    }

    private fun setupTallasGrid() {
        binding.gridTallas.removeAllViews()
        tallasSeleccionadas.clear()

        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val margin = (6 * density).toInt()
        val itemWidth = (52 * density).toInt() // Ancho fijo para cada talla
        val itemsPerRow = 5 // 5 tallas por fila

        // Calcular cuántas filas necesitamos
        val totalRows = (tallas.size + itemsPerRow - 1) / itemsPerRow

        for (rowIndex in 0 until totalRows) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                if (rowIndex > 0) {
                    setPadding(0, margin, 0, 0)
                }
            }

            val startIndex = rowIndex * itemsPerRow
            val endIndex = minOf(startIndex + itemsPerRow, tallas.size)

            for (i in startIndex until endIndex) {
                val talla = tallas[i]
                val toggle = android.widget.ToggleButton(this).apply {
                    text = "T$talla"
                    textOn = "T$talla"
                    textOff = "T$talla"
                    textSize = 13f
                    isChecked = false
                    setTextColor(android.graphics.Color.parseColor("#374151"))
                    setBackgroundResource(R.drawable.bg_talla_toggle)
                    layoutParams = LinearLayout.LayoutParams(itemWidth, (44 * density).toInt()).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            tallasSeleccionadas.add(talla)
                            setTextColor(android.graphics.Color.WHITE)
                            setBackgroundResource(R.drawable.bg_talla_toggle_selected)
                        } else {
                            tallasSeleccionadas.remove(talla)
                            setTextColor(android.graphics.Color.parseColor("#374151"))
                            setBackgroundResource(R.drawable.bg_talla_toggle)
                        }
                    }
                }
                rowLayout.addView(toggle)
            }

            // Agregar espacio vacío si la fila no está completa (para alineación)
            val remainingItems = itemsPerRow - (endIndex - startIndex)
            for (j in 0 until remainingItems) {
                val spacer = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(itemWidth, (44 * density).toInt()).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                }
                rowLayout.addView(spacer)
            }

            binding.gridTallas.addView(rowLayout)
        }
    }

    private fun setupBotones() {
        binding.btnCancelar.setOnClickListener {
            finish()
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

                val nombres = mutableListOf("Seleccionar categoría...")
                nombres.addAll(categorias.map { it.nombre ?: "" })

                val adapter = ArrayAdapter(this@GestionStockGlobalActivity, R.layout.item_spinner, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@GestionStockGlobalActivity, "Error cargando categorías", Toast.LENGTH_SHORT).show()
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

                val nombres = mutableListOf("Seleccionar modelo...")
                nombres.addAll(modelos.map { it.nombre ?: "" })

                val adapter = ArrayAdapter(this@GestionStockGlobalActivity, R.layout.item_spinner, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerModelo.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@GestionStockGlobalActivity, "Error cargando modelos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarStock() {
        val cantidad = binding.etCantidad.text.toString().toIntOrNull()
        val modeloIndex = binding.spinnerModelo.selectedItemPosition

        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (modeloIndex <= 0 || modelos.isEmpty()) {
            Toast.makeText(this, "Seleccione un modelo", Toast.LENGTH_SHORT).show()
            return
        }

        if (tallasSeleccionadas.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos una talla", Toast.LENGTH_SHORT).show()
            return
        }

        val modelo = modelos.getOrNull(modeloIndex - 1)
        val color = binding.spinnerColor.selectedItem?.toString()

        Toast.makeText(
            this,
            "Agregando $cantidad unidades de ${modelo?.nombre} ($color) a ${tallasSeleccionadas.size} tallas",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }
}
