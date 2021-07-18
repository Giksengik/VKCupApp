package ru.vlasov.vkcupapp.network.json.map.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.vlasov.vkcupapp.models.map.Address

@Serializable
class LocationDataResponse (
        @SerialName("place_id") val placeID: Long = 0,
        @SerialName("licence") val licence: String = "",
        @SerialName("osm_type") val osmType: String = "",
        @SerialName("osm_id")val osmID: Long = 0,
        @SerialName("lat") val lat: Double,
        @SerialName("lon") val lon: Double,
        @SerialName("place_rank") val placeRank: Long = 0,
        @SerialName("category") val category: String = "",
        @SerialName("type") val type: String = "",
        @SerialName("importance") val importance: Double = 0.0,
        @SerialName("addresstype") val addresstype: String = "",
        @SerialName("name") val name: String? = "",
        @SerialName("display_name") val displayName: String = "",
        @SerialName("address") val address: AddressResponse = AddressResponse(),
        @SerialName("boundingbox") val boundingbox: List<Double> = listOf()
        )