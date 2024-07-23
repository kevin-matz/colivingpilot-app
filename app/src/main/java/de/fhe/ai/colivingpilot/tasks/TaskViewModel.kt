package de.fhe.ai.colivingpilot.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.fhe.ai.colivingpilot.model.Task
import de.fhe.ai.colivingpilot.network.NetworkResult
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.storage.Repository
import de.fhe.ai.colivingpilot.util.IRefreshable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing tasks.
 *
 * This ViewModel provides data and business logic related to tasks, such as adding, updating, and deleting tasks.
 *
 * @param refreshListener The listener to notify when a data refresh operation has finished.
 * @see Task
 * @see Repository
 * @author Dario Daßler
 */
class TaskViewModel(private val refreshListener: IRefreshable? = null)
    : ViewModel() {
    private val repository : Repository = Repository()
    val tasks : LiveData<List<Task>> = repository.getTasks().asLiveData()

    fun addTask(title: String, notes: String, beerCount: Int, callback: NetworkResult<String>) {
        repository.addTask(title, notes, beerCount, callback)
    }

    fun updateTask(id: String, title: String, notes: String, beerCount: Int, callback: NetworkResultNoData) {
        repository.updateTask(id, title, notes, beerCount, callback)
    }

    fun doneTask(id: String, callback: NetworkResultNoData) {
        repository.doneTaskById(id, callback)
    }

    fun deleteTask(id: String, callback: NetworkResultNoData) {
        repository.deleteTaskById(id, callback)
    }

    /**
     * Initiates a data refresh operation.
     */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refresh()
            refreshListener?.refreshFinish()
        }
    }
}