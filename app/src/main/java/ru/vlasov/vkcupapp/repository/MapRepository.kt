package ru.vlasov.vkcupapp.repository

import ru.vlasov.vkcupapp.features.taxi.FindLocationViewState
import ru.vlasov.vkcupapp.features.taxi.MapViewState
import ru.vlasov.vkcupapp.network.json.Coordinates

interface MapRepository {
    suspend fun getLocationByCoordinates(coordinates: Coordinates) : MapViewState
    suspend fun getLastLocationCoordinates() : MapViewState
    suspend fun loadLocationsByQuery(query : String) : FindLocationViewState
}