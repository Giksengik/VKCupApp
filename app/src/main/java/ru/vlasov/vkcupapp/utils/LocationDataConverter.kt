package ru.vlasov.vkcupapp.utils

import ru.vlasov.vkcupapp.models.map.LocationData
import java.lang.StringBuilder

object LocationDataConverter {

    fun convertLocationDataToString(locationData: LocationData) : String{
        val res = StringBuilder()
        res.append(locationData.address.road)
        res.append(" ")
        res.append(locationData.address.houseNumber)
        if(res.toString() == " ") {
            res.setLength(0)
            res.append(locationData.displayName)
        }
        return res.toString()
    }
}