package ru.vlasov.vkcupapp.features.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.vlasov.vkcupapp.models.vkapi.PostData
import ru.vlasov.vkcupapp.repository.NewsRepository
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepository: NewsRepository): ViewModel() {

    private val mutableNewsViewState : MutableLiveData<NewsViewState> = MutableLiveData()
    val newsViewState : LiveData<NewsViewState>
    get() = mutableNewsViewState

    fun checkForAuthorized(){
       mutableNewsViewState.value = newsRepository.getAuthViewState()
    }

    fun loadContent(){
        mutableNewsViewState.value = NewsViewState.Loading
        viewModelScope.launch (Dispatchers.Default){
                delay(500)
                mutableNewsViewState.postValue(newsRepository.getPost())

        }
    }

    fun likePost(){
            viewModelScope.launch(Dispatchers.Default) {
                newsRepository.likeLastItem()
            }

    }
}