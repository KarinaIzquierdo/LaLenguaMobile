package com.marents.app.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.marents.app.AppRoutes
import com.marents.app.MainActivity
import com.marents.app.Navigator
import com.marents.app.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnIniciar.setOnClickListener {
            // Ir a Login
            (activity as? MainActivity)?.navigateToFragment(AppRoutes.LOGIN)
        }

        binding.btnRegistro.setOnClickListener {
            // Ir a Registro
            (activity as? MainActivity)?.navigateToFragment(AppRoutes.REGISTER)
        }

        binding.tvInvitado.setOnClickListener {
            // Iniciar la actividad que tiene el menú inferior (Navbar)
            val intent = android.content.Intent(requireContext(), com.marents.app.MainMenuActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Opcional: cerrar la pantalla de bienvenida
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
