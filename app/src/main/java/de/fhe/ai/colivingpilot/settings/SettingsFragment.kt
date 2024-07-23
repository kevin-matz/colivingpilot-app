package de.fhe.ai.colivingpilot.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import de.fhe.ai.colivingpilot.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themePreference = findPreference<ListPreference>("theme_preference")

        val currentValue = themePreference?.value ?: run {
            val defaultValue = "system" // Assuming "system" is the value for follow system theme
            themePreference?.value = defaultValue
            defaultValue
        }
        updateThemePreferenceIcon(themePreference, currentValue)

        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            updateThemePreferenceIcon(themePreference, newValue.toString())
            when (newValue) {
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }
    }


    /**
     * Updates the icon of the theme preference based on its current value.
     *
     * This method sets the appropriate icon for the theme preference to visually
     * indicate the current theme setting to the user.
     *
     * @param preference The [ListPreference] whose icon is to be updated.
     * @param value The current value of the preference, used to determine
     * which icon should be displayed.
     */
    private fun updateThemePreferenceIcon(preference: ListPreference?, value: String?) {
        preference?.icon = when (value) {
            "dark" -> {
                ContextCompat.getDrawable(requireContext(), R.drawable.baseline_dark_mode_24)
            }

            "light" -> ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_light_mode_24
            )

            "system" -> ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_auto_awesome_24
            )

            else -> null // You can set a default icon here if you like
        }
    }
}