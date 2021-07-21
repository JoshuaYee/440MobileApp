package com.flexedev.twobirds_onescone.data

import androidx.annotation.WorkerThread
import com.flexedev.twobirds_onescone.data.entities.*
import kotlinx.coroutines.flow.Flow

class SconeRepository(private val sconeDao: SconeDao, private val ratingDao: RatingDao) {
    val scones: Flow<List<SconeWithRatings>> = sconeDao.getSconesWithRatings()
    val numScones: Flow<Int> = sconeDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(scone: Scone) {
        sconeDao.insertScone(scone)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSconeWithRatings(scone: Scone, rating: Rating) {
        val sId = sconeDao.insertScone(scone)
        rating.sconeId = sId
        ratingDao.insertRating(rating)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateSconeAndRating(scone: Scone, rating: Rating) {
        sconeDao.updateScone(scone)
        rating.sconeId = scone.id
        ratingDao.update(rating)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteSconeWithRatings(sconeWithRatings: SconeWithRatings) {
        sconeDao.deleteScone(sconeWithRatings.scone)
        ratingDao.deleteRating(sconeWithRatings.ratings[0])
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSconesWithRatings() {
        sconeDao.getSconesWithRatings()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateScone(scone: Scone) {
        sconeDao.updateScone(scone)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteScone(scone: Scone) {
        sconeDao.deleteScone(scone)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSconeScore(id: Int) {
        ratingDao.getSconeScore(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getOne(id: Int) {
        sconeDao.getSconeWithRating(id)
    }

}