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
import com.example.dailyregister.databinding.FragmentLoginBinding
import com.example.dailyregister.utils.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_startFragment)
        }

        binding.loginButton.setOnClickListener {
            val login = binding.loginInput.text.toString()
            val password = binding.passwordInput.text.toString()

            lifecycleScope.launch {
                val hashedPassword = hashPassword(password)
                val user = withContext(Dispatchers.IO) {
                    db.userDao().login(login, hashedPassword)
                }


                if (user != null) {
                    Toast.makeText(requireContext(), "Connexion réussie", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle().apply {
                        putInt("userId", user.id)
                    }

                    findNavController().navigate(
                        R.id.action_loginFragment_to_planningFragment,
                        bundle
                    )
                } else {
                    Toast.makeText(requireContext(), "Échec de connexion", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}
