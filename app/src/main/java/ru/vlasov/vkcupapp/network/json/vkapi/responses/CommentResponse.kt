package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CommentResponse(
    @SerialName("count") val count : Long,
    @SerialName("can_post") val canPost : Int
)