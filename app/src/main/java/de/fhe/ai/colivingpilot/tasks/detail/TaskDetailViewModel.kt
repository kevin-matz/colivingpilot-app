package de.fhe.ai.colivingpilot.tasks.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.fhe.ai.colivingpilot.model.Task
import de.fhe.ai.colivingpilot.storage.Repository

/**
 * ViewModel for managing detailed information about a task.
 *
 * This ViewModel provides data related to a specific task, such as its title, notes, and beer count.
 *
 * @param id The ID of the task for which details are requested.
 * @see Task
 * @see Repository
 * @author Dario Daßler
 */
class TaskDetailViewModel(id: String)
    : ViewModel() {
    private val repository : Repository = Repository()
    val task : LiveData<Task> = repository.getTask(id).asLiveData()
}