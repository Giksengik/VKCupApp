package ru.vlasov.vkcupapp

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import dagger.hilt.android.HiltAndroidApp
import ru.vlasov.vkcupapp.data.VkUserDataHolder
import javax.inject.Inject

@HiltAndroidApp
class VkCupApplication : Application() {

    @Inject
    lateinit var vkUserDataHolder: VkUserDataHolder

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            vkUserDataHolder.clearUserData()
        }
    }
}