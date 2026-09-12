package unpsjb.ing.tntpm2024.turnos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.AsignacionTurno
import unpsjb.ing.tntpm2024.basededatos.entidades.Turno
import unpsjb.ing.tntpm2024.databinding.FragmentListaTurnosBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import java.util.Locale
import unpsjb.ing.tntpm2024.databinding.DialogAsignarTurnoBinding

class ListaTurnosFragment : Fragment() {

    private var _binding: FragmentListaTurnosBinding? = null
    private val binding get() = _binding!!

    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var turnosAdapter: TurnosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTurnosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenemos el Application context y configuramos el ViewModel
        val application = requireActivity().application
        val database = EncuestasDatabase.getInstance(application)
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]
        binding.toolbarTurnos.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
        observarSolicitudes()
    }

    private fun setupRecyclerView() {
        turnosAdapter = TurnosAdapter(emptyList()) { turnoSeleccionado ->
            abrirDialogoAsignacion(turnoSeleccionado)
        }

        binding.rvTurnosSolicitados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = turnosAdapter
        }
    }

    private fun observarSolicitudes() {
        // En fragments SIEMPRE se usa viewLifecycleOwner para evitar fugas al cambiar de vista
        encuestaViewModel.obtenerTurnosSolicitados().observe(viewLifecycleOwner) { listaTurnos ->
            if (listaTurnos.isNullOrEmpty()) {
                binding.emptyStateTurnos.visibility = View.VISIBLE
                binding.rvTurnosSolicitados.visibility = View.GONE
            } else {
                binding.emptyStateTurnos.visibility = View.GONE
                turnosAdapter.actualizarLista(listaTurnos)
                binding.rvTurnosSolicitados.visibility = View.VISIBLE
            }
        }
    }

    private fun abrirDialogoAsignacion(turno: Turno) {
        val dialogBinding = DialogAsignarTurnoBinding.inflate(layoutInflater)
        dialogBinding.tvVoluntarioSubtitle.text = "Voluntario ID: ${turno.voluntarioId}"

        val calendario = Calendar.getInstance()

        // Selector de Fecha
        dialogBinding.etFecha.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    dialogBinding.etFecha.setText(fechaFormateada)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Selector de Hora
        dialogBinding.etHora.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    dialogBinding.etHora.setText(horaFormateada)
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            ).show()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Confirmar y Asignar", null) // Se setea null para validar antes de cerrar
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val botonConfirmar = dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE)
            botonConfirmar.setOnClickListener {
                val fecha = dialogBinding.etFecha.text.toString().trim()
                val hora = dialogBinding.etHora.text.toString().trim()
                val lugar = dialogBinding.etLugar.text.toString().trim()
                val indicaciones = dialogBinding.etIndicaciones.text.toString().trim()

                if (fecha.isEmpty() || hora.isEmpty() || lugar.isEmpty()) {
                    Toast.makeText(requireContext(), "Completá fecha, hora y lugar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val asignacion = AsignacionTurno(
                    fecha = fecha,
                    hora = hora,
                    lugar = lugar,
                    indicaciones = indicaciones
                )

                encuestaViewModel.asignarTurno(
                    turnoId = turno.turnoId,
                    asignacion = asignacion,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Turno asignado exitosamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        // Al cambiar el estado a "ASIGNADO", Firebase quitará automáticamente
                        // este ítem del RecyclerView gracias al listener de "SOLICITADO"
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Liberación de la referencia al ViewBinding
        _binding = null
    }
}