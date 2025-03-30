package com.example.dailyregister.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.dailyregister.R
import com.example.dailyregister.data.AppDatabase
import com.example.dailyregister.databinding.FragmentRegistrationBinding
import com.example.dailyregister.model.User
import com.example.dailyregister.utils.ValidationUtils
import com.example.dailyregister.utils.hashPassword
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        binding.submitButton.setOnClickListener {
            handleRegistration()
        }

        setupBirthDateMask()

        return binding.root
    }

    private fun handleRegistration() {
        val login = binding.loginInput.text.toString().trim()
        val rawPassword = binding.passwordInput.text.toString().trim()
        val password = hashPassword(rawPassword)

        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val birthDate = binding.birthDateInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val master = getSelectedInterests()

        if (!validateInputs(login, password, email)) return

        lifecycleScope.launch {
            val existingUser = withContext(Dispatchers.IO) {
                db.userDao().getByLogin(login)
            }

            if (existingUser != null) {
                showError(binding.loginInputLayout, "Login déjà utilisé")
                return@launch
            }

            val user = User(
                login = login,
                password = password,
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                phone = phone,
                email = email,
                Master = master
            )

            withContext(Dispatchers.IO) {
                db.userDao().insert(user)
            }

            val summary = """
                Login : $login
                Nom : $firstName $lastName
                Date de naissance : $birthDate
                Téléphone : $phone
                Email : $email
                Master : $master
            """.trimIndent()

            val bundle = Bundle().apply {
                putString("user_summary", summary)
            }

            findNavController().navigate(
                R.id.action_registrationFragment_to_displayFragment,
                bundle
            )
        }
    }

    private fun getSelectedInterests(): String {
        return binding.interestsChipGroup.children
            .filterIsInstance<Chip>()
            .filter { it.isChecked }
            .joinToString(", ") { it.text.toString() }
    }

    private fun validateInputs(login: String, password: String, email: String): Boolean {
        var isValid = true

        if (!ValidationUtils.isValidLogin(login)) {
            showError(binding.loginInputLayout, "Login invalide (lettre, max 10 caractères)")
            isValid = false
        } else {
            binding.loginInputLayout.error = null
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showError(binding.passwordInputLayout, "Mot de passe trop court (min 6 caractères)")
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError(binding.emailInputLayout, "Email invalide")
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        return isValid
    }

    private fun setupBirthDateMask() {
        binding.birthDateInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val unmasked = s.toString().replace("[^\\d]".toRegex(), "")
                val result = StringBuilder()

                var i = 0
                for (char in mask) {
                    if (char == '#') {
                        if (i < unmasked.length) {
                            result.append(unmasked[i])
                            i++
                        } else break
                    } else {
                        result.append(char)
                    }
                }

                binding.birthDateInput.setText(result.toString())
                binding.birthDateInput.setSelection(result.length)
                isUpdating = false
            }
        })
    }

    private fun showError(inputLayout: com.google.android.material.textfield.TextInputLayout, message: String) {
        inputLayout.error = message
        showToast(message)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
