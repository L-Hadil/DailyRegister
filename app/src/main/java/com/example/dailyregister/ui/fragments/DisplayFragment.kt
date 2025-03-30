package com.example.dailyregister.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.dailyregister.R
import com.example.dailyregister.data.AppDatabase
import com.example.dailyregister.databinding.FragmentDisplayBinding
import kotlinx.coroutines.launch

class DisplayFragment : Fragment() {

    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        val summary = arguments?.getString("user_summary") ?: return binding.root
        displayUserInfo(summary)

        binding.actionButton.setOnClickListener {
            val login = binding.loginValue.text.toString()
            val bundle = Bundle().apply {
                putString("prefilled_login", login)
            }
            findNavController().navigate(R.id.action_displayFragment_to_loginFragment, bundle)
        }
        binding.deleteButton.setOnClickListener {
            val login = binding.loginValue.text.toString()

            lifecycleScope.launch {
                AppDatabase.getInstance(requireContext()).userDao().deleteByLogin(login)
                Toast.makeText(requireContext(), "Compte supprimé", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_displayFragment_to_loginFragment)
            }
        }

        return binding.root
    }

    private fun displayUserInfo(summary: String) {
        val lines = summary.lines().associate {
            val (label, value) = it.split(":", limit = 2).map { it.trim() }
            label to value
        }

        binding.loginValue.text = lines["Login"]
        binding.firstNameValue.text = lines["Nom"]?.split(" ")?.getOrNull(0)
        binding.lastNameValue.text = lines["Nom"]?.split(" ")?.getOrNull(1)
        binding.birthDateValue.text = lines["Date de naissance"]
        binding.phoneValue.text = lines["Téléphone"]
        binding.emailValue.text = lines["Email"]
        binding.interestsValue.text = lines["Master"]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
