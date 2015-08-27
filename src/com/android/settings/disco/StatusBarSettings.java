/*
 * Copyright (C) 2015 ParanoidAndroid
 * Copyright (C) 2015 Rastapop
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

package com.android.settings.disco;

import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.DropDownPreference;
import com.android.settings.notification.DropDownPreference.Callback;

import static android.provider.Settings.System.SYSTEM_DESIGN_FLAGS;
import static android.view.View.SYSTEM_DESIGN_FLAG_IMMERSIVE_STATUS;

public class StatusBarSettings extends SettingsPreferenceFragment {

    private static final String KEY_STATUS_BAR_STYLE = "status_bar_style";

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

    }
}
