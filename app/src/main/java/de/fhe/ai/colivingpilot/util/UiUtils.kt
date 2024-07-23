package de.fhe.ai.colivingpilot.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar

class UiUtils {
    companion object {

        /**
         * Closes the keyboard if it is open
         *
         * @param activity The activity where this is called from
         */
        fun hideKeyboard(activity: AppCompatActivity) {
            try {
                val imm = activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            } catch (_: Exception) {
            }
        }

        /**
         * Shows a snackbar
         *
         * @param activity The activity where this is called from
         * @param view The parent view
         * @param stringId The string resource id of the text to be shown
         * @param duration The duration for which the snackbar should be visible
         * @param colorId (Optional) The color resource id of the snackbar's background color
         */
        fun showSnackbar(activity
                         : AppCompatActivity, view: View, stringId: Int, @Duration duration: Int, colorId: Int? = null) {
            if (colorId == null) {
                Snackbar.make(view, activity.getString(stringId), duration)
                    .show()
            } else {
                Snackbar.make(view, activity.getString(stringId), duration)
                    .setBackgroundTint(activity.getColor(colorId))
                    .show()
            }
        }

    }
}