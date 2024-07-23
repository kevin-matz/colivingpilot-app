package de.fhe.ai.colivingpilot.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.network.NetworkResultNoData
import de.fhe.ai.colivingpilot.network.data.request.CreateWgRequest
import de.fhe.ai.colivingpilot.util.UiUtils

class CreateWgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wg)

        val createBtn = findViewById<Button>(R.id.create_wg_activity_button_create_wg)
        createBtn.setOnClickListener {
            UiUtils.hideKeyboard(this)

            val nameField = findViewById<TextInputLayout>(R.id.create_wg_activity_textfield_wg_name)
            val maxMembersField = findViewById<TextInputLayout>(R.id.create_wg_activity_textfield_member_count)

            val progressBar = findViewById<ProgressBar>(R.id.create_wg_activity_progress)

            fun setFormLocked(locked: Boolean) {
                createBtn.visibility = if (locked) View.GONE else View.VISIBLE
                progressBar.visibility = if (locked) View.VISIBLE else View.GONE
                nameField.isEnabled = !locked
                maxMembersField.isEnabled = !locked
            }

            setFormLocked(true)

            val name = nameField.editText?.text.toString()
            val memberCount = maxMembersField.editText?.text.toString().toInt()
            val request = CreateWgRequest(name, memberCount)
            // TODO: response invitation code is unused, remove it?
            CoLiPiApplication.instance.repository.createWg(request, object : NetworkResultNoData {
                override fun onSuccess() {
                    val intent = Intent(this@CreateWgActivity, JoinedWgActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("mode", "created")
                    startActivity(intent)
                }

                override fun onFailure(code: String?) {
                    // TODO: Translate status field
                    UiUtils.showSnackbar(this@CreateWgActivity, createBtn, R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT, R.color.red)
                    setFormLocked(false)
                }
            })
        }
    }
}