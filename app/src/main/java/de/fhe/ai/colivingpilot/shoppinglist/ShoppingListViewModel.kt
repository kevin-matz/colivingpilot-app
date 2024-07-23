package de.fhe.ai.colivingpilot.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.fhe.ai.colivingpilot.model.ShoppingListItem
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.storage.Repository
import de.fhe.ai.colivingpilot.util.IRefreshable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing the shopping list data and interactions.
 *
 * @param refreshListener Interface to notify the UI about the completion of a data refresh.
 * @author Hendrik Lendeckel
 */
class ShoppingListViewModel(private val refreshListener: IRefreshable? = null): ViewModel() {

    private val repository: Repository = Repository()

    // LiveData containing the list of shopping list items
    val shoppingListItems: LiveData<List<ShoppingListItem>> = repository.getShoppingListItemsFlow().asLiveData()


    /**
     * Adds a new shopping list item.
     *
     * @param title The title of the shopping list item.
     * @param notes Notes for the shopping list item.
     * @param callback Callback to handle the network result.
     */
    fun addItemToShoppingList(title: String, notes: String, callback: NetworkResultNoData) {
        repository.addShoppingListItem(title, notes, callback)
    }

    /**
     * Updates an existing shopping list item.
     *
     * @param id The ID of the shopping list item to be updated.
     * @param title The new title for the shopping list item.
     * @param notes The new notes for the shopping list item.
     * @param callback Callback to handle the network result.
     */
    fun updateShoppingListItem(id: String, title: String, notes: String, callback: NetworkResultNoData) {
        repository.updateShoppingListItem(id, title, notes, callback)
    }

    /**
     * Deletes all completed shopping list items.
     */
    fun deleteDoneItems() {
        shoppingListItems.value?.forEach{ item ->
            if (item.isChecked){
                repository.deleteItemFromShoppingList(item.id, object : NetworkResultNoData {
                    override fun onSuccess() {
                    }
                    override fun onFailure(code: String?) {
                    }
                })
            }
        }
    }

    /**
     * Toggles the checked status of a shopping list item.
     *
     * @param id The ID of the shopping list item to be updated.
     * @param isChecked The new checked status for the shopping list item.
     * @param callback Callback to handle the network result.
     */
    fun toggleIsChecked(id: String, isChecked: Boolean, callback: NetworkResultNoData) {
        repository.checkShoppingListItem(id, !isChecked, callback)
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refresh()
            refreshListener?.refreshFinish()
        }
    }

    /**
     * Retrieves a specific shopping list item by its ID.
     *
     * @param id The ID of the shopping list item to be retrieved.
     * @return Flow representing the requested shopping list item.
     */
    fun getShoppingListItemById(id: String): Flow<ShoppingListItem> {
        return repository.getShoppingListItemById(id)
    }
}

