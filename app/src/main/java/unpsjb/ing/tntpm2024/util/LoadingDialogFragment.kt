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

        fun show(
            fragmentManager: FragmentManager,
            message: String = "Cargando..."
        ) {
            // No se realizan transacciones después de guardar el estado.
            if (fragmentManager.isStateSaved) return

            val existing =
                fragmentManager.findFragmentByTag(TAG) as? LoadingDialogFragment

            if (existing != null && existing.isAdded) {
                existing.updateMessage(message)
                return
            }

            val dialog = LoadingDialogFragment().apply {
                this.message = message
            }

            // showNow evita que dos llamadas consecutivas creen dos diálogos.
            dialog.showNow(fragmentManager, TAG)
        }

        fun hide(fragmentManager: FragmentManager) {
            if (fragmentManager.isStateSaved) return

            val existing =
                fragmentManager.findFragmentByTag(TAG) as? LoadingDialogFragment

            existing?.dismissAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogLoadingBinding.inflate(layoutInflater)

        binding.tvLoadingMessage.text = message

        return Dialog(requireContext()).apply {
            setContentView(binding.root)

            window?.setBackgroundDrawableResource(
                android.R.color.transparent
            )

            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    fun updateMessage(newMessage: String) {
        message = newMessage

        if (_binding != null) {
            binding.tvLoadingMessage.text = newMessage
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
