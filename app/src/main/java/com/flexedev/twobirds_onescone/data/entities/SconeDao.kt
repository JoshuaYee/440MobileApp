package com.flexedev.twobirds_onescone.data.entities

import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface SconeDao {
    @Insert
    suspend fun insertScone(scone: Scone): Long

    @Update
    suspend fun updateScone(scone: Scone)

    @Delete
    suspend fun deleteScone(scone: Scone)

    @Query("SELECT * FROM scone")
    fun getAll(): Flow<List<Scone>>

    @Query("SELECT COUNT(*) FROM scone")
    fun getCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM scone as S LEFT JOIN rating as R on S.id = R.sconeId ORDER BY R.score DESC")
    fun getSconesWithRatings(): Flow<List<SconeWithRatings>>

    @Query("SELECT * FROM scone as S LEFT JOIN rating as R on S.id = R.sconeId WHERE S.id = :id")
    fun getSconeWithRating(id: Int): SconeWithRatings

}