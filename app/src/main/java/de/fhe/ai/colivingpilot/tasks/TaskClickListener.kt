package de.fhe.ai.colivingpilot.tasks

/**
 * Interface for handling click events on tasks in the RecyclerView.
 */
interface TaskClickListener {
    fun onFinishButtonClick(id: String)
    fun onItemClick(id: String)
    fun onLongItemClick(id: String)
}