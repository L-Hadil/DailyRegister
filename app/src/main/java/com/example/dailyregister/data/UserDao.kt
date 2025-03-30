package com.example.dailyregister.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dailyregister.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)
    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getByLogin(login: String): User?
    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    suspend fun login(login: String, password: String): User?
    @Query("DELETE FROM users WHERE login = :login")
    suspend fun deleteByLogin(login: String)

}
