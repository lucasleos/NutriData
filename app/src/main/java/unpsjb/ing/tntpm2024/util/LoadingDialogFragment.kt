package unpsjb.ing.tntpm2024.util

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import unpsjb.ing.tntpm2024.databinding.DialogLoadingBinding

class LoadingDialogFragment : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!

    private var message: String = "Cargando..."

    companion object {
        private const val TAG = "LoadingDialogFragment"

        fun show(fragmentManager: FragmentManager, message: String = "Cargando...") {
            // Evita agregar el diálogo dos veces si ya está visible
            val existing = fragmentManager.findFragmentByTag(TAG)
            if (existing != null) return

            val dialog = LoadingDialogFragment()
            dialog.message = message
            dialog.show(fragmentManager, TAG)
        }

        fun hide(fragmentManager: FragmentManager) {
            val existing = fragmentManager.findFragmentByTag(TAG) as? LoadingDialogFragment
            existing?.dismissAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogLoadingBinding.inflate(layoutInflater)
        binding.tvLoadingMessage.text = message

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false) // que no se cierre tocando afuera o con "back"
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}