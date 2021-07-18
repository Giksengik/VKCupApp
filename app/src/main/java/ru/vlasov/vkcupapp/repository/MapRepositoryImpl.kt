package ru.vlasov.vkcupapp.repository

import ru.vlasov.vkcupapp.data.LastLocationDataHolder
import ru.vlasov.vkcupapp.features.taxi.FindLocationViewState
import ru.vlasov.vkcupapp.features.taxi.MapViewState
import ru.vlasov.vkcupapp.network.json.Coordinates
import ru.vlasov.vkcupapp.network.json.map.RemoteMapDataSource
import java.lang.Exception
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val remoteMapDataSource : RemoteMapDataSource,private val lastLocationDataHolder: LastLocationDataHolder) : MapRepository {

    companion object{
        private const val STANDART_LAT = 59.93587255
        private const val STANDART_LON = 30.325898855714637
    }


    override suspend fun getLocationByCoordinates(coordinates: Coordinates) : MapViewState {
        return try {
            val res = remoteMapDataSource.getLocationByCoordinates(coordinates)
            MapViewState.Success.OnLocationDataLoaded(res)
        }catch(e: Exception){
            e.printStackTrace()
            MapViewState.Error.NetworkError
        }
    }

    override suspend  fun getLastLocationCoordinates(): MapViewState =
            try {
                when (val coordinates = lastLocationDataHolder.getLastLocation()) {
                    null -> MapViewState.Error.UndefinedLastLocation(
                            remoteMapDataSource.getLocationByCoordinates(Coordinates(lat = STANDART_LAT, lon = STANDART_LON )))
                    else -> {

                            val res = remoteMapDataSource.getLocationByCoordinates(coordinates)
                            MapViewState.Success.OnLastLocationDefined(res)

                    }
                }
            }catch(e : Exception) {
                e.printStackTrace()
                MapViewState.Error.NetworkError
            }

    override suspend fun loadLocationsByQuery(query: String): FindLocationViewState =
        try{
            val res = remoteMapDataSource.findLocationsByQuery(query)
            FindLocationViewState.Success.LocationsLoaded(res)
        }
        catch (e : Exception){
            e.printStackTrace()
            FindLocationViewState.Error.NetworkError
        }

}