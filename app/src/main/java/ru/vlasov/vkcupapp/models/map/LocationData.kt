package ru.vlasov.vkcupapp.models.map

import kotlinx.serialization.SerialName
import ru.vlasov.vkcupapp.network.json.map.response.AddressResponse

data class LocationData (
        val lat: Double,
        val lon: Double,
        val category: String,
        val type: String,
        val addresstype: String = "",
        val name: String? = "",
        val displayName: String,
        val address: Address,
        val boundingbox: List<Double>
        )