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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import net.darkkatrom.dksettings.R;

public class PowerButtonSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CONFIRM_POWER_OFF     = "power_menu_confirm_power_off";
    private static final String PREF_ADVANCED_RESTART_MODE = "power_menu_advanced_restart_mode";
    private static final String PREF_CONFIRM_RESTART       = "power_menu_confirm_restart";

    private SwitchPreference mConfirmPowerOff;
    private ListPreference mAdvancedRestartMode;
    private SwitchPreference mConfirmRestart;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_button_settings);

        mResolver = getContentResolver();

        mConfirmPowerOff =
                (SwitchPreference) findPreference(PREF_CONFIRM_POWER_OFF);
        mConfirmPowerOff.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_CONFIRM_POWER_OFF, 0) == 1));
        mConfirmPowerOff.setOnPreferenceChangeListener(this);

        mAdvancedRestartMode = (ListPreference) findPreference(PREF_ADVANCED_RESTART_MODE);
        final int advancedRestartMode = Settings.System.getInt(mResolver,
                    Settings.System.POWER_MENU_ADVANCED_RESTART_MODE, 2);
        mAdvancedRestartMode.setValue(String.valueOf(advancedRestartMode));
        mAdvancedRestartMode.setSummary(getAdvancedRestartModeSummary(advancedRestartMode));
        mAdvancedRestartMode.setOnPreferenceChangeListener(this);

        mConfirmRestart =
                (SwitchPreference) findPreference(PREF_CONFIRM_RESTART);
        mConfirmRestart.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_CONFIRM_RESTART, 0) == 1));
        mConfirmRestart.setOnPreferenceChangeListener(this);
    }

    private String getAdvancedRestartModeSummary(int mode) {
        String[] titles = getResources().getStringArray(
                R.array.power_menu_advanced_restart_mode_summary_titles);
        return titles[mode];
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_power_menu;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
       boolean value;

        if (preference == mConfirmPowerOff) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_CONFIRM_POWER_OFF, value ? 1 : 0);
            return true;
        } else if (preference == mAdvancedRestartMode) {
            int intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ADVANCED_RESTART_MODE, intValue);
            mAdvancedRestartMode.setSummary(getAdvancedRestartModeSummary(intValue));
            return true;
        } else if (preference == mConfirmRestart) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_CONFIRM_RESTART, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
