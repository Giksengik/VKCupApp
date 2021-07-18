package ru.vlasov.vkcupapp.network.json.map

import retrofit2.http.GET
import retrofit2.http.Query
import ru.vlasov.vkcupapp.network.json.map.response.LocationDataResponse

interface JsonMapApiPlaceholder {
    companion object{
        private const val FORMAT = "json"
        private const val ZOOM = 18

    }

    @GET("reverse")
    suspend fun getLocationByCoordinates(@Query("lat") lat : Double, @Query("lon") lon: Double,
    @Query("format") format : String = FORMAT, @Query("zoom") zoom : Int = ZOOM) : LocationDataResponse

    @GET("search")
    suspend fun getLocationsByQuery(@Query("q") query : String, @Query("format") format : String = FORMAT) : List<LocationDataResponse>
}