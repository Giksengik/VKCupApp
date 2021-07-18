package ru.vlasov.vkcupapp.features.taxi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vlasov.vkcupapp.network.json.Coordinates
import ru.vlasov.vkcupapp.repository.MapRepository
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class MapViewModel @Inject constructor(val mapRepository: MapRepository) : ViewModel() {

    private var mapTimer : Timer = Timer()
    private var isRouteSet = false
    private val mutableViewState : MutableLiveData<MapViewState> = MutableLiveData()
    val viewState : LiveData<MapViewState>
    get() = mutableViewState


    fun getLastLocation() {
        viewModelScope.launch(Dispatchers.Default) {
            mutableViewState.postValue(mapRepository.getLastLocationCoordinates())
        }
    }

    fun loadLocationData(coordinates: Coordinates){
        viewModelScope.launch(Dispatchers.Default) {
            mutableViewState.postValue(mapRepository.getLocationByCoordinates(coordinates))
        }
    }

    private fun scheduleLocationDataGetting(){
        mapTimer.schedule(1000) {
            mutableViewState.postValue(MapViewState.OnLocationLabelSet)
        }
    }

    fun onRouteSet(){
        isRouteSet = true
    }

    fun onMapEvent(){
        if(!isRouteSet) {
            mapTimer.cancel()
            mapTimer = Timer()
            scheduleLocationDataGetting()
        }
    }
}