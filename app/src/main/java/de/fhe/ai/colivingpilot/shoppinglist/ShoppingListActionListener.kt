package de.fhe.ai.colivingpilot.shoppinglist

import de.fhe.ai.colivingpilot.model.ShoppingListItem

/**
 * This interface establishes a contract for handling various types of user interactions
 * with items in the shopping list, such as taps, long presses, and checkbox toggles.
 *
 * @author Hendrik Lendeckel
 */
interface ShoppingListActionListener {
    fun onItemChecked(id: String, isChecked: Boolean)
    fun onItemLongClick(id: String)
    fun onItemClicked(item: ShoppingListItem)
}
