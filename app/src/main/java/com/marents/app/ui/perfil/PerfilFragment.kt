package com.marents.app.ui.perfil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marents.app.databinding.FragmentPerfilBinding

import android.widget.Toast
import com.marents.app.Navigator

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarDatosUsuario()
        setupClickListeners()
    }

    private fun cargarDatosUsuario() {
        // En el futuro, esto se llenará con los datos del usuario logueado
        binding.etNombreUsuario.setText("Usuario Marents")
        binding.etEmailUsuario.setText("contacto@calzadomarents.com")
    }

    private fun setupClickListeners() {
        binding.btnGuardarPerfil.setOnClickListener {
            val nuevoNombre = binding.etNombreUsuario.text.toString()
            val nuevoEmail = binding.etEmailUsuario.text.toString()

            if (nuevoNombre.isNotEmpty() && nuevoEmail.isNotEmpty()) {
                Toast.makeText(requireContext(), "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
                // Aquí iría la llamada al servidor para guardar realmente
            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnInstagram.setOnClickListener {
            openUrl("https://www.instagram.com/calzadomarents.tuestilo")
        }

        binding.btnFacebook.setOnClickListener {
            openUrl("https://www.facebook.com/calzadomarents.tuestilo")
        }

        binding.btnTikTok.setOnClickListener {
            openUrl("https://www.tiktok.com/@calzadomarents_7")
        }

        binding.btnCerrarSesion.setOnClickListener {
            // Regresar al Welcome/Login
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToLogin()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
