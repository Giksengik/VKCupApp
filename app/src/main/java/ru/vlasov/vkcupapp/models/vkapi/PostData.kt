package ru.vlasov.vkcupapp.models.vkapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.vlasov.vkcupapp.network.json.vkapi.responses.GroupDataResponse
import ru.vlasov.vkcupapp.network.json.vkapi.responses.NewsContent

@Serializable
data class PostData (
    @SerialName("content") val content : NewsContent,
    @SerialName("group_info")val groupInfo : GroupDataResponse
)