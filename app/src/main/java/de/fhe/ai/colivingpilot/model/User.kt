package de.fhe.ai.colivingpilot.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false) val id: String,
    val username: String,
    @ColumnInfo(name = "beer_counter") val beerCounter: Int,
    @ColumnInfo(name = "is_creator") val isCreator: Boolean
)