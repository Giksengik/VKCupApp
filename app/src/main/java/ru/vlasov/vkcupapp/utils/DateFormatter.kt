package ru.vlasov.vkcupapp.utils

import android.content.Context
import android.icu.number.IntegerWidth
import androidx.core.os.ConfigurationCompat
import ru.vlasov.vkcupapp.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private  var simpleDateFormat : SimpleDateFormat? = null

    private fun defineFormat(context : Context){
        simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", ConfigurationCompat.getLocales(context.resources.configuration).get(0))
    }

    fun getDateString(time: Long, context: Context) : String {
        if (simpleDateFormat == null)
            defineFormat(context)
        val currentDate = simpleDateFormat?.format(System.currentTimeMillis())
        val postDate = simpleDateFormat?.format(time * 1000L)
        return if(currentDate != null && postDate != null){
            val currentDateArray = currentDate.toString().split(" ")
            val postDateArray = postDate.toString().split(" ")
            if(currentDateArray[2] == postDateArray[2]){
                if(currentDateArray[1] == postDateArray[1] && currentDateArray[0] == postDateArray[0]){
                    getDiff(currentDateArray[3], postDateArray[3], context)
                } else "${postDateArray[0]} ${postDateArray[1]} ${postDateArray[3]}"
            }else "${postDateArray[0]} ${postDateArray[1]}  ${postDateArray[2].substring(0,4)}"
        } else ""
    }

    private fun getDiff(currentTime : String, postTime : String, context: Context) : String {
        val diffInMinutes = (Integer.parseInt(currentTime.substring(0,2) ) - Integer.parseInt(postTime.substring(0,2))) * 60 +
                Integer.parseInt(currentTime.substring(3,5) ) - Integer.parseInt(postTime.substring(3,5))
        if(diffInMinutes > 60) {
            val hours = (diffInMinutes / 60)
            return if(hours == 1 || hours == 21) "$hours ${context.getString(R.string.hour)} ${context.getString(R.string.ago)}"
            else "$hours ${context.getString(R.string.hours)} ${context.getString(R.string.ago)}"
        }
        return if(diffInMinutes % 10 == 1 && diffInMinutes != 11)
            "$diffInMinutes ${context.getString(R.string.minute)}  ${context.getString(R.string.ago)}"
        else "$diffInMinutes ${context.getString(R.string.minutes)}  ${context.getString(R.string.ago)}"
    }

}