package ru.vlasov.vkcupapp.repository

import ru.vlasov.vkcupapp.features.news.NewsViewState
import ru.vlasov.vkcupapp.models.vkapi.PostData

interface NewsRepository {

    fun getAuthViewState() : NewsViewState

    suspend fun getPost() : NewsViewState

    suspend fun ignorePost(postData: PostData)

    suspend fun likeLastItem()
}