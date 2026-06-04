package com.marents.app.ui.carrito

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.marents.app.databinding.FragmentPasarelaPagoBinding

class PasarelaPagoFragment : Fragment() {

    private var _binding: FragmentPasarelaPagoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasarelaPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monto = arguments?.getString("montoTotal") ?: "$0"
        binding.tvMontoTotalPasarela.text = monto

        binding.btnFinalizarPago.setOnClickListener {
            if (validarCampos()) {
                procesarPagoSimulado()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val tarjeta = binding.etNumeroTarjeta.text.toString()
        val nombre = binding.etNombreTitular.text.toString()
        val venc = binding.etVencimiento.text.toString()
        val cvv = binding.etCVV.text.toString()

        if (tarjeta.length < 16) {
            Toast.makeText(requireContext(), "Número de tarjeta inválido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el nombre del titular", Toast.LENGTH_SHORT).show()
            return false
        }
        if (venc.length < 5) {
            Toast.makeText(requireContext(), "Fecha de vencimiento inválida", Toast.LENGTH_SHORT).show()
            return false
        }
        if (cvv.length < 3) {
            Toast.makeText(requireContext(), "CVV inválido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun procesarPagoSimulado() {
        binding.layoutCargandoPago.visibility = View.VISIBLE
        binding.btnFinalizarPago.isEnabled = false

        // Simular espera de 3 segundos como una pasarela real
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.layoutCargandoPago.visibility = View.GONE
                mostrarExito()
            }
        }, 3000)
    }

    private fun mostrarExito() {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("¡Pago Exitoso!")
            .setMessage("Tu pedido ha sido procesado correctamente. ¡Gracias por tu compra!")
            .setCancelable(false)
            .setPositiveButton("ACEPTAR") { _, _ ->
                // Regresar al inicio de la app o cerrar el flujo
                parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
