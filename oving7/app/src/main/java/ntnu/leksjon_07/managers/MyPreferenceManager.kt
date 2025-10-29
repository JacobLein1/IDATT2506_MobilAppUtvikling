package ntnu.leksjon_07.managers

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import ntnu.leksjon_07.R

class MyPreferenceManager(private val activity: AppCompatActivity) {

	private val resources = activity.resources
	private val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
	private val editor: SharedPreferences.Editor = preferences.edit()

	fun example() {
		val preferences: SharedPreferences = activity.getSharedPreferences("prefs", 0)
		val editor: SharedPreferences.Editor = preferences.edit()
		editor.putString("playerName", "GameMaster3000")
		editor.putBoolean("darkMode", true)
		editor.apply()
	}

	fun putString(key: String, value: String) {
		editor.putString(key, value)
		editor.apply()
	}

	fun getString(key: String, defaultValue: String): String {
		return preferences.getString(key, defaultValue) ?: defaultValue
	}

	fun updateNightMode() {
		val darkModeValues = resources.getStringArray(R.array.night_mode_values)
		val value = getString(
				resources.getString(R.string.night_mode),
				resources.getString(R.string.night_mode_default_value)
		)
		when (value) {
			darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
			darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
		}
	}

	fun registerListener(activity: SharedPreferences.OnSharedPreferenceChangeListener) {
		preferences.registerOnSharedPreferenceChangeListener(activity)
	}

	fun unregisterListener(activity: SharedPreferences.OnSharedPreferenceChangeListener) {
		preferences.unregisterOnSharedPreferenceChangeListener(activity)
	}
}
