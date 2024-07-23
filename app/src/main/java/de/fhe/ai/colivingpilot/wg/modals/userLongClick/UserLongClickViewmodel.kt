package de.fhe.ai.colivingpilot.wg.modals.userLongClick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.storage.Repository
import kotlinx.coroutines.launch


/**
 * ViewModel for handling actions from the UserLongClickDialogFragment.
 * This ViewModel facilitates operations such as updating a user's emoji
 * and removing a user from the WG.
 */
class UserLongClickViewmodel : ViewModel(){
    private val repository = Repository()

    /**
     * Updates the emoji for a given user in shared preferences.
     *
     * @param emoji The new emoji to associate with the user.
     * @param username The username of the user whose emoji is being updated.
     */
    fun onDialogOkClick(emoji: String, username: String){
        val sharedPrefs = CoLiPiApplication.instance.keyValueStore
        sharedPrefs.writeString(username + "_emoji", emoji)
    }

    /**
     * Initiates the deletion of a user by their ID.
     *
     * The function fetches the user from the repository by ID and then requests
     *
     * @param id The unique identifier of the user to delete.
     */

    fun onDeleteUserClick(username: String){
        viewModelScope.launch {
            repository.kickUser(username, object : NetworkResultNoData {
                override fun onSuccess() {
                }

                override fun onFailure(code: String?) {
                }
            })
        }
    }
}