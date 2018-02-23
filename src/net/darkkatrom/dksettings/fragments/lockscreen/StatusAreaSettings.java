/*
 * Copyright (C) 2018 DarkKat
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

package net.darkkatrom.dksettings.fragments.lockscreen;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.LockscreenHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class StatusAreaSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_SHOW_WEATHER =
            "lock_screen_status_area_show_weather";
    private static final String PREF_SHOW_WEATHER_LOCATION =
            "lock_screen_status_area_show_weather_location";
    private static final String PREF_HIDE_WEATHER_ON_ALARM =
            "lock_screen_status_area_hide_weather_on_alarm";

    private SwitchPreference mShowWeather;
    private SwitchPreference mShowWeatherLocation;
    private SwitchPreference mHideWeatherOnAlarm;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.lock_screen_status_area_settings);

        mResolver = getContentResolver();

        final boolean showWeather = LockscreenHelper.showWeather(getActivity());

        mShowWeather = (SwitchPreference) findPreference(PREF_SHOW_WEATHER);
        mShowWeather.setChecked(showWeather);
        mShowWeather.setOnPreferenceChangeListener(this);

        if (showWeather) {
            mShowWeatherLocation = (SwitchPreference) findPreference(PREF_SHOW_WEATHER_LOCATION);
            mShowWeatherLocation.setChecked(LockscreenHelper.showWeatherLocation(getActivity()));
            mShowWeatherLocation.setOnPreferenceChangeListener(this);

            mHideWeatherOnAlarm = (SwitchPreference) findPreference(PREF_HIDE_WEATHER_ON_ALARM);
            mHideWeatherOnAlarm.setChecked(LockscreenHelper.hideWeatherOnAlarm(getActivity()));
            mHideWeatherOnAlarm.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_WEATHER_LOCATION);
            removePreference(PREF_HIDE_WEATHER_ON_ALARM);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowWeather) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_STATUS_AREA_SHOW_WEATHER, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowWeatherLocation) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_STATUS_AREA_SHOW_WEATHER_LOCATION, value ? 1 : 0);
            return true;
        } else if (preference == mHideWeatherOnAlarm) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_STATUS_AREA_HIDE_WEATHER_ON_ALARM, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
