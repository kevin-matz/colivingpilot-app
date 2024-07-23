package de.fhe.ai.colivingpilot.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.fhe.ai.colivingpilot.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(vararg user: User)

    @Update
    suspend fun update(vararg user: User)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users where id = :id")
    suspend fun getUserById(id: String): User

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * FROM users ORDER by beer_counter DESC")
    fun getUsersFlow(): Flow<List<User>>

    @Delete
    suspend fun delete(vararg user: User)

    @Query("SELECT * FROM users WHERE username = 'Hendrik'")
    fun getTestUser(): User
}