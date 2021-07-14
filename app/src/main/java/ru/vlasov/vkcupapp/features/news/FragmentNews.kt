package ru.vlasov.vkcupapp.features.news

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.post_card.*
import kotlinx.coroutines.launch
import ru.vlasov.vkcupapp.AuthProvider
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.databinding.FragmentNewsBinding
import ru.vlasov.vkcupapp.features.FragmentNetworkError
import ru.vlasov.vkcupapp.models.vkapi.PostData

@AndroidEntryPoint
class FragmentNews : Fragment(), FragmentNetworkError.NetworkButtonListener, FragmentUnauthenticated.AuthButtonListener {

    var binding : FragmentNewsBinding? = null
    private val viewModel : NewsViewModel by viewModels()
    var hasNetworkError = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.newsViewState.observe(viewLifecycleOwner) {
            handleState(it)
        }

        binding?.imageButtonDislikePost?.setOnClickListener {
            viewModel.loadContent()
            if (isContentDisplaying()) {
                lifecycleScope.launch {
                    binding?.contentPlaceholder?.let { card ->
                        cardHideFromHide(card)
                        setCardColor(ResourcesCompat.getColor(resources, R.color.transparency_blue, null))
                    }
                    binding?.backgroundCard?.let { card ->
                        backgroundCardShow(card)
                    }
                }
            }
        }

        binding?.imageButtonLikePost?.setOnClickListener{
            viewModel.loadContent()
            if(isContentDisplaying()) {
                lifecycleScope.launch {
                        binding?.contentPlaceholder?.let { card ->
                            viewModel.likePost()
                            cardHideFromLike(card)
                            setCardColor(ResourcesCompat.getColor(resources, R.color.transparency_red, null))
                        }
                        binding?.backgroundCard?.let { card ->
                            backgroundCardShow(card)
                        }
                    }
            }
        }
        viewModel.checkForAuthorized()
    }

    private fun isContentDisplaying() : Boolean =
        childFragmentManager.findFragmentById(R.id.contentPlaceholder) is FragmentNewsCard
                && !hasNetworkError



    private fun handleState(viewState: NewsViewState) =
        when(viewState){
            is NewsViewState.Unauthorized -> {
                setLoaded()
                hasNetworkError = true
                showAuthOfferConfirm()
            }
            is NewsViewState.Authorized ->{
                setLoaded()
                getContent()
                hasNetworkError = false
            }
            is NewsViewState.Fail.NetworkError ->{
                setLoaded()
                showNetworkError()
                hasNetworkError = true
            }
            is NewsViewState.Loading ->{
                setLoading()
            }
            is NewsViewState.EndOfContent ->{
                setLoaded()
                showEndOfContent()
                hasNetworkError = false
            }
            is NewsViewState.Success -> {
                setLoaded()
                hasNetworkError = false
                if (childFragmentManager.findFragmentById(R.id.contentPlaceholder) != null) {
                    binding?.contentPlaceholder?.let {
                        if(binding?.contentPlaceholder?.x!! < 0)
                            cardShowFromHide(it)
                        else cardShowFromLike(it)
                    }
                    binding?.backgroundCard?.let{
                        backgroundCardHide(it)
                    }
                    showContent(viewState.postData)
                }else
                    showContent(viewState.postData)

            }
            else -> getContent()
        }

    private fun showNetworkError() {
        AlertDialog.Builder(context).setMessage(R.string.network_error_text)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    onNetworkButtonClick()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showEndOfContent() {
        AlertDialog.Builder(context).setMessage(getString(R.string.content_over_text))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }.show()
    }
    private fun setLoading() {
        binding?.imageButtonLikePost?.isEnabled = false
        binding?.imageButtonDislikePost?.isEnabled = false
    }

    private fun setLoaded() {
        binding?.imageButtonLikePost?.isEnabled = true
        binding?.imageButtonDislikePost?.isEnabled = true
        binding?.backgroundCard?.visibility = View.VISIBLE
    }

    private fun showContent(postData: PostData) {
        childFragmentManager.beginTransaction()
            .replace(R.id.contentPlaceholder, FragmentNewsCard.newInstance(postData))
            .commit()
        binding?.backgroundCard?.visibility = View.VISIBLE

    }

    private fun setCardColor(color : Int){
        if((childFragmentManager.findFragmentById(R.id.contentPlaceholder) is FragmentNewsCard))
            (childFragmentManager.findFragmentById(R.id.contentPlaceholder) as FragmentNewsCard).setColor(color)
    }

    private fun cardHideFromHide(v: View) {
        v.clearAnimation()
        val X = v.x - 4000
        v.animate().translationX(X).start()
        v.animate().alpha(0f).start()
    }
    private fun cardShowFromHide(v: View) {
        v.clearAnimation()
        val X = v.x + 4000
        v.translationX = X
        v.animate().alpha(1f).start()
    }

    private fun cardHideFromLike(v: View) {
        v.clearAnimation()
        val X = v.x + 4000
        v.animate().translationX(X).start()
        v.animate().alpha(0f).start()
    }
    private fun cardShowFromLike(v: View) {
        v.clearAnimation()
        val X = v.x - 4000
        v.translationX = X
        v.animate().alpha(1f).start()
    }

    private fun backgroundCardShow(v : View) {
        v.clearAnimation()
        v.y = v.y + 30
        v.animate().scaleX(1.2f).start()
    }
    private fun backgroundCardHide(v : View) {
        v.clearAnimation()
        v.y = v.y - 30
        v.animate().scaleX(0.85f).start()

    }


    private fun authorize() {
        activity?.let{
            (it as AuthProvider).auth()
        }
    }

    private fun getContent(){
        viewModel.loadContent()
    }

    private fun showAuthOfferConfirm() {
        AlertDialog.Builder(
                context
        ).setMessage(R.string.auth_offer_text)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                authorize()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onNetworkButtonClick() {

        viewModel.loadContent()
    }

    override fun onAuthButtonClick() {
        activity?.let{
            (it as AuthProvider).auth()
        }
    }

}