package de.fhe.ai.colivingpilot.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import de.fhe.ai.colivingpilot.MainActivity
import de.fhe.ai.colivingpilot.R
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.network.RetrofitClient
import de.fhe.ai.colivingpilot.network.data.request.RegisterRequest
import de.fhe.ai.colivingpilot.network.data.response.BackendResponse
import de.fhe.ai.colivingpilot.network.data.response.datatypes.JwtData
import de.fhe.ai.colivingpilot.util.UiUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn = findViewById<Button>(R.id.register_activity_button_register)
        registerBtn.setOnClickListener {
            UiUtils.hideKeyboard(this)

            val usernameField = findViewById<TextInputLayout>(R.id.register_activity_textfield_username)
            val emailField = findViewById<TextInputLayout>(R.id.register_activity_textfield_email)
            val passwordField = findViewById<TextInputLayout>(R.id.register_activity_textfield_password)

            val passwordRepeatField = findViewById<TextInputLayout>(R.id.register_activity_textfield_password_again)
            val progressBar = findViewById<ProgressBar>(R.id.register_activity_progress)

            fun setFormLocked(locked: Boolean) {
                registerBtn.visibility = if (locked) View.GONE else View.VISIBLE
                progressBar.visibility = if (locked) View.VISIBLE else View.GONE
                usernameField.isEnabled = !locked
                emailField.isEnabled = !locked
                passwordField.isEnabled = !locked
                passwordRepeatField.isEnabled = !locked
            }

            setFormLocked(true)

            val username = usernameField.editText?.text.toString()
            val email = emailField.editText?.text.toString()
            val password = passwordField.editText?.text.toString()
            val registerRequest = RegisterRequest(username, email, password)
            RetrofitClient.instance.register(registerRequest).enqueue(object : Callback<BackendResponse<JwtData>> {
                override fun onResponse(
                    call: Call<BackendResponse<JwtData>>,
                    response: Response<BackendResponse<JwtData>>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            val token = it.data.token
                            val app = application as CoLiPiApplication
                            app.keyValueStore.writeString("jwt", token)
                            Log.i(CoLiPiApplication.LOG_TAG, "Received JWT: $token")
                        }

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    } else {
                        Log.e(CoLiPiApplication.LOG_TAG, "Register response unsuccessful: ${response.errorBody()?.string()}")
                        UiUtils.showSnackbar(this@RegisterActivity, registerBtn, R.string.snackbar_register_unsuccessful, Snackbar.LENGTH_SHORT, R.color.red)

                        setFormLocked(false)
                    }
                }

                override fun onFailure(call: Call<BackendResponse<JwtData>>, t: Throwable) {
                    Log.e(CoLiPiApplication.LOG_TAG, "Register request failed: ${t.message}")
                    UiUtils.showSnackbar(this@RegisterActivity, registerBtn, R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT, R.color.red)

                    setFormLocked(false)
                }
            })
        }
    }
}