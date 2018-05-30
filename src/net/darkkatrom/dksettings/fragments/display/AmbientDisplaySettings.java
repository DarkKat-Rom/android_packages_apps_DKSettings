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

package net.darkkatrom.dksettings.fragments.experimental;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class AmbientDisplaySettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_PULSE_ON_NOTIFICATION =
            "ambient_display_pulse_on_notification";

    private ListPreference mPulseOnNotification;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_display_settings);

        mResolver = getContentResolver();


        mPulseOnNotification = (ListPreference) findPreference(PREF_PULSE_ON_NOTIFICATION);
        final int pulseOnNotification = Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_ON_NOTIFICATION, 2);
        mPulseOnNotification.setValue(String.valueOf(pulseOnNotification));
        mPulseOnNotification.setSummary(getPulseOnNotificationSummary(pulseOnNotification));
        mPulseOnNotification.setOnPreferenceChangeListener(this);
    }

    private void setPulseOnNotificationSetting(boolean enabled) {
        boolean settingEnabled = Settings.Secure.getInt(mResolver,
                Settings.Secure.DOZE_ENABLED, 1) == 1;
        if (settingEnabled != enabled) {
            Settings.Secure.putInt(mResolver, Settings.Secure.DOZE_ENABLED, enabled ? 1 : 0);
        }
    }

    private String getPulseOnNotificationSummary(int index) {
        String[] titles = getResources().getStringArray(
                R.array.ambient_display_pulse_on_notification_summary_titles);
        return titles[index];
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_ambient_display;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPulseOnNotification) {
            int intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_ON_NOTIFICATION, intValue);
            setPulseOnNotificationSetting(intValue != 1);
            mPulseOnNotification.setSummary(getPulseOnNotificationSummary(intValue));
            return true;
        }
        return false;
    }
}
