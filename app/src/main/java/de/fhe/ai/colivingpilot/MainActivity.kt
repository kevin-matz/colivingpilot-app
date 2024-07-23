package de.fhe.ai.colivingpilot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.view.NotInWgActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private fun tryRefreshAppDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = CoLiPiApplication.instance.repository.refresh()
            val refreshFailedWithStatus = !result.first && result.second.isNotEmpty()
            if (!refreshFailedWithStatus) {
                return@launch
            }

            val status = result.second
            if (status == "NOT_IN_WG") {
                val intent = Intent(this@MainActivity, NotInWgActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tryRefreshAppDatabase()

        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupWithNavController(navView, navController)
    }
}