package de.fhe.ai.colivingpilot.wg

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.databinding.FragmentWgBinding
import kotlinx.coroutines.launch

class WgFragment : Fragment() {

    private val viewmodel: WgViewmodel by viewModels()

    private lateinit var binding: FragmentWgBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_wg, container, false)
    }

    /**
     * Initializes the UI and sets up event handlers for user interactions.
     *
     * Configures the user list RecyclerView with adapters and click listeners, allowing for user
     * interaction long clicks on user items. It also handles UI changes for
     * group name editing, including showing and hiding input fields and buttons based on the edit mode.
     *
     * Listens for touch events on the root view to exit edit mode when clicking outside editable areas.
     * Observes ViewModel states for updating UI components such as user list, group name, and edit mode.
     * Collects UI events from the ViewModel to execute actions like navigation, showing snackbars,
     * dialogues, and updating emojis or group names.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userAdapter = UserUiItemAdapter(
            onClick = { username ->
                viewmodel.onEvent(WgEvent.OnClickUser(username))
            },
            onLongClick = { user ->
                viewmodel.onEvent(WgEvent.OnLongClickUser(user))
            }
        )

        binding = FragmentWgBinding.bind(view)
        binding.apply {
            fabAddUser.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_wg_to_addUserDialogFragment)
            }
            rvSettingsUser.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            rvSettingsUser.adapter = userAdapter

            etGroupName.setOnEditorActionListener { view, actionId, event ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    viewmodel.onEvent(WgEvent.OnChangeWgName(etGroupName.text.toString()))
                    true
                } else {
                    false
                }
            }
            ibEdit.setOnClickListener {
                viewmodel.onEvent(WgEvent.OnClickEditWgButton)
            }

            root.setOnTouchListener(View.OnTouchListener { v, event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    if (binding.etGroupName.visibility == View.VISIBLE) {
                        viewmodel.onEvent(WgEvent.OnClickOutsideEditMode)
                        true
                    }
                }
                false
            })
            ibSettings.setOnClickListener {
                viewmodel.onEvent(WgEvent.OnSettingsClick)
            }
        }


        viewmodel.userUiItems.observe(viewLifecycleOwner) { userList ->
            userAdapter.userList = userList
            userAdapter.notifyDataSetChanged()
        }

        viewmodel.wgName.observe(viewLifecycleOwner) {
            binding.tvGroupName.text = it
            binding.etGroupName.setText(it)
        }
        viewmodel.wgFragmentState.observe(viewLifecycleOwner) {
            if (it.isEditMode) {
                binding.tvGroupName.visibility = View.GONE
                binding.etGroupName.visibility = View.VISIBLE
                binding.ibEdit.visibility = View.GONE

                binding.etGroupName.setSelection(binding.etGroupName.text.length)
                binding.etGroupName.requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(
                    binding.etGroupName,
                    android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
                )
            } else {
                binding.tvGroupName.visibility = View.VISIBLE
                binding.etGroupName.visibility = View.GONE
                binding.ibEdit.visibility = View.VISIBLE
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        lifecycleScope.launch {
            viewmodel.uiEvent.collect { uiEvent ->
                when (uiEvent) {
                    is UiEvent.PopBackStack -> {
                    }

                    is UiEvent.Navigate -> {
                        when (uiEvent.route) {
                            "user" -> {
                                //navigate to user
                            }

                            "settings" -> {
                                findNavController().navigate(R.id.action_navigation_wg_to_navigation_settings)
                            }
                        }
                    }

                    is UiEvent.ShowSnackbar -> {
                        val message = getString(uiEvent.message)
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                    }

                    is UiEvent.ShowUserLongClickDialog -> {
                        val bundle = Bundle()
                        bundle.putString("username", uiEvent.username)
                        bundle.putString("id", uiEvent.id)
                        findNavController().navigate(R.id.action_navigation_wg_to_userLongClickDialogFragment, bundle)
                    }

                    is UiEvent.updateEmoji -> {
                        userAdapter.notifyDataSetChanged()
                    }

                    is UiEvent.updateWgName -> {
                        binding.tvGroupName.text = uiEvent.wgName
                    }

                    is UiEvent.activateEditMode -> {
                        binding.tvGroupName.visibility = View.GONE
                        binding.etGroupName.visibility = View.VISIBLE
                        binding.ibEdit.visibility = View.GONE
                        binding.etGroupName.setSelection(binding.etGroupName.text.length)
                        binding.etGroupName.requestFocus()
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.showSoftInput(
                            binding.etGroupName,
                            android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
                        )
                    }

                    is UiEvent.deactivateEditMode -> {
                        binding.tvGroupName.visibility = View.VISIBLE
                        binding.etGroupName.visibility = View.GONE
                        binding.ibEdit.visibility = View.VISIBLE
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
    }
}

