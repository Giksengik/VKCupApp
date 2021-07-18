package ru.vlasov.vkcupapp.features.taxi

import ru.vlasov.vkcupapp.models.map.LocationData

sealed class FindLocationViewState {

    class Success{
        class LocationsLoaded(val locations : List<LocationData>) : FindLocationViewState()
    }

    class Error{
        object NetworkError : FindLocationViewState()
    }
}