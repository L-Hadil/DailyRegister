package com.example.dailyregister.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dailyregister.databinding.FragmentStartBinding
import com.example.dailyregister.R

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return binding.root
    }
}
