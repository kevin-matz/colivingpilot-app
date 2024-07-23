package de.fhe.ai.colivingpilot.tasks.detail

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.databinding.FragmentTaskDetailBinding

/**
 * Fragment for displaying detailed information about a task.
 *
 * This fragment includes the title, notes, and beer count of a task, and allows the user to navigate
 * to the task configuration dialog for updating the task.
 */
class TaskDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val taskId = arguments?.getString("selectedTask")!!

        val taskDetailViewModel = TaskDetailViewModel(taskId)

        binding.taskUpdateButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedTask", taskId)
            }
            findNavController().navigate(R.id.action_task_info_to_taskConfigDialogFragment, bundle)
        }

        // Observe changes in the task data and update the UI accordingly
        taskDetailViewModel.task.observe(viewLifecycleOwner) { task ->
            binding.taskTitleTextView.text = task.title
            binding.taskNotesTextView.text = task.notes
            binding.beerCounterTextView.text = task.beerReward.toString().plus(" 🍺")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}