package de.fhe.ai.colivingpilot.wg


/**
 * Represents UI events that can be emitted from the WgFragment UI to trigger actions in the WgViewModel.
 */
sealed class WgEvent{
    data class OnClickUser(val username: String) : WgEvent()
    data class OnLongClickUser(val user: UserUiItem) : WgEvent()
    data class OnChangeWgName(val wgName: String) : WgEvent()
    object OnClickEditWgButton : WgEvent()
    object OnClickOutsideEditMode : WgEvent()
    data class OnDialogOkClick (val user: String, val selectedEmoji : String) : WgEvent()
    object OnDialogCancelClick : WgEvent()
    object OnSettingsClick : WgEvent()
    object OnClickAddUser : WgEvent()
}
