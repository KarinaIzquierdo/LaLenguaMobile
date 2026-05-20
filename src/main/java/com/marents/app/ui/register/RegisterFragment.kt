package com.marents.app.ui.register

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

/**
 * Fragment de Registro - MVVM Pattern
 * View: Maneja la UI y eventos de usuario
 * ViewModel: RegisterViewModel (lógica de registro)
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    // Inyección de ViewModel usando delegado by viewModels()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    /**
     * Configura los listeners de click
     * - btnBack: Volver atrás (onBackPressed)
     * - btnTogglePassword: Mostrar/ocultar contraseña
     * - btnRegistrar: Ejecutar registro
     * - tvIniciarSesion: Navegar a Login
     */
    private fun setupClickListeners() {
        // Botón atrás - usa el sistema de back stack de fragments
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Toggle mostrar/ocultar contraseña
        binding.btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Toggle mostrar/ocultar confirmar contraseña
        binding.btnToggleConfirmPassword?.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        // Botón registrar
        binding.btnRegistrar.setOnClickListener {
            val nombres = binding.etNombres.text.toString().trim()
            val apellidos = binding.etApellidos.text.toString().trim()
            val documento = binding.etDocumento.text.toString().trim()
            val celular = binding.etCelular.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmarPassword = binding.etConfirmarPassword.text.toString()
            
            // Validar que las contraseñas coincidan
            if (password != confirmarPassword) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            
            // Por ahora usamos solo nombres como nombre completo
            val nombreCompleto = "$nombres $apellidos"
            viewModel.register(nombreCompleto, email, password, confirmarPassword)
        }
        
        // Iniciar sesión - navegar a Login
        binding.tvIniciarSesion.setOnClickListener {
            (activity as? Navigator.Provider)?.getNavigator()?.navigateToLogin()
        }
    }
// comentario de prueba
    /**
     * Cambia la visibilidad de la contraseña
     * Usa InputType para alternar entre PASSWORD y VISIBLE
     */
    private fun togglePasswordVisibility() {
        val isPasswordVisible = binding.etPassword.inputType ==
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

        binding.etPassword.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        
        // Mantiene cursor al final del texto
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    /**
     * Cambia la visibilidad de la confirmación de contraseña
     */
    private fun toggleConfirmPasswordVisibility() {
        val isPasswordVisible = binding.etConfirmarPassword.inputType ==
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

        binding.etConfirmarPassword.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        
        // Mantiene cursor al final del texto
        binding.etConfirmarPassword.setSelection(binding.etConfirmarPassword.text.length)
    }

    /**
     * Observa los StateFlows del ViewModel
     * - isLoading: Muestra/oculta ProgressBar y deshabilita botón
     * - error: Muestra mensajes de error en Toast
     * - registerSuccess: Navega a Home cuando el registro es exitoso
     * 
     * Usa repeatOnLifecycle para auto-cancelar cuando el Fragment no está visible
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                // Observa estado de carga
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.btnRegistrar.isEnabled = !isLoading
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Observa errores
                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.limpiarError()
                        }
                    }
                }

                // Observa éxito de registro
                launch {
                    viewModel.registerSuccess.collect { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                            // Navega a selección de categorías antes del Home
                            (activity as? Navigator.Provider)?.getNavigator()?.navigateToCategories()
                            viewModel.resetRegisterSuccess()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
