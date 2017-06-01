/*
 * Copyright (C) 2017 DarkKat
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

package net.darkkatrom.dksettings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class ExperimentalThemeColorsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_ENABLE_NOTIFICATIONS_THEME_COLORS =
            "experimental_enable_notification_theme_colors";

    private SwitchPreference mEnableNotificationThemeColors;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.experimental_theme_colors_settings);
        mResolver = getActivity().getContentResolver();

        mEnableNotificationThemeColors =
                (SwitchPreference) findPreference(PREF_ENABLE_NOTIFICATIONS_THEME_COLORS);
        mEnableNotificationThemeColors.setChecked(Settings.System.getInt(mResolver,
                Settings.System.EXPERIMENTAL_ENABLE_NOTIFICATIONS_THEME_COLORS, 0) == 1);
        mEnableNotificationThemeColors.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;

        if (preference == mEnableNotificationThemeColors) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPERIMENTAL_ENABLE_NOTIFICATIONS_THEME_COLORS,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
