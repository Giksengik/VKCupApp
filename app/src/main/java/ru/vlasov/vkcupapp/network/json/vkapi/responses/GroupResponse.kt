package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GroupResponse (
    @SerialName("response") val groupResponse : List<GroupDataResponse>
)