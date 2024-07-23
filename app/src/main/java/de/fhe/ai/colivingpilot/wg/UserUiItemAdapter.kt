package de.fhe.ai.colivingpilot.wg

import androidx.recyclerview.widget.RecyclerView
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.databinding.ItemUserBinding
import android.view.ViewGroup
import android.view.LayoutInflater

/**
 * Adapter for the RecyclerView in the WgFragment.
 */
class UserUiItemAdapter(
    private val onClick: (String) -> Unit,
    private val onLongClick: (UserUiItem) -> Unit

) : RecyclerView.Adapter<UserUiItemAdapter.UserUiItemViewHolder>() {

    var userList: List<UserUiItem> = emptyList()

    inner class UserUiItemViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a [UserUiItem] to the view.
         *
         * This function sets up the UI elements of the list item with the user's data.
         * It displays the user's username, beer count, and a custom emoji (if available) from shared preferences.
         * It also sets up click and long click listeners for the item view.
         *
         * @param user The [UserUiItem] containing the user data to bind to the view.
         */
        fun bind(user: UserUiItem) {
            binding.listItemContactName.text = user.username
            binding.listItemContactBeerCount.text = user.beerCount.toString()
            val sharedPrefs = CoLiPiApplication.instance.keyValueStore.preferences
            val emoji = sharedPrefs.getString(user.username.toString() + "_emoji", "")
            binding.listItemContactEmoji.text = emoji
            itemView.setOnClickListener {
                onClick(user.username)
            }
            itemView.setOnLongClickListener {
                onLongClick(user)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserUiItemViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserUiItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserUiItemAdapter.UserUiItemViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}