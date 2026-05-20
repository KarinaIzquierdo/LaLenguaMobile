package com.marents.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.marents.app.AppRoutes
import com.marents.app.MainActivity
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.btnLogin.isEnabled = !isLoading
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        if (!isAdded) return@collect
                        error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.limpiarError()
                        }
                    }
                }

                launch {
                    viewModel.loginSuccess.collect { success ->
                        if (!isAdded) return@collect
                        if (success) {
                            val user = viewModel.user.value
                            
                            // GUARDAR ID DE USUARIO EN PREFERENCIAS
                            user?.id?.let { id ->
                                val prefs = requireActivity().getSharedPreferences("marents_prefs", android.content.Context.MODE_PRIVATE)
                                prefs.edit().putInt("user_id", id).apply()
                            }

                            Toast.makeText(requireContext(), "Login exitoso", Toast.LENGTH_SHORT).show()

                            // Verificar el rol del usuario y redirigir accordingly
                            if (user?.rol == "admin") {
                                (activity as? MainActivity)?.navigateToFragment(AppRoutes.ADMIN)
                            } else {
                                // Navegar a selección de categorías para usuarios normales
                                (activity as? MainActivity)?.navigateToFragment(AppRoutes.CATEGORIES)
                            }
                            viewModel.resetLoginSuccess()
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
