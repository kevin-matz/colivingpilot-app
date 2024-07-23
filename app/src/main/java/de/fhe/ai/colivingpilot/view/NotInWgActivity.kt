package de.fhe.ai.colivingpilot.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.fhe.ai.colivingpilot.R

class NotInWgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_in_wg)

        val createBtn = findViewById<Button>(R.id.not_in_wg_activity_button_create_wg)
        createBtn.setOnClickListener {
            val intent = Intent(this, CreateWgActivity::class.java)
            startActivity(intent)
        }

        val joinBtn = findViewById<Button>(R.id.not_in_wg_activity_button_join_wg)
        joinBtn.setOnClickListener {
            val intent = Intent(this, JoinWgActivity::class.java)
            startActivity(intent)
        }
    }
}