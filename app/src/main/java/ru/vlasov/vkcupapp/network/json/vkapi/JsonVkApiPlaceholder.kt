package ru.vlasov.vkcupapp.network.json.vkapi

import retrofit2.http.GET
import retrofit2.http.Query
import ru.vlasov.vkcupapp.network.json.vkapi.responses.*

interface JsonVkApiPlaceholder {

    companion object{
        private const val FILTERS = "post"
        private const val SOURCES = "groups"
        var START_FROM = ""
        private const val VERSION ="5.60"
        private const val COUNT = 5
        private const val TYPE_FOR_IGNORE = "wall"
        private const val TYPE_FOR_LIKE = "post"

    }

    @GET("newsfeed.get")
    suspend fun getPosts(@Query("filters") filters : String = FILTERS, @Query("access_token") accessToken : String,
    @Query("source_ids") sources : String = SOURCES, @Query("start_from") startFrom : String = START_FROM,
    @Query("v") version : String  = VERSION , @Query("count") count : Int = COUNT) : PostContentResponse

    @GET("groups.getById")
    suspend fun getGroupInfo(@Query("access_token") accessToken : String, @Query("group_id") groupId : Long,
        @Query("v") version : String  = VERSION) :
            GroupResponse

    @GET("newsfeed.ignoreItem")
    suspend fun ignorePost(@Query("type") type : String = TYPE_FOR_IGNORE, @Query("owner_id") ownerId : String,
                           @Query("item_id") itemId : String, @Query("access_token") accessToken : String,
                           @Query("v") version : String  = VERSION
    ) : CodeResponse

    @GET("likes.add")
    suspend fun likePost(@Query("type") type : String = TYPE_FOR_LIKE, @Query("owner_id") ownerId : String,
                         @Query("item_id") itemId : String, @Query("access_token") accessToken : String,
                         @Query("v") version : String  = VERSION
    ) : LikePostResponse

    @GET("secure.checkToken")
    suspend fun checkToken(@Query("token") token : String) : TokenCheckResponse
}