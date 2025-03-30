package com.example.dailyregister.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.dailyregister.R
import com.example.dailyregister.data.AppDatabase
import com.example.dailyregister.databinding.FragmentPlanningSummaryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlanningSummaryFragment : Fragment() {

    private lateinit var binding: FragmentPlanningSummaryBinding
    private lateinit var db: AppDatabase
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanningSummaryBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        userId = arguments?.getInt("userId") ?: -1
        if (userId == -1) return binding.root

        loadPlannings()

        return binding.root
    }

    private fun loadPlannings() {
        lifecycleScope.launch {
            val plannings = withContext(Dispatchers.IO) {
                db.planningDao().getAllPlanningsForUser(userId)
            }

            val container = binding.summaryContainer
            container.removeAllViews()

            if (plannings.isEmpty()) {
                binding.summaryTitle.visibility = View.GONE
                return@launch
            }

            binding.summaryTitle.visibility = View.VISIBLE

            for (p in plannings) {
                val card = layoutInflater.inflate(R.layout.item_planning_summary, container, false)

                val text = card.findViewById<TextView>(R.id.summaryText)
                val delete = card.findViewById<View>(R.id.deleteButton)

                text.text = """
                    📅 ${p.date}
                    08h-10h: ${p.slot1}
                    10h-12h: ${p.slot2}
                    14h-16h: ${p.slot3}
                    16h-18h: ${p.slot4}
                """.trimIndent()

                delete.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            db.planningDao().deleteByUserAndDate(userId, p.date)
                        }
                        loadPlannings()
                        Toast.makeText(requireContext(), "Planning supprimé", Toast.LENGTH_SHORT).show()
                    }
                }

                container.addView(card)
            }
        }
    }
}
