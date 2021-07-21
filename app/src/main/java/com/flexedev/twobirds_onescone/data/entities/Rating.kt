package com.flexedev.twobirds_onescone.data.entities


import androidx.room.*


@Entity(tableName = "rating", foreignKeys = [ForeignKey(
    entity = Scone::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("sconeId"),
    onDelete = ForeignKey.CASCADE
)]
)
data class Rating(
    @PrimaryKey(autoGenerate = true)
    var ratingId: Long = 0,
    @ColumnInfo(index = true, name = "sconeId")
    var sconeId: Long,
    @ColumnInfo(name = "score")
    var score: String,
    @ColumnInfo
    var value: String,
    @ColumnInfo
    var texture: String,
    @ColumnInfo
    var flavour: String,
    @ColumnInfo
    var notes: String
)
