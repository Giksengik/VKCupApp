package ru.vlasov.vkcupapp.data

import android.content.Context
import android.content.SharedPreferences
import com.vk.api.sdk.auth.VKAccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.vlasov.vkcupapp.R
import javax.inject.Inject

class VkUserDataHolder @Inject constructor(@ApplicationContext context: Context) {
    private var dataHolder : SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.vk_user_data_key), Context.MODE_PRIVATE)

    fun parseUserToken(token: VKAccessToken){
        dataHolder.edit().apply{
            putString(TOKEN_KEY, token.accessToken)
            putInt(USER_ID_KEY, token.userId)
        }.apply()
    }

    fun clearUserData(){
        dataHolder.edit().apply{
            putString(TOKEN_KEY, "")
            putInt(USER_ID_KEY, -1)
        }.apply()
    }

    fun isAuth() : Boolean =
        !(dataHolder.getInt(USER_ID_KEY, -1) == -1 ||
                dataHolder.getString(TOKEN_KEY, "") == "")

    fun getToken() : String =
        dataHolder.getString(TOKEN_KEY, "") ?: ""

    companion object{
        const val TOKEN_KEY = "USER_DATA_TOKEN_KEY"
        const val USER_ID_KEY = "USER_DATA_ID_KEY"
    }
}