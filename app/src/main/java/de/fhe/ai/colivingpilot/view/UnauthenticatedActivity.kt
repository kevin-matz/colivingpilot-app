package de.fhe.ai.colivingpilot.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.fhe.ai.colivingpilot.MainActivity
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnauthenticatedActivity : AppCompatActivity() {
    private fun checkIfUserIsAlreadyLoggedIn() {
        if (CoLiPiApplication.instance.keyValueStore.readString("jwt") != "") {
            CoroutineScope(Dispatchers.IO).launch {
                val result = CoLiPiApplication.instance.repository.refresh()
                val successful = result.first
                if (successful) {
                    val intent = Intent(this@UnauthenticatedActivity, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIfUserIsAlreadyLoggedIn()

        setContentView(R.layout.activity_unauthenticated)

        val loginBtn = findViewById<Button>(R.id.unauthenticated_activity_button_login)
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerBtn = findViewById<Button>(R.id.unauthenticated_activity_button_create_account)
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}