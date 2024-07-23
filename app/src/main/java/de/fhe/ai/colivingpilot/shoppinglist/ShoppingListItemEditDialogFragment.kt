package de.fhe.ai.colivingpilot.shoppinglist

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.databinding.FragmentShoppingListItemEditDialogBinding
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * BottomSheetDialogFragment for editing an existing shopping list item.
 *
 * This dialog allows the user to modify the title and notes of a specific shopping list item.
 *
 * @see ShoppingListViewModel
 * @author Hendrik Lendeckel
 */
class ShoppingListItemEditDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentShoppingListItemEditDialogBinding? = null
    private val shoppingListViewModel: ShoppingListViewModel = ShoppingListViewModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentShoppingListItemEditDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    /**
     * Initializes the dialog views and sets up the data based on the selected shopping list item.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = arguments?.getString("selectedItem") ?: return

        // Observe the data changes for the selected item using a coroutine
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shoppingListViewModel.getShoppingListItemById(itemId)
                    .catch { e ->
                        Log.e(CoLiPiApplication.LOG_TAG, "Error getting shopping list item by ID", e)
                    }
                    .collect { item ->
                        @Suppress("SENSELESS_COMPARISON") // this "senseless" comparison prevents crashes when refresh is called
                        if (item == null)
                            return@collect

                        binding.editTextTitle.setText(item.title)
                        binding.editTextNotes.setText(item.notes)
                    }
            }
        }

        binding.btnAdd.setOnClickListener {

            val itemTitle = binding.editTextTitle.text.toString()
            val itemNotes = binding.editTextNotes.text.toString()

            if (itemTitle.isNotEmpty()) {
                shoppingListViewModel.updateShoppingListItem(itemId, itemTitle, itemNotes,
                    object : NetworkResultNoData {
                        override fun onSuccess() {
                            findNavController().navigate(R.id.action_shoppingListItemEditDialogFragment_to_navigation_shoppinglist)
                        }

                        override fun onFailure(code: String?) {
                            Snackbar.make(view, "Fehler beim Updaten der Online DB!", Snackbar.LENGTH_LONG)
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