package ru.vlasov.vkcupapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.network.json.Coordinates
import javax.inject.Inject

class LastLocationDataHolder @Inject constructor(@ApplicationContext context: Context) {
    private var dataHolder : SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.last_location_sp), Context.MODE_PRIVATE)
    private var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager


    @SuppressLint("MissingPermission")
    fun getLastLocation() : Coordinates? {
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            saveLastLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!)
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            saveLastLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!)
        } else {
            if (dataHolder.getString(LAT_KEY, "") == "" || dataHolder.getString(LON_KEY, "") == "")
                null
            else
                Coordinates(dataHolder.getString(LAT_KEY, "")!!.toDouble(), dataHolder.getString(LON_KEY, "")!!.toDouble())
        }
    }

    private fun saveLastLocation(location : Location) : Coordinates {
        val coordinates = Coordinates(lat = location.latitude, lon = location.longitude)
        dataHolder.edit()
                .putString(LAT_KEY, coordinates.lat.toString())
                .putString(LON_KEY, coordinates.lon.toString())
                .apply()
        return coordinates
    }


    companion object{
        const val LAT_KEY = "USER_LAST_LAT_KEY"
        const val LON_KEY = "USER_LAST_LON_KEY"
    }
}