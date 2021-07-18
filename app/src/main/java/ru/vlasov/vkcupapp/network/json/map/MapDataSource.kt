package ru.vlasov.vkcupapp.network.json.map

import ru.vlasov.vkcupapp.di.network.MapAPI
import ru.vlasov.vkcupapp.models.map.Address
import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.network.json.Coordinates
import ru.vlasov.vkcupapp.network.json.map.response.LocationDataResponse
import javax.inject.Inject

class MapDataSource @Inject constructor(@MapAPI val api : JsonMapApiPlaceholder) : RemoteMapDataSource {
    override suspend fun getLocationByCoordinates(coordinates: Coordinates): LocationData =
            api.getLocationByCoordinates(coordinates.lat, coordinates.lon).let{ locationDataResponse ->
                LocationData(
                        lat = locationDataResponse.lat,
                        lon = locationDataResponse.lon,
                        category = locationDataResponse.category,
                        type = locationDataResponse.type,
                        addresstype = locationDataResponse.addresstype ,
                        name = locationDataResponse.name,
                        displayName = locationDataResponse.displayName,
                        address = locationDataResponse.address.let{ addressResponse ->
                            Address(
                                    houseNumber = addressResponse.houseNumber,
                                    road = addressResponse.road,
                                    cityDistrict = addressResponse.cityDistrict,
                                    city = addressResponse.city,
                                    country = addressResponse.country,
                                    state = addressResponse.state,
                                    county = addressResponse.county,
                                    region = addressResponse.region,
                                    postcode = addressResponse.postcode,
                                    countryCode = addressResponse.countryCode
                                    )
                        },
                        boundingbox = locationDataResponse.boundingbox
                )
            }

        override suspend fun findLocationsByQuery(query: String): List<LocationData> =
                api.getLocationsByQuery(query = query).map{ locationDataResponse ->
                        LocationData(
                                lat = locationDataResponse.lat,
                                lon = locationDataResponse.lon,
                                category = locationDataResponse.category,
                                type = locationDataResponse.type,
                                addresstype = locationDataResponse.addresstype ,
                                name = locationDataResponse.name,
                                displayName = locationDataResponse.displayName,
                                address = locationDataResponse.address.let{ addressResponse ->
                                        Address(
                                                houseNumber = addressResponse.houseNumber,
                                                road = addressResponse.road,
                                                cityDistrict = addressResponse.cityDistrict,
                                                city = addressResponse.city,
                                                country = addressResponse.country,
                                                state = addressResponse.state,
                                                county = addressResponse.county,
                                                region = addressResponse.region,
                                                postcode = addressResponse.postcode,
                                                countryCode = addressResponse.countryCode
                                        )
                                },
                                boundingbox = locationDataResponse.boundingbox
                        )
                }

}