package de.fhe.ai.colivingpilot.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creator"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShoppingListItem(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    val creator: String,
    @ColumnInfo(name = "is_checked") var isChecked: Boolean //TODO @Kevin ins Backend!
)
