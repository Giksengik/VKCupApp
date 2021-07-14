package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LikeResponse(
    @SerialName("count") val count : Long,
    @SerialName("user_likes") val userLikes : Int,
    @SerialName("can_like") val canLike : Int,
    @SerialName("can_publish") val canPublish : Int
)