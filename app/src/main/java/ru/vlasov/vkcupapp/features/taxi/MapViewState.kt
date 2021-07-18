package ru.vlasov.vkcupapp.features.taxi

import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.network.json.Coordinates

sealed class MapViewState {
    class Error {
        class UndefinedLastLocation(val locationData: LocationData) : MapViewState()
        object NetworkError : MapViewState()
    }
    class Success{
        class OnLocationDataLoaded(val locationData: LocationData) : MapViewState()
        class OnLastLocationDefined(val locationData: LocationData) : MapViewState()
    }
    object OnLocationLabelSet : MapViewState()
}