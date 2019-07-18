package com.gmail.andre00nogueira.githubreposearch

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // This connects the layout w/ the preferences to this fragment
        addPreferencesFromResource(R.xml.preferences_layout)
    }

}