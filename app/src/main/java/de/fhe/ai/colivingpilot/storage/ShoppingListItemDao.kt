package de.fhe.ai.colivingpilot.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.fhe.ai.colivingpilot.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {

    @Insert
    fun insert(vararg item: ShoppingListItem)

    @Update
    fun update(vararg item: ShoppingListItem)

    @Delete
    fun delete(vararg item: ShoppingListItem)

    @Query("SELECT * FROM shopping_list_items")
    fun getAll(): List<ShoppingListItem>

    @Query("SELECT * FROM shopping_list_items")
    fun getShoppingListItemsFlow(): Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM shopping_list_items WHERE id = :id")
    fun getShoppingListItemById(vararg id: String): Flow<ShoppingListItem>

}