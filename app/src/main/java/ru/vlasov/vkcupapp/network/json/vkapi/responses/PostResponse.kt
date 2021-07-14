package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PostResponse (
    @SerialName("items") val items : List<NewsContent>,
    @SerialName("next_from") val nextFrom : String
        )