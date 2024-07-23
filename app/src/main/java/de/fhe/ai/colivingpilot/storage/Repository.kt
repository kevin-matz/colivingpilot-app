package de.fhe.ai.colivingpilot.storage

import android.util.Log
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.network.RetrofitClient
import de.fhe.ai.colivingpilot.model.ShoppingListItem
import de.fhe.ai.colivingpilot.model.Task
import de.fhe.ai.colivingpilot.model.User
import de.fhe.ai.colivingpilot.network.NetworkResult
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.network.data.request.AddShoppingListItemRequest
import de.fhe.ai.colivingpilot.network.data.request.AddTaskRequest
import de.fhe.ai.colivingpilot.network.data.request.CheckShoppingListItemRequest
import de.fhe.ai.colivingpilot.network.data.request.CreateWgRequest
import de.fhe.ai.colivingpilot.network.data.request.RenameWgRequest
import de.fhe.ai.colivingpilot.network.data.request.UpdateShoppingListItemRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class Repository {
    private val db: WgDatabase = WgDatabase.getInstance(CoLiPiApplication.applicationContext())
    private val userDao: UserDao = db.userDao()
    private val taskDao: TaskDao = db.taskDao()
    private val shoppingListItemDao: ShoppingListItemDao = db.shoppingListItemDao()

    /**
     * Refreshes the app's data by fetching the relevant dataset from the server.
     *
     * @return A pair indicating the success or failure of the operation and an optional error message.
     */
    suspend fun refresh(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getWgData().execute()
            if (!response.isSuccessful) {
                val body = response.errorBody()
                body?.let {
                    val json = JSONObject(body.string())
                    return@withContext Pair(false, json.getString("status"))
                }
                return@withContext Pair(false, "")
            }

            val body = response.body()
            body?.let { resp ->
                val wgName = resp.data.wg.name
                val wgCode = resp.data.wg.invitationCode
                val wgMaxMembers = resp.data.wg.maximumMembers
                val creator = resp.data.wg.creator

                val existingWgName = CoLiPiApplication.instance.keyValueStore.readString("wg_name")
                val existingWgCode = CoLiPiApplication.instance.keyValueStore.readString("wg_code")
                val existingWgMaxMembers = CoLiPiApplication.instance.keyValueStore.readInt("wg_max_members")

                if (wgName != existingWgName || wgCode != existingWgCode || wgMaxMembers != existingWgMaxMembers) {
                    CoLiPiApplication.instance.keyValueStore.writeString("wg_name", wgName)
                    CoLiPiApplication.instance.keyValueStore.writeString("wg_code", wgCode)
                    CoLiPiApplication.instance.keyValueStore.writeInt("wg_max_members", wgMaxMembers)
                }

                val existingUsers = db.userDao().getAll()
                val existingShoppingListItems = db.shoppingListItemDao().getAll()
                val existingTasks = db.taskDao().getAll()

                val newUsers = resp.data.wg.members.filter { member -> !existingUsers.any { it.id == member.id } }.map { user ->
                    User(user.id, user.username, user.beercounter, user.id == creator.id)
                }
                val updatedUsers = resp.data.wg.members.filter { member -> existingUsers.any { it.id == member.id && (it.username != member.username || it.beerCounter != member.beercounter) } }.map { user ->
                    User(user.id, user.username, user.beercounter, user.id == creator.id)
                }
                val deletedUsers = existingUsers.filter { user -> !resp.data.wg.members.any { it.id == user.id } }

                val newShoppingListItems = resp.data.wg.shoppingList.filter { item -> !existingShoppingListItems.any { it.id == item.id } }.map { item ->
                    ShoppingListItem(item.id, item.title, item.notes, item.creator.id, item.isChecked)
                }
                val updatedShoppingListItems = resp.data.wg.shoppingList.filter { item -> existingShoppingListItems.any { it.id == item.id && (it.title != item.title || it.notes != item.notes || it.isChecked != item.isChecked) } }.map { item ->
                    ShoppingListItem(item.id, item.title, item.notes, item.creator.id, item.isChecked)
                }
                val deletedShoppingListItems = existingShoppingListItems.filter { item -> !resp.data.wg.shoppingList.any { it.id == item.id } }

                val newTasks = resp.data.wg.tasks.filter { task -> !existingTasks.any { it.id == task.id } }.map { task ->
                    Task(task.id, task.title, task.description, task.beerbonus)
                }
                val updatedTasks = resp.data.wg.tasks.filter { task -> existingTasks.any { it.id == task.id && (it.title != task.title || it.notes != task.description || it.beerReward != task.beerbonus) } }.map { task ->
                    Task(task.id, task.title, task.description, task.beerbonus)
                }
                val deletedTasks = existingTasks.filter { task -> !resp.data.wg.tasks.any { it.id == task.id } }

                if (newUsers.isNotEmpty()) {
                    db.userDao().insert(*newUsers.toTypedArray())
                }
                if (updatedUsers.isNotEmpty()) {
                    db.userDao().update(*updatedUsers.toTypedArray())
                }
                if (deletedUsers.isNotEmpty()) {
                    db.userDao().delete(*deletedUsers.toTypedArray())
                }

                if (newShoppingListItems.isNotEmpty()) {
                    db.shoppingListItemDao().insert(*newShoppingListItems.toTypedArray())
                }
                if (updatedShoppingListItems.isNotEmpty()) {
                    db.shoppingListItemDao().update(*updatedShoppingListItems.toTypedArray())
                }
                if (deletedShoppingListItems.isNotEmpty()) {
                    db.shoppingListItemDao().delete(*deletedShoppingListItems.toTypedArray())
                }

                if (newTasks.isNotEmpty()) {
                    db.taskDao().insert(*newTasks.toTypedArray())
                }
                if (updatedTasks.isNotEmpty()) {
                    db.taskDao().update(*updatedTasks.toTypedArray())
                }
                if (deletedTasks.isNotEmpty()) {
                    db.taskDao().delete(*deletedTasks.toTypedArray())
                }
            }
            return@withContext Pair(true, "")
        } catch (_: Exception) {
            Log.e(CoLiPiApplication.LOG_TAG, "Failed to fetch WG data")
            return@withContext Pair(false, "")
        }
    }

    fun getUsersFlow(): Flow<List<User>> {
        return userDao.getUsersFlow()
    }

    suspend fun addUser(user: User) {
        userDao.insert(user)
    }

    suspend fun kickUser(username: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.kickFromWg(username).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    suspend fun getUserById(id: String): User {
        return userDao.getUserById(id)
    }

    fun renameWg(name: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.renameWg(RenameWgRequest(name)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()

                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun addTask(title: String, notes: String, beerReward: Int, callback: NetworkResult<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.addTask(AddTaskRequest(title, notes, beerReward)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                val result = response.body()
                if (result == null) {
                    withContext(Dispatchers.Main) { callback.onFailure(null) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess(result.data.id) }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun createWg(request: CreateWgRequest, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createWg(request).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun joinWg(invitationCode: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.joinWg(invitationCode).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun leaveWg(callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.leaveWg().execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun updateTask(id: String, title: String, notes: String, beerReward: Int, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateTask(id, AddTaskRequest(title, notes, beerReward)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun doneTaskById(id: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.doneTask(id).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun deleteTaskById(id: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.removeTask(id).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasks()
    }

    fun getTask(id: String): Flow<Task> {
        return taskDao.getTask(id)
    }


    /**
     *  ShoppingList
     */
    fun getShoppingListItemsFlow(): Flow<List<ShoppingListItem>> {
        return shoppingListItemDao.getShoppingListItemsFlow()
    }

    fun getShoppingListItemById(id: String): Flow<ShoppingListItem> {
        return shoppingListItemDao.getShoppingListItemById(id)
    }

    fun deleteItemFromShoppingList(id: String, callback: NetworkResultNoData){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.removeShoppingListItem(id).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun addShoppingListItem(title: String, notes: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.addShoppingListItem(AddShoppingListItemRequest(title, notes)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun checkShoppingListItem(id: String, checkState: Boolean, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.checkShoppingListItem(id, CheckShoppingListItemRequest(checkState)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

    fun updateShoppingListItem(id: String, title: String, notes: String, callback: NetworkResultNoData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateShoppingListItem(id, UpdateShoppingListItemRequest(title, notes)).execute()
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { callback.onFailure(response.errorBody()?.string()) }
                    return@launch
                }

                refresh()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (_: IOException) {
                withContext(Dispatchers.Main) { callback.onFailure(null) }
            }
        }
    }

}