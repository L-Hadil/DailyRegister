package com.example.dailyregister.utils

import java.security.MessageDigest

object ValidationUtils {
    fun isValidLogin(login: String): Boolean = login.matches(Regex("^[a-zA-Z][a-zA-Z0-9]{0,9}$"))
    fun isValidPassword(password: String): Boolean = password.length >= 6
    fun isValidEmail(email: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
fun hashPassword(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}