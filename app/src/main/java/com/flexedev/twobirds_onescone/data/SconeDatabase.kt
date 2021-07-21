package com.flexedev.twobirds_onescone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.flexedev.twobirds_onescone.data.entities.Rating
import com.flexedev.twobirds_onescone.data.entities.RatingDao
import com.flexedev.twobirds_onescone.data.entities.Scone
import com.flexedev.twobirds_onescone.data.entities.SconeDao


@Database(entities = [Scone::class, Rating::class], version = 4, exportSchema = false)
abstract class SconeDatabase: RoomDatabase () {
    abstract fun sconeDao(): SconeDao
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile
        private var INSTANCE: SconeDatabase? = null

        fun getDatabase(context: Context): SconeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SconeDatabase::class.java,
                    "scone_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance

                instance
            }
        }
    }
}