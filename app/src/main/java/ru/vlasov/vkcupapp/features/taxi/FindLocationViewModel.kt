package ru.vlasov.vkcupapp.features.taxi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import ru.vlasov.vkcupapp.repository.MapRepository
import javax.inject.Inject

@HiltViewModel
class FindLocationViewModel @Inject constructor(private val mapRepository: MapRepository) : ViewModel(){

    private val mutableViewState : MutableLiveData<FindLocationViewState> = MutableLiveData()
    val viewState : LiveData<FindLocationViewState>
    get() = mutableViewState

    fun loadLocationsByQuery(query : String) {
        viewModelScope.launch(Dispatchers.Default) {
            mutableViewState.postValue(mapRepository.loadLocationsByQuery(query))
        }
    }
}