package ru.vlasov.vkcupapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.AndroidEntryPoint
import ru.vlasov.vkcupapp.data.VkUserDataHolder
import ru.vlasov.vkcupapp.databinding.ActivityMainBinding
import javax.inject.Inject


@AndroidEntryPoint
class  MainActivity
    : AppCompatActivity() , AuthProvider {
    @Inject lateinit var vkUserDataHolder: VkUserDataHolder
    lateinit var binding : ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setUpNavigation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.navHostFragment.visibility = View.VISIBLE
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                vkUserDataHolder.parseUserToken(token)
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(baseContext, R.string.auth_error, Toast.LENGTH_LONG).show()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setUpNavigation(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setupWithNavController(
            navController = navController
        )
    }

    override fun auth() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
        binding.bottomNavigationView.visibility = View.GONE
        binding.navHostFragment.visibility = View.GONE
    }
}