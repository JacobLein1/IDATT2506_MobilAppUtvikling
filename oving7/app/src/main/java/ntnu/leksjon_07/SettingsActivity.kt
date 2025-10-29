package ntnu.leksjon_07

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ntnu.leksjon_07.databinding.ActivitySettingsBinding
import ntnu.leksjon_07.managers.MyPreferenceManager

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
                         Preference.SummaryProvider<ListPreference> {

	private lateinit var ui: ActivitySettingsBinding
	private lateinit var myPreferenceManager: MyPreferenceManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		myPreferenceManager = MyPreferenceManager(this)
		myPreferenceManager.registerListener(this)

		ui = ActivitySettingsBinding.inflate(layoutInflater)
		setContentView(ui.root)

		supportFragmentManager
				.beginTransaction()
				.replace(R.id.settings_container, SettingsFragment())
				.commit()

		ui.button.setOnClickListener {
			finish()
		}
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
		if (key == getString(R.string.night_mode)) myPreferenceManager.updateNightMode()
	}

	override fun provideSummary(preference: ListPreference?): CharSequence {
		return when (preference?.key) {
			getString(R.string.night_mode) -> preference.entry
			else                           -> "Unknown Preference"
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		myPreferenceManager.unregisterListener(this)
	}

	class SettingsFragment : PreferenceFragmentCompat() {

		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.preference_screen, rootKey)
		}
	}
}
