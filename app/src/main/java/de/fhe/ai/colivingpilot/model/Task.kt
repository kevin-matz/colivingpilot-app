package de.fhe.ai.colivingpilot.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    /*foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creator"],
            onDelete = ForeignKey.CASCADE
        )
    ]*/
)
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    @ColumnInfo(name = "beer_reward") val beerReward: Int
)
