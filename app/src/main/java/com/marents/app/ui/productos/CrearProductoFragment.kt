package com.marents.app.ui.productos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marents.app.databinding.FragmentCrearProductoBinding
import java.io.File
import java.io.FileOutputStream
import coil.load
import coil.transform.RoundedCornersTransformation

class CrearProductoFragment : Fragment() {

    private var _binding: FragmentCrearProductoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                try {
                    val file = uriToFile(uri)
                    selectedImageUri = Uri.fromFile(file)
                    binding.ivPreview.load(selectedImageUri) {
                        crossfade(true)
                    }
                    binding.ivPreview.imageTintList = null
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupSpinners() {
        val categorias = arrayOf("Dama", "Caballero", "Niño")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombreModelo.text.toString().trim()
            val precioStr = binding.etPrecio.text.toString().trim()
            val costoStr = binding.etCosto.text.toString().trim()
            val stockStr = binding.etStock.text.toString().trim()
            val tallaStr = binding.etTalla.text.toString().trim()
            val colorStr = binding.etColor.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull() ?: 0.0
            val costo = costoStr.toDoubleOrNull() ?: 0.0
            val stock = stockStr.toIntOrNull() ?: 0
            val talla = tallaStr.toDoubleOrNull() ?: 38.0
            val categoriaNombre = binding.spinnerCategoria.selectedItem.toString()
            val color = colorStr.ifEmpty { "Negro" }

            val imageFile = selectedImageUri?.let { uri -> 
                try { uriToFile(uri) } catch (e: Exception) { null }
            }

            binding.btnGuardar.isEnabled = false
            binding.btnGuardar.text = "GUARDANDO..."

            viewModel.crearProductoFinal(
                nombre, categoriaNombre, color, talla, precio, costo, stock, imageFile
            )
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                binding.btnGuardar.isEnabled = true
                binding.btnGuardar.text = "GUARDAR PRODUCTO"
            }
        }

        viewModel.productos.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "✅ ¡Producto guardado!", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.limpiarError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
