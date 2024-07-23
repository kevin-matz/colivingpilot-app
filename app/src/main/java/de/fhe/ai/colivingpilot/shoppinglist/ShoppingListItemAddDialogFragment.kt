package de.fhe.ai.colivingpilot.shoppinglist

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.databinding.FragmentShoppingListItemAddDialogBinding
import de.fhe.ai.colivingpilot.network.NetworkResultNoData

/**
 * BottomSheetDialogFragment for adding a new item to the shopping list.
 *
 * This dialog allows the user to input a title and notes for a new shopping list item and adds it to the list.
 *
 * @see ShoppingListViewModel
 * @author Hendrik Lendeckel
 */
class ShoppingListItemAddDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentShoppingListItemAddDialogBinding? = null
    private val shoppingListViewModel: ShoppingListViewModel = ShoppingListViewModel()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShoppingListItemAddDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle "Add" button click
        binding.btnAdd.setOnClickListener {

            val itemTitle = binding.editTextTitle.text.toString()
            val itemNotes = binding.editTextNotes.text.toString()

            // Validate and add the item to the shopping list
            if (itemTitle.isNotEmpty()) {
                shoppingListViewModel.addItemToShoppingList(itemTitle, itemNotes,
                    object : NetworkResultNoData {
                        override fun onSuccess() {
                            // Navigate to the shopping list after a successful addition
                            findNavController().navigate(R.id.action_shoppingListItemConfigDialogFragment_to_navigation_shoppinglist)
                        }
                        override fun onFailure(code: String?) {
                            Snackbar.make(view, "Fehler beim Schreiben der Online DB!", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.RED)
                                .show()
                        }
                    })
            } else {
                Snackbar.make(view, "Bitte alle Felder ausfüllen!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}