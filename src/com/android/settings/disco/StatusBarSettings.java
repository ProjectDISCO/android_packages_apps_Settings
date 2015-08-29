/*
 *  Parts Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.android.settings.disco;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.DropDownPreference;
import com.android.settings.notification.DropDownPreference.Callback;
import com.android.settings.Utils;
import java.util.List;
import java.util.ArrayList;

import static android.provider.Settings.System.SYSTEM_DESIGN_FLAGS;
import static android.view.View.SYSTEM_DESIGN_FLAG_IMMERSIVE_STATUS;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "StatusBarSettings";

    private static final String KEY_STATUS_BAR_STYLE = "status_bar_style";
    private static final String STATUSBAR_BATTERY_STYLE = "statusbar_battery_style";
    private static final String STATUSBAR_BATTERY_PERCENT = "statusbar_battery_percent";

    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.projectdisco_tweaks_statusbar);

        final DropDownPreference.Callback styleCallback = new DropDownPreference.Callback() {

            @Override
            public synchronized boolean onItemSelected(final int position, final Object value) {
                int flags = Settings.System.getInt(getContentResolver(), SYSTEM_DESIGN_FLAGS, 0);

                switch (((Integer) value).intValue()) {
                case R.string.status_bar_style_summary_stock:
                    // Revert the status bar to Google's stock.
                    flags &= ~SYSTEM_DESIGN_FLAG_IMMERSIVE_STATUS;
                    break;
                case R.string.status_bar_style_summary_immersive:
                    // Switch the status bar over to Immersive mode.
                    flags |= SYSTEM_DESIGN_FLAG_IMMERSIVE_STATUS;
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

        final DropDownPreference statusBarStyle =
                (DropDownPreference) findPreference(KEY_STATUS_BAR_STYLE);
        statusBarStyle.addItem(R.string.status_bar_style_summary_stock,
                R.string.status_bar_style_summary_stock);
        statusBarStyle.addItem(R.string.status_bar_style_summary_immersive,
                R.string.status_bar_style_summary_immersive);
        if ((systemDesignFlags & SYSTEM_DESIGN_FLAG_IMMERSIVE_STATUS) != 0) {
            statusBarStyle.setSelectedValue(R.string.status_bar_style_summary_immersive);
        } else {
            statusBarStyle.setSelectedValue(R.string.status_bar_style_summary_stock);
        }
        statusBarStyle.setCallback(styleCallback);


        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mBatteryStyle = (ListPreference) findPreference(STATUSBAR_BATTERY_STYLE);
        int batteryStyle = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_STYLE, 0);

        mBatteryStyle.setValue(Integer.toString(batteryStyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference(STATUSBAR_BATTERY_PERCENT);
        int batteryPercent = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_PERCENT, 2);

        mBatteryPercent.setValue(Integer.toString(batteryPercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_STYLE, value);
        } else if (preference == mBatteryPercent) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_PERCENT, value);
        }

        return true;
    }
}
