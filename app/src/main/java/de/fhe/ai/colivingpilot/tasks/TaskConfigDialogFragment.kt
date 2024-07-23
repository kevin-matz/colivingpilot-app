package de.fhe.ai.colivingpilot.tasks

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.databinding.FragmentTaskConfigDialogBinding
import de.fhe.ai.colivingpilot.network.NetworkResult
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.tasks.detail.TaskDetailViewModel

/**
 * BottomSheetDialogFragment for configuring tasks.
 *
 * This dialog allows users to add, update, or delete tasks. It also displays pre-filled data for updating tasks.
 *
 * @see TaskViewModel
 * @author Dario Daßler
 */
class TaskConfigDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTaskConfigDialogBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel = TaskViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskConfigDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = arguments?.getString("selectedTask")

        // Populate the dialog with existing task data for updating
        if (!taskId.isNullOrBlank()) {
            val taskDetailViewModel = TaskDetailViewModel(taskId)
            taskDetailViewModel.task.observe(viewLifecycleOwner) { task ->
                if (task != null) {
                    binding.taskNameEditText.setText(task.title)
                    binding.notesTextView.setText(task.notes)
                    binding.editBeerCounter.setText(task.beerReward.toString())
                }
            }
        }

        // Handle "Abort" button click
        binding.abortButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle "Add" or "Update" button click
        binding.addButton.setOnClickListener {

            var title = binding.taskNameEditText.text.toString()
            var notes = binding.notesTextView.text.toString()
            var beerCountText = binding.editBeerCounter.text.toString()

            title = title.trim()
            notes = notes.trim()
            beerCountText = beerCountText.trim()

            if (title.isBlank() || notes.isBlank() || beerCountText.isBlank()) {
                val taskConfigView = view.findViewById<View>(R.id.taskConfigView)
                Snackbar.make(taskConfigView, "Bitte alle Felder ausfüllen!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
            } else {
                val beerCount = beerCountText.toInt()
                if (taskId == null) {
                    taskViewModel.addTask(title, notes, beerCount,
                        object : NetworkResult<String> {

                            override fun onSuccess(data: String?) {
                                findNavController().navigateUp()
                                if (data != null) {
                                    val bundle = Bundle().apply {
                                        putString("selectedTask", data)
                                    }
                                    findNavController().navigate(
                                        R.id.action_navigation_tasks_to_task_info,
                                        bundle
                                    )
                                }
                            }

                            override fun onFailure(code: String?) {
                            }

                        })
                } else {
                    taskViewModel.updateTask(taskId, title, notes, beerCount,
                        object : NetworkResultNoData {

                            override fun onSuccess() {
                                val bundle = Bundle().apply {
                                    putString("selectedTask", taskId)
                                }
                                findNavController().navigateUp()
                                findNavController().navigate(
                                    R.id.action_navigation_tasks_to_task_info,
                                    bundle
                                )
                            }

                            override fun onFailure(code: String?) {
                            }
                        })
                }
            }
        }

        if (taskId != null) {

            binding.deleteButton.visibility = View.VISIBLE

            binding.deleteButton.setOnClickListener {
                taskViewModel.deleteTask(taskId, object : NetworkResultNoData {
                    override fun onSuccess() {
                        findNavController().navigate(R.id.action_taskConfigDialogFragment_to_navigation_tasks)
                    }

                    override fun onFailure(code: String?) {
                    }
                })
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}