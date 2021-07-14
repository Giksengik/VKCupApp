package ru.vlasov.vkcupapp.repository

import retrofit2.HttpException
import ru.vlasov.vkcupapp.data.VkUserDataHolder
import ru.vlasov.vkcupapp.features.news.NewsViewState
import ru.vlasov.vkcupapp.models.vkapi.PostData
import ru.vlasov.vkcupapp.network.json.vkapi.RemoteContentDataSource
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(

    private val remoteDataSource: RemoteContentDataSource,
    private val vkUserDataHolder: VkUserDataHolder) : NewsRepository{
    private var currentPostsList : List<PostData> = mutableListOf()
    private var postCount = 0

    override fun getAuthViewState(): NewsViewState {
        return if(vkUserDataHolder.isAuth()){
            NewsViewState.Authorized
        }
        else
            NewsViewState.Unauthorized
    }

    override suspend fun getPost(): NewsViewState{
        return if(postCount >= currentPostsList.size){
            if(postCount != 0){
                ignorePost(postData = currentPostsList[postCount - 1])
            }
            loadPosts()
        }else{
            ignorePost(postData = currentPostsList[postCount - 1])
            NewsViewState.Success(currentPostsList[postCount++])
        }
    }


    private suspend fun loadPosts() : NewsViewState {
        return try {
            val res = remoteDataSource.getPosts()
            currentPostsList = res
            postCount = 0
            NewsViewState.Success(currentPostsList[postCount++])
        }catch (e : Exception){
            when(e){
                is IOException -> NewsViewState.Fail.NetworkError
                is HttpException -> {
                    when(e.code()){
                        5 -> {
                            vkUserDataHolder.clearUserData()
                            NewsViewState.Unauthorized
                        }
                        else -> NewsViewState.Fail.NetworkError
                    }
                }
                else -> NewsViewState.Fail.UnexpectedError
            }
        }
    }

    override suspend fun ignorePost(postData: PostData) {
        try {
            remoteDataSource.ignorePost(postData)
        }catch (e : Exception){ }
    }

    override suspend fun likeLastItem() {
        try {
            remoteDataSource.likePost(currentPostsList[postCount - 1])
        }catch (e : Exception){ }

    }


}