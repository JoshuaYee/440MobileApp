package com.flexedev.twobirds_onescone.fragments.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.flexedev.twobirds_onescone.helper.NotificationHelper
import com.flexedev.twobirds_onescone.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var sharedPreferences: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            loadData()
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val enableAlerts = preferenceScreen.findPreference<SwitchPreferenceCompat>("enableAlerts")
            val interval = preferenceScreen.findPreference<ListPreference>("alertIntervals")
            val setDefaultLocationButton = preferenceScreen.findPreference<Preference>("setDefaultLocation")

            setDefaultLocationButton!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Log.d("AAAAAAAAA", "BRRRRRRUH")
                updateDefaultLocation()
                true
            }

            enableAlerts!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {_, value ->
                updateEnableAlerts(value)
                true
            }
            interval!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {_, value ->
                updateIntervals(value)
                true
            }

        }

        private fun updateDefaultLocation() {
            SetDefaultLocationFragment().show(requireActivity().supportFragmentManager, null)
        }

        private fun updateEnableAlerts(value: Any) {
            NotificationHelper().updateNotificationsEnabled(value, requireContext(), sharedPreferences)
        }

        private fun updateIntervals(value: Any) {
            NotificationHelper().updateNotificationsInterval(value, requireContext(), sharedPreferences)
        }

        private fun loadData(): String? {
            sharedPreferences =
                requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getString("STRING_KEY", null)
        }

    }
}
