package com.flexedev.twobirds_onescone.data.entities

import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface RatingDao {
    @Insert
    suspend fun insertRating(rating: Rating): Long

    @Update
    suspend fun update(rating: Rating)

    @Delete
    suspend fun deleteRating(rating: Rating)

    @Query("SELECT * FROM rating")
    fun getAll(): Flow<List<Rating>>


    @Query("SELECT * FROM rating WHERE sconeId = :sID")
    fun getSconeScore(sID: Int): Flow<List<Rating>>

}