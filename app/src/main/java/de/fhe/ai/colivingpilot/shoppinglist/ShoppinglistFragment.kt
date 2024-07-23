package de.fhe.ai.colivingpilot.shoppinglist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.databinding.FragmentShoppinglistBinding
import de.fhe.ai.colivingpilot.model.ShoppingListItem
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.util.IRefreshable

/**
 * Fragment for displaying and managing the shopping list.
 *
 * This fragment handles the UI for the shopping list, including the RecyclerView for displaying items,
 * refresh functionality, and interaction with the view model.
 *
 * @author Hendrik Lendeckel
 */
class ShoppinglistFragment : Fragment(R.layout.fragment_shoppinglist), ShoppingListActionListener, IRefreshable {

    private lateinit var binding: FragmentShoppinglistBinding
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var rvShoppingListItems: RecyclerView
    private val shoppingListViewModel: ShoppingListViewModel = ShoppingListViewModel(this)
    private var swipeRefreshLayout : SwipeRefreshLayout? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shoppinglist, container, false)
    }

    // Called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentShoppinglistBinding.bind(view)

        setupRecyclerView(view)

        // Set up SwipeRefreshLayout for manual refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            shoppingListViewModel.refresh()
        }

        // Navigate to add item dialog
        binding.btnAddItemToShoppingList.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_shoppinglist_to_shoppingListItemConfigDialogFragment)
        }

        // Delete completed shopping items
        binding.btnDeleteDoneShoppingItems.setOnClickListener {
            shoppingListViewModel.deleteDoneItems()
        }
    }

    // Called when an item is checked in the RecyclerView
    override fun onItemChecked(id: String, isChecked: Boolean) {
        shoppingListViewModel.toggleIsChecked(id, isChecked, object : NetworkResultNoData {
            override fun onSuccess() {
            }

            override fun onFailure(code: String?) {
            }
        })
    }

    override fun onItemLongClick(id: String) {
        val bundle = Bundle().apply {
            putString("selectedItem", id)
        }
        findNavController().navigate(R.id.action_navigation_shoppinglist_to_shoppingListItemEditDialogFragment, bundle)
    }

    // Called when an item is clicked in the RecyclerView
    override fun onItemClicked(item: ShoppingListItem) {
        val itemPosition = shoppingListAdapter.items.indexOf(item)
        val viewHolder = rvShoppingListItems.findViewHolderForAdapterPosition(itemPosition) as? ShoppingListAdapter.ShoppingListViewHolder
        viewHolder?.let { shoppingListAdapter.toggleNoteVisibility(it) }
    }

    /**
     * Initializes and sets up the RecyclerView for displaying shopping list items.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(view: View) {
        val itemsList = mutableListOf<ShoppingListItem>()
        shoppingListAdapter = ShoppingListAdapter(requireContext(), this, itemsList)

        rvShoppingListItems = binding.rvShoppingListItems

        rvShoppingListItems.adapter = shoppingListAdapter
        rvShoppingListItems.layoutManager = LinearLayoutManager(requireContext())

        shoppingListViewModel.shoppingListItems.observe(viewLifecycleOwner) { newList ->
            shoppingListAdapter.items = newList
            shoppingListAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Implementation of the refreshInterface callback to signal the end of the refresh operation.
     */
    override fun refreshFinish() {
        binding.swipeRefreshLayout.isRefreshing = false
    }
}
