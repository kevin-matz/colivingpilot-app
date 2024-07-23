package de.fhe.ai.colivingpilot.tasks

/**
 * Data class representing a simplified view of a task for display purposes.
 *
 * @param title The title of the task.
 * @param notes Additional notes for the task.
 * @param beerCount The beer count associated with the task.
 * @author Dario Daßler
 */
data class ViewTask (
    val title: String,
    val notes: String,
    val beerCount: Int
)
