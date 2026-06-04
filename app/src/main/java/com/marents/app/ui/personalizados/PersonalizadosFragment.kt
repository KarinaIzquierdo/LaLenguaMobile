package com.marents.app.ui.personalizados

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentPersonalizadosBinding

class PersonalizadosFragment : Fragment() {

    private var _binding: FragmentPersonalizadosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalizadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            (activity as? Navigator.Provider)?.getNavigator()?.back()
        }

        binding.btnContactanos.setOnClickListener {
            // Abrir WhatsApp o email para contacto
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:contacto@marents.com")
                putExtra(Intent.EXTRA_SUBJECT, "Personalización de Zapatos")
                putExtra(Intent.EXTRA_TEXT, "Hola, me interesa personalizar unos zapatos...")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "No se pudo abrir el correo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnWhatsapp.setOnClickListener {
            val phone = "+573000000000" // Reemplaza con el número real de la tienda
            val message = "Hola Marents! Me interesa personalizar unos zapatos."
            val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
