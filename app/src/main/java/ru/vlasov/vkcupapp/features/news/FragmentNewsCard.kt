package ru.vlasov.vkcupapp.features.news

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.databinding.PostCardBinding
import ru.vlasov.vkcupapp.models.vkapi.PostData
import ru.vlasov.vkcupapp.network.json.vkapi.responses.NewsContent
import ru.vlasov.vkcupapp.network.json.vkapi.responses.PostResponse
import ru.vlasov.vkcupapp.utils.DateFormatter
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class FragmentNewsCard : Fragment() {

    companion object{

        private const val CONTENT_KEY = "NEWS_CARD_CONTENT"
        fun newInstance(content : PostData): FragmentNewsCard {
            val args = Bundle()
            val fragment = FragmentNewsCard()
            args.putString(CONTENT_KEY, Json.encodeToString(content))
            fragment.arguments = args
            return fragment
        }
    }

    var binding : PostCardBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostCardBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(arguments != null){
            if(arguments?.getString(CONTENT_KEY) != null){
                val content = Json.decodeFromString<PostData>(arguments?.getString(CONTENT_KEY)?: "")
                bindContent(content)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun bindContent(postData : PostData){
        bindCommonParts(postData)
        if(postData.content.attachments.isNotEmpty()){
            if(postData.content.attachments[0].photo.albumId != 0L){
                if(postData.content.attachments.size == 1 && postData.content.attachments[0].photo.height > 1.5 * postData.content.attachments[0].photo.width) {
                    bindTextPhotoPost(postData)
                }else bindTextPhotoPost(postData)
            }else{
                bindTextPost(postData)
            }
        }
        else bindTextPost(postData)


    }

    private fun bindCommonParts(postData: PostData) {
        binding?.numOfReposts?.text = if(postData.content.reposts.count == 0L) "" else postData.content.reposts.count.toString()
        binding?.numberOfComments?.text = if(postData.content.comments.count == 0L) "" else postData.content.comments.count.toString()
        binding?.numberOfLikes?.text = if(postData.content.likes.count == 0L) "" else postData.content.likes.count.toString()
        try{
            binding?.groupIcon?.load(postData.groupInfo.photo_200Url){
                transformations(CircleCropTransformation())
            }
        }catch (e : Exception){ }
        binding?.groupName?.text = postData.groupInfo.name
        binding?.dateOfPost?.text = DateFormatter.getDateString(postData.content.date, requireContext())
    }

    private fun bindTextPost(postData: PostData){
        binding?.contentPlaceholder?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.turquoise, null))
        binding?.contentDescription?.gravity = Gravity.TOP
        var text = postData.content.text
        setWhiteColorToViews()
        if(text.length > 550)
            text = text.substring(0,550) + "..."
        binding?.contentDescription?.text = text
    }

    // Почти в 100% работает криво..

    private fun bindPhotoPost(postData: PostData){
        context?.let {
            val imageLoader = it.imageLoader
            val request = ImageRequest.Builder(it)
                .data(postData.content.attachments[0].photo.photo_1280Url)
                .build()
            lifecycleScope.launch(Dispatchers.Default) {
                val drawable = imageLoader.execute(request).drawable
                withContext(Dispatchers.Main){
                    binding?.contentPlaceholder?.background = drawable
                }
            }
        }
        setWhiteColorToViews()
        binding?.contentDescription?.setTextColor(Color.WHITE)
        binding?.groupName?.setTextColor(Color.WHITE)
        binding?.backgroundMask?.visibility = View.VISIBLE
        binding?.contentDescription?.text = postData.content.text
        binding?.contentDescription?.setTextSize(TypedValue.COMPLEX_UNIT_SP,21f)
    }

    private fun bindTextPhotoPost(postData: PostData) {
        var text = postData.content.text
        if(text.length > 120)
            text = text.substring(0, 120) + "..."
        binding?.contentDescription?.text = text
        try {
            binding?.contentImage?.load(postData.content.attachments[0].photo.photo_604Url)
        }catch (e : Exception) {}

    }

    private fun setWhiteColorToViews(){
        binding?.imageComments?.setColorFilter(Color.WHITE)
        binding?.imageLikes?.setColorFilter(Color.WHITE)
        binding?.imageReposts?.setColorFilter(Color.WHITE)
        binding?.numberOfLikes?.setTextColor(Color.WHITE)
        binding?.numberOfComments?.setTextColor(Color.WHITE)
        binding?.numOfReposts?.setTextColor(Color.WHITE)
        binding?.contentDescription?.setTextColor(Color.WHITE)
        binding?.groupName?.setTextColor(Color.WHITE)
        binding?.dateOfPost?.setTextColor(Color.WHITE)
    }

    fun setColor(color : Int){
        binding?.backgroundMask?.setBackgroundColor(color)
        binding?.backgroundMask?.visibility = View.VISIBLE
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
    }
}