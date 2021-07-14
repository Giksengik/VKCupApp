package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PhotoResponse(
    @SerialName("album_id") val albumId : Long = 0,
    @SerialName("date") val date : Long = 0,
    @SerialName("id") val id : Long = 0,
    @SerialName("owner_id") val ownerId : Long = 0,
    @SerialName("hasTags") val hasTags : Boolean = false,
    @SerialName("access_key") val accessKey : String = "",
    @SerialName("height") val height : Long = 0,
    @SerialName("photo_1280") val photo_1280Url : String = "",
    @SerialName("photo_604") val photo_604Url : String = "",
    @SerialName("photo_130") val photo_130Url : String = "",
    @SerialName("photo_75") val photo_75Url : String = "",
    @SerialName("photo_807") val photo_807Url : String = "",
    @SerialName("user_id") val userId : Long = 0,
    @SerialName("width") val width : Long = 0,
    )

