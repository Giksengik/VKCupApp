package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PhotoAttachmentResponse (
    @SerialName("type") val type : String = "",
    @SerialName("photo") val photo : PhotoResponse = PhotoResponse()
        )