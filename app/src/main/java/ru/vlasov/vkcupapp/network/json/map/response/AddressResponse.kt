package ru.vlasov.vkcupapp.network.json.map.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AddressResponse (
        @SerialName("house_number") val houseNumber: String = "",
        @SerialName("road") val road: String = "",
        @SerialName("city_district") val cityDistrict: String = "",
        @SerialName("city") val city: String = "",
        @SerialName("county") val county: String = "",
        @SerialName("state") val state: String = "",
        @SerialName("region") val region: String = "",
        @SerialName("postcode") val postcode: String = "",
        @SerialName("country") val country: String = "",
        @SerialName("country_code") val countryCode: String = ""
        )