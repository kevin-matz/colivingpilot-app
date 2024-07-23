package de.fhe.ai.colivingpilot.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.util.UiUtils

class JoinWgActivity : AppCompatActivity() {

    private lateinit var scanQrBtn: Button
    private lateinit var joinBtn: Button
    private lateinit var codeField: TextInputLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_wg)

        scanQrBtn = findViewById<Button>(R.id.join_wg_activity_button_join_qr)

        scanQrBtn.setOnClickListener {
            // Initiieren des QR-Code-Scanners
            val integrator = IntentIntegrator(this)
            integrator.setOrientationLocked(false)
            integrator.setBeepEnabled(false)
            integrator.initiateScan()
        }

        joinBtn = findViewById<Button>(R.id.join_wg_activity_button_join)

        codeField = findViewById(R.id.join_wg_activity_textfield_code)

        progressBar = findViewById(R.id.join_wg_activity_progress)

        joinBtn.setOnClickListener {
            UiUtils.hideKeyboard(this)

            setFormLocked(true)
            
            val code = codeField.editText?.text.toString()
            tryJoinWg(code)
        }
    }

    private fun setFormLocked(locked: Boolean) {
        joinBtn.visibility = if (locked) View.GONE else View.VISIBLE
        progressBar.visibility = if (locked) View.VISIBLE else View.GONE
        scanQrBtn.isEnabled = !locked
        codeField.isEnabled = !locked
    }

    private fun tryJoinWg(code: String) {
        CoLiPiApplication.instance.repository.joinWg(code, object : NetworkResultNoData {
            override fun onSuccess() {
                val intent = Intent(this@JoinWgActivity, JoinedWgActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("mode", "joined")
                startActivity(intent)
            }

            override fun onFailure(code: String?) {
                UiUtils.showSnackbar(this@JoinWgActivity, joinBtn, R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT, R.color.red)
                setFormLocked(false)
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedCode = result.contents
                // Hier kannst du mit dem gescannten QR-Code-Inhalt arbeiten (z.B. anzeigen)
                // result.contents enthält den gescannten Text
                // Beispiel:
                codeField.editText?.setText(scannedCode)
                tryJoinWg(scannedCode)
                //Toast.makeText(this, "Gescannter QR-Code: $scannedCode", Toast.LENGTH_LONG).show()
            } else {
                // Wenn der Scan abgebrochen wurde

                Toast.makeText(this, "Scan abgebrochen", Toast.LENGTH_LONG).show()
            }
        }
    }
}