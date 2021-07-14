package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RepostResponse  (
    @SerialName("count") val count : Long,
    @SerialName("user_reposted") val userReposted : Int
        )