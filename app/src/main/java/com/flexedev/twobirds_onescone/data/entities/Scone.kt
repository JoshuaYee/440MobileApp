package com.flexedev.twobirds_onescone.data.entities

import androidx.room.*

@Entity(tableName = "scone")
data class Scone(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name="scone_name")
    var sconeName: String,
    @ColumnInfo
    var sconeBusiness: String,
    @ColumnInfo(defaultValue = "40.29")
    var latitude: String,
    @ColumnInfo(defaultValue = "174.88")
    var longitude: String,
    @ColumnInfo(name="file_name")
    var image: String
)

data class SconeWithRatings(
    @Embedded val scone: Scone,
    @Relation(
        parentColumn = "id",
        entityColumn = "sconeId"
    )
    val ratings: List<Rating>
)