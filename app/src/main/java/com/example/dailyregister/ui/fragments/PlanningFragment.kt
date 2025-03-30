package com.example.dailyregister.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.dailyregister.R
import com.example.dailyregister.data.AppDatabase
import com.example.dailyregister.databinding.FragmentPlanningBinding
import com.example.dailyregister.model.Planning
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PlanningFragment : Fragment() {

    private lateinit var binding: FragmentPlanningBinding
    private lateinit var db: AppDatabase
    private var userId: Int = -1
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanningBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        userId = arguments?.getInt("userId") ?: -1
        if (userId == -1) {
            Toast.makeText(requireContext(), "Utilisateur non reconnu", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return binding.root
        }

        binding.dateInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choisir une date")
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                val selectedDate = dateFormat.format(Date(millis))
                binding.dateInput.setText(selectedDate)
            }

            picker.show(parentFragmentManager, "date_picker")
        }

        binding.savePlanningButton.setOnClickListener {
            val date = binding.dateInput.text.toString()
            val slot1 = binding.input1.text.toString()
            val slot2 = binding.input2.text.toString()
            val slot3 = binding.input3.text.toString()
            val slot4 = binding.input4.text.toString()

            if (date.isBlank() || slot1.isBlank() || slot2.isBlank() || slot3.isBlank() || slot4.isBlank()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val planning = Planning(
                userId = userId,
                date = date,
                slot1 = slot1,
                slot2 = slot2,
                slot3 = slot3,
                slot4 = slot4
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.planningDao().deleteByUserAndDate(userId, date)
                    db.planningDao().insert(planning)
                }

                Toast.makeText(requireContext(), "Planning enregistré", Toast.LENGTH_SHORT).show()


                val bundle = Bundle().apply {
                    putInt("userId", userId)
                }
                findNavController().navigate(R.id.action_planningFragment_to_planningSummaryFragment, bundle)
            }
        }
        binding.viewSummaryButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("userId", userId)
            }
            findNavController().navigate(
                R.id.action_planningFragment_to_planningSummaryFragment,
                bundle
            )
        }


        return binding.root
    }
}
