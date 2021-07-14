package ru.vlasov.vkcupapp.network.json.vkapi

import ru.vlasov.vkcupapp.models.vkapi.PostData

interface RemoteContentDataSource {
    suspend fun getPosts() : List<PostData>
    suspend fun ignorePost(postData: PostData)
    suspend fun likePost(postData: PostData)
}