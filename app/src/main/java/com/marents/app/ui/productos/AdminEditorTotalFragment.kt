package com.marents.app.ui.productos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.marents.app.Producto
import com.marents.app.RetrofitClient
import com.marents.app.R
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class AdminEditorTotalFragment : DialogFragment() {

    private var producto: Producto? = null
    private var imageUri: Uri? = null
    private lateinit var ivPreview: ImageView

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            imageUri = it
            try {
                requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}

            ivPreview.load(it) {
                crossfade(true)
                placeholder(R.drawable.ic_shoe_placeholder)
                error(R.drawable.ic_shoe_placeholder)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        producto = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("producto", Producto::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("producto") as? Producto
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.admin_fragment_editor_total, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.ivProductoPreview)
        val etNombre = view.findViewById<EditText>(R.id.etNombreModelo)
        val etColor = view.findViewById<EditText>(R.id.etColor)
        val etStock = view.findViewById<EditText>(R.id.etStock)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)
        val etCosto = view.findViewById<EditText>(R.id.etCosto)
        val spinnerCat = view.findViewById<Spinner>(R.id.spinnerCategoria)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val btnCambiarFoto = view.findViewById<View>(R.id.btnCambiarFoto)

        val categorias = arrayOf("Hombre", "Mujer", "Niño", "Pisa huevos", "Outlet")
        spinnerCat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)

        producto?.let {
            etNombre.setText(it.modelo?.nombre ?: "")
            if (!it.imagen.isNullOrEmpty()) ivPreview.load(it.imagen)

            val variacion = it.variaciones?.firstOrNull()
            etColor.setText(variacion?.colorPrimario?.nombre ?: "")
            etStock.setText(it.variaciones?.sumOf { v -> v.stock ?: 0 }?.toString() ?: "0")

            val precio = it.variaciones?.mapNotNull { v -> v.precio?.toDoubleOrNull() }?.average() ?: 0.0
            val costo = it.variaciones?.mapNotNull { v -> v.costo?.toDoubleOrNull() }?.average() ?: 0.0

            etPrecio.setText(String.format(Locale.US, "%.0f", precio))
            etCosto.setText(String.format(Locale.US, "%.0f", costo))

            val categoriaNombre = it.modelo?.categoria?.nombre ?: ""
            val catIndex = categorias.indexOfFirst { c -> c.equals(categoriaNombre, ignoreCase = true) }
            if (catIndex != -1) spinnerCat.setSelection(catIndex)
        }

        btnBack.setOnClickListener { dismiss() }
        btnCambiarFoto.setOnClickListener { selectImageLauncher.launch(arrayOf("image/*")) }

        btnGuardar.setOnClickListener {
            guardarCambios(etNombre.text.toString())
        }
    }

    private fun guardarCambios(nuevoNombre: String) {
        if (nuevoNombre.isEmpty()) return

        val btnGuardar = view?.findViewById<Button>(R.id.btnGuardar)
        btnGuardar?.isEnabled = false
        btnGuardar?.text = "PROCESANDO..."

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val imagePart = imageUri?.let { uri ->
                        val file = uriToFile(uri)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("imagen", file.name, requestFile)
                    }

                    if (imagePart != null) {
                        val nameBody = nuevoNombre.toRequestBody("text/plain".toMediaTypeOrNull())
                        RetrofitClient.apiService.actualizarProductoConImagen(
                            producto?.id ?: 0,
                            nameBody,
                            imagePart
                        ).execute()
                    } else {
                        val params = hashMapOf("modelo_nombre" to nuevoNombre)
                        RetrofitClient.apiService.actualizarProducto(producto?.id ?: 0, params).execute()
                    }
                }

                if (isAdded) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "¡Actualizado!", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        btnGuardar?.isEnabled = true
                        btnGuardar?.text = "GUARDAR CAMBIOS"
                    }
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                    btnGuardar?.isEnabled = true
                    btnGuardar?.text = "GUARDAR CAMBIOS"
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val file = File(requireContext().cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    companion object {
        fun newInstance(producto: Producto) = AdminEditorTotalFragment().apply {
            arguments = Bundle().apply { putSerializable("producto", producto) }
        }
    }
}
