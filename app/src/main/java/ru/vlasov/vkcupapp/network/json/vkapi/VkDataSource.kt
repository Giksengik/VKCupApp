package ru.vlasov.vkcupapp.network.json.vkapi

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import ru.vlasov.vkcupapp.data.VkUserDataHolder
import ru.vlasov.vkcupapp.di.network.VkAPI
import ru.vlasov.vkcupapp.models.vkapi.PostData
import ru.vlasov.vkcupapp.network.json.vkapi.JsonVkApiPlaceholder
import ru.vlasov.vkcupapp.network.json.vkapi.RemoteContentDataSource
import ru.vlasov.vkcupapp.network.json.vkapi.responses.PostContentResponse
import javax.inject.Inject
import kotlin.math.absoluteValue

class VkDataSource @Inject constructor(@VkAPI val api : JsonVkApiPlaceholder, private val vkUserDataHolder: VkUserDataHolder)  :
    RemoteContentDataSource {
    override suspend fun getPosts(): List<PostData> {
            val res = api.getPosts(accessToken = vkUserDataHolder.getToken()).response
            JsonVkApiPlaceholder.START_FROM = res.nextFrom
            return res.items.map { newsContent ->
                delay(20)
                val groupInfo = api.getGroupInfo(accessToken = vkUserDataHolder.getToken(), groupId = newsContent.sourceId.absoluteValue).groupResponse[0]
                PostData(newsContent, groupInfo)
            }
    }

    override suspend fun ignorePost(postData: PostData) {
        api.ignorePost(ownerId = "-${postData.content.sourceId.absoluteValue}",
        itemId = "${postData.content.postId.absoluteValue}", accessToken = vkUserDataHolder.getToken())

    }

    override suspend fun likePost(postData: PostData) {
        if(checkToken()){
        api.likePost(ownerId = "-${postData.content.sourceId.absoluteValue}",
                itemId = "${postData.content.postId.absoluteValue}", accessToken = vkUserDataHolder.getToken())
        }
        else
            throw HttpException(Response.error<String>(5,"{\"message\":\"invalid token\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())))
    }

    private suspend fun checkToken() : Boolean{
        return if(vkUserDataHolder.getToken() == "") false
        else{
            val res = api.checkToken(vkUserDataHolder.getToken())
            res.tokenResponseData.isCorrect == 1
        }
    }
}