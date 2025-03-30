package com.example.dailyregister.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val phone: String,
    val email: String,
    val Master: String
)
