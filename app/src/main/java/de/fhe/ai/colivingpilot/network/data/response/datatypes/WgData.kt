package de.fhe.ai.colivingpilot.network.data.response.datatypes

import com.google.gson.annotations.SerializedName

data class WgMember(
    @SerializedName("_id")
    val id: String,
    val username: String,
    val beercounter: Int
)

data class WgUser(
    @SerializedName("_id")
    val id: String,
    val username: String
)

data class WgShoppingListItem(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val notes: String,
    val creator: WgUser,
    val isChecked: Boolean
)

data class WgTask(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val description: String,
    val beerbonus: Int
)

data class WgDataGroup(
    val name: String,
    val invitationCode: String,
    val maximumMembers: Int,
    val members: List<WgMember>,
    val creator: WgUser,
    val shoppingList: List<WgShoppingListItem>,
    val tasks: List<WgTask>
)

data class WgData(
    val wg: WgDataGroup
)