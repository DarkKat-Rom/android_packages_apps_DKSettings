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
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.LockscreenHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class WidgetsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_WEATHER =
            "lock_screen_widgets_cat_weather";
    private static final String PREF_SHOW_WIDGETS =
            "lock_screen_show_widgets";
    private static final String PREF_SHOW_WEATHER_HOUR_FORECAST =
            "lock_screen_widgets_show_weather_hour_forecast";
    private static final String PREF_SHOW_WEATHER_DAY_FORECAST =
            "lock_screen_widgets_show_weather_day_forecast";

    private SwitchPreference mShowWidgets;
    private SwitchPreference mShowWeatherHourForecast;
    private SwitchPreference mShowWeatherDayForecast;

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

        addPreferencesFromResource(R.xml.lock_screen_widgets_settings);

        mResolver = getContentResolver();

        final boolean showWidgets = LockscreenHelper.showWidgets(getActivity());

        mShowWidgets = (SwitchPreference) findPreference(PREF_SHOW_WIDGETS);
        mShowWidgets.setChecked(showWidgets);
        mShowWidgets.setOnPreferenceChangeListener(this);

        PreferenceCategory catWeather =
                (PreferenceCategory) findPreference(PREF_CAT_WEATHER);

        if (showWidgets) {
            mShowWeatherHourForecast = (SwitchPreference) findPreference(PREF_SHOW_WEATHER_HOUR_FORECAST);
            mShowWeatherHourForecast.setChecked(LockscreenHelper.showWeatherWidgetHourForecast(getActivity()));
            mShowWeatherHourForecast.setOnPreferenceChangeListener(this);

            mShowWeatherDayForecast = (SwitchPreference) findPreference(PREF_SHOW_WEATHER_DAY_FORECAST);
            mShowWeatherDayForecast.setChecked(LockscreenHelper.showWeatherWidgetDayForecast(getActivity()));
            mShowWeatherDayForecast.setOnPreferenceChangeListener(this);
       } else {
            catWeather.removePreference(findPreference(PREF_SHOW_WEATHER_HOUR_FORECAST));
            catWeather.removePreference(findPreference(PREF_SHOW_WEATHER_DAY_FORECAST));
            removePreference(PREF_CAT_WEATHER);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        if (preference == mShowWidgets) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_WIDGETS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowWeatherHourForecast) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_WIDGETS_SHOW_WEATHER_HOUR_FORECAST, value ? 1 : 0);
            return true;
        } else if (preference == mShowWeatherDayForecast) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_WIDGETS_SHOW_WEATHER_DAY_FORECAST, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
