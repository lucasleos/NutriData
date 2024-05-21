package unpsjb.ing.tntpm2024.estadistica

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unpsjb.ing.tntpm2024.R

class EstadisticaFragment : Fragment() {

    companion object {
        fun newInstance() = EstadisticaFragment()
    }

    private val viewModel: EstadisticaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_estadistica, container, false)
    }
}