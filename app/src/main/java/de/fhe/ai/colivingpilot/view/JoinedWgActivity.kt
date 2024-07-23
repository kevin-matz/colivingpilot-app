package de.fhe.ai.colivingpilot.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import de.fhe.ai.colivingpilot.MainActivity
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class JoinedWgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joined_wg)

        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        konfettiView.start(Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        ))

        val title = findViewById<TextView>(R.id.joined_wg_activity_textview_title)
        val mode = intent.getStringExtra("mode")
        val wgName = CoLiPiApplication.instance.keyValueStore.readString("wg_name")
        if (mode.equals("created")) {
            title.text = getString(R.string.created_wg_title, wgName)
        } else {
            title.text = getString(R.string.joined_wg_title, wgName)
        }

        val startBtn = findViewById<Button>(R.id.joined_wg_activity_button_start)
        startBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

}