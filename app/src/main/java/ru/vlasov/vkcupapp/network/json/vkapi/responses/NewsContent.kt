package ru.vlasov.vkcupapp.network.json.vkapi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class NewsContent (
    @SerialName("type") val type : String,
    @SerialName("source_id") val sourceId : Long,
    @SerialName("date") val date : Long,
    @SerialName("post_id") val postId : Long,
    @SerialName("post_type") val postType : String,
    @SerialName("text") val text : String,
    @SerialName("comments") val comments : CommentResponse,
    @SerialName("likes") val likes : LikeResponse,
    @SerialName("reposts") val reposts : RepostResponse,
    @SerialName("attachments") val attachments : List<PhotoAttachmentResponse> = listOf()
)