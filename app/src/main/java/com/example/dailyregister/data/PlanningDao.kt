package com.example.dailyregister.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dailyregister.model.Planning

@Dao
interface PlanningDao {
    @Insert
    suspend fun insert(planning: Planning)

    @Query("SELECT * FROM plannings WHERE userId = :userId LIMIT 1")
    suspend fun getPlanningByUser(userId: Int): Planning?

    @Query("DELETE FROM plannings WHERE userId = :userId")
    suspend fun deleteByUser(userId: Int)
    @Query("SELECT * FROM plannings WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getPlanningByUserAndDate(userId: Int, date: String): Planning?

    @Query("DELETE FROM plannings WHERE userId = :userId AND date = :date")
    suspend fun deleteByUserAndDate(userId: Int, date: String)
    @Query("SELECT * FROM plannings WHERE userId = :userId ORDER BY date ASC")
    suspend fun getAllPlanningsForUser(userId: Int): List<Planning>


}