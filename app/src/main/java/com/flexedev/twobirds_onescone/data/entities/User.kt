package com.flexedev.twobirds_onescone.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
class User (
    @PrimaryKey(autoGenerate = true)
    val userId: Int)
