package ru.vlasov.vkcupapp.utils

import android.content.Context
import org.osmdroid.bonuspack.routing.Road
import ru.vlasov.vkcupapp.R
import java.lang.StringBuilder
import kotlin.math.ceil

object TaxiDataHelper {
    private const val FAST_CAR_COEF = 1.1
    private const val NICE_CAR_COEF = 1.6
    private const val DRONE_CAR_COEF = 2.2
    private const val FAST_CAR_WAITING = 2
    private const val NICE_CAR_WAITING = 5
    private const val DRONE_CAR_WAITING = 15

    fun getFastCarTravelPrice(road : Road, context : Context) : String =
        getTravelPrice(FAST_CAR_COEF, road, context)

    fun getNiceCarTravelPrice(road : Road, context : Context) : String =
        getTravelPrice(NICE_CAR_COEF, road, context)

    fun getDroneCarTravelPrice(road : Road, context : Context) : String =
        getTravelPrice(DRONE_CAR_COEF, road, context)

    private fun getTravelPrice(coef : Double, road : Road, context : Context) : String{
        val res = StringBuilder()
        res.append(((ceil(road.mLength ) * coef * 50).toInt()).toString())
        res.append(context.getString(R.string.currency))
        return res.toString()
    }

    fun getFastCarWaiting(road : Road, context : Context) : String =
       getWaitingData(FAST_CAR_WAITING, road, context)


    fun getNiceCarWaiting(road : Road, context : Context) : String =
        getWaitingData(NICE_CAR_WAITING, road, context)

    fun getDroneCarWaiting(road : Road, context : Context) : String =
        getWaitingData(DRONE_CAR_WAITING, road, context)

    private fun getWaitingData(coef : Int, road : Road, context : Context) : String =
        "${ceil(road.mLength * 3).toInt()} ${context.getString(R.string.min_on_the_way)}  $coef ${context.getString(R.string.min_waiting)}"


}