package de.fhe.ai.colivingpilot.wg


/**
 * Represents UI events that can be emitted from the WgViewModel to trigger actions in the WgFragment.
 */
sealed class UiEvent {

    object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        val message: Int,
        val action: String? = null,
    ) : UiEvent()

    data class ShowUserLongClickDialog(
        val username: String,
        val id: String,
    ) : UiEvent()

    data class updateEmoji(
        val username: String, val emoji: String
    ) : UiEvent()

    data class updateWgName(
        val wgName: String
    ) : UiEvent()

    object activateEditMode : UiEvent()

    object deactivateEditMode : UiEvent()
}