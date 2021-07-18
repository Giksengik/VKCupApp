package ru.vlasov.vkcupapp.network.json.map

import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.network.json.Coordinates
import ru.vlasov.vkcupapp.network.json.map.response.LocationDataResponse

interface RemoteMapDataSource {
    suspend fun getLocationByCoordinates(coordinates: Coordinates) : LocationData
    suspend fun findLocationsByQuery(query : String) : List<LocationData>
}