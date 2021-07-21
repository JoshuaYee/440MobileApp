package com.flexedev.twobirds_onescone

import android.app.Application
import com.flexedev.twobirds_onescone.data.SconeDatabase
import com.flexedev.twobirds_onescone.data.SconeRepository

class TwoBirdsLiveRoomApplication : Application() {
    val database by lazy { SconeDatabase.getDatabase(this) }
    val repository by lazy { SconeRepository(database.sconeDao(), database.ratingDao()) }
}
