package ru.vlasov.vkcupapp.features.news

import ru.vlasov.vkcupapp.models.vkapi.PostData
import ru.vlasov.vkcupapp.network.json.vkapi.responses.PostContentResponse
import ru.vlasov.vkcupapp.network.json.vkapi.responses.PostResponse

sealed class NewsViewState{
    object Unauthorized : NewsViewState()
    object Authorized : NewsViewState()
    object Loading : NewsViewState()
    object OperationComplete : NewsViewState()
    object EndOfContent : NewsViewState()
    class Success(val postData: PostData) : NewsViewState()

    class Fail{
        object NetworkError : NewsViewState()
        object UnexpectedError : NewsViewState()
    }
}

