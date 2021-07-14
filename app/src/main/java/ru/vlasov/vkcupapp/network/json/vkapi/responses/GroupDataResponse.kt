package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class GroupDataResponse(
    @SerialName("id") val id : Long,
    @SerialName("name") val name : String,
    @SerialName("screen_name") val overhear_mtu : String,
    @SerialName("is_closed") val isClosed : Int = 0,
    @SerialName("type") val type : String,
    @SerialName("is_admin") val isAdmin : Int = 0,
    @SerialName("is_member") val isMember : Int = 0,
    @SerialName("is_advertiser") val isAdvertiser : Int = 0,
    @SerialName("description") val description : String = "",
    @SerialName("photo_50") val photo_50Url : String,
    @SerialName("photo_100") val photo_100Url : String,
    @SerialName("photo_200") val photo_200Url : String
    )
