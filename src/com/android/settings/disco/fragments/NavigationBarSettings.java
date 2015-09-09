/*
 * Copyright (C) 2015 ProjectDisco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.disco.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.DropDownPreference;
import com.android.settings.notification.DropDownPreference.Callback;

import static android.provider.Settings.System.SYSTEM_DESIGN_FLAGS;
import static android.view.View.SYSTEM_DESIGN_FLAG_IMMERSIVE_NAV;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String KEY_NAVIGATION_BAR_STYLE = "navigation_bar_style";

    private ListPreference mNavigationBarHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.projectdisco_extras_navigation_bar);

        mNavigationBarHeight = (ListPreference) findPreference(KEY_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);
        int statusNavigationBarHeight = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mNavigationBarHeight.setValue(String.valueOf(statusNavigationBarHeight));
        mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntry());

        final DropDownPreference.Callback styleCallback = new DropDownPreference.Callback() {

            @Override
            public synchronized boolean onItemSelected(final int position, final Object value) {
                int flags = Settings.System.getInt(getContentResolver(), SYSTEM_DESIGN_FLAGS, 0);

                switch (((Integer) value).intValue()) {
                case R.string.navigation_bar_style_summary_stock:
                    // Revert the navigation bar to Google's stock.
                    flags &= ~SYSTEM_DESIGN_FLAG_IMMERSIVE_NAV;
                    break;
                case R.string.navigation_bar_style_summary_immersive:
                    // Switch the navigation bar over to Immersive mode.
                    flags |= SYSTEM_DESIGN_FLAG_IMMERSIVE_NAV;
                    break;
                default:
                    // Report a bad state.
                    return false;
                }

                return Settings.System.putInt(getContentResolver(), SYSTEM_DESIGN_FLAGS, flags);
            }

        };

        final int systemDesignFlags = Settings.System.getInt(getContentResolver(),
                SYSTEM_DESIGN_FLAGS, 0);

        final DropDownPreference navigationBarStyle =
                (DropDownPreference) findPreference(KEY_NAVIGATION_BAR_STYLE);
        navigationBarStyle.addItem(R.string.navigation_bar_style_summary_stock,
                R.string.navigation_bar_style_summary_stock);
        navigationBarStyle.addItem(R.string.navigation_bar_style_summary_immersive,
                R.string.navigation_bar_style_summary_immersive);
        if ((systemDesignFlags & SYSTEM_DESIGN_FLAG_IMMERSIVE_NAV) != 0) {
            navigationBarStyle.setSelectedValue(R.string.navigation_bar_style_summary_immersive);
        } else {
            navigationBarStyle.setSelectedValue(R.string.navigation_bar_style_summary_stock);
        }
        navigationBarStyle.setCallback(styleCallback);

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavigationBarHeight) {
            int statusNavigationBarHeight = Integer.valueOf((String) objValue);
            int index = mNavigationBarHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, statusNavigationBarHeight);
            mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntries()[index]);
        }
        return true;
    }
}
