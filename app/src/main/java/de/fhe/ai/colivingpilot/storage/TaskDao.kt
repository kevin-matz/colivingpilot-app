package de.fhe.ai.colivingpilot.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.fhe.ai.colivingpilot.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    fun insert(vararg task: Task)

    @Update
    fun update(vararg task: Task)

    @Delete
    fun delete(vararg task: Task)

    @Query("SELECT * FROM tasks")
    fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(vararg id: String) : Flow<Task>

    // TODO: removable once Task has reference to User
    @Query("DELETE FROM tasks")
    fun deleteAll()

    @Query("SELECT * FROM tasks")
    fun getTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteByID(vararg id: String)
}