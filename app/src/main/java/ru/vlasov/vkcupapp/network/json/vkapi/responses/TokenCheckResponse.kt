package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TokenCheckResponse(@SerialName("response") val tokenResponseData: TokenResponseData = TokenResponseData() )