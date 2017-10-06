/*
 * Copyright (C) 2016 DarkKat
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

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class ThemeColorsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeColorsSettings";

    private static final String PREF_THEME = "theme";
    private static final String PREF_AUTO_NIGHT_MODE = "auto_night_mode";

    private UiModeManager mUiModeManager;
    private ContentResolver mResolver;

    private ListPreference mTheme;
    private ListPreference mAutoNightMode;

    private int mCurrentTheme;

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

        addPreferencesFromResource(R.xml.theme_colors_settings);

        mUiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        mResolver = getContentResolver();

        mTheme = (ListPreference) findPreference(PREF_THEME);
        mCurrentTheme = mUiModeManager.getNightMode();
        mTheme.setValue(String.valueOf(mCurrentTheme));
        mTheme.setOnPreferenceChangeListener(this);

        if (mCurrentTheme == mUiModeManager.MODE_NIGHT_AUTO) {
            mAutoNightMode = (ListPreference) findPreference(PREF_AUTO_NIGHT_MODE);
            final int autoNightMode = Settings.Secure.getInt(mResolver,
                    Settings.Secure.UI_NIGHT_AUTO_MODE, UiModeManager.MODE_NIGHT_YES_DARKKAT);
            mAutoNightMode.setValue(String.valueOf(autoNightMode));
            mAutoNightMode.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_AUTO_NIGHT_MODE);
        }

        final boolean isWeatherAvailable =
                WeatherHelper.isWeatherAvailable(getActivity());
        final int weatherAvailability = WeatherHelper.getWeatherAvailability(getActivity());

        Preference detailedWeather =
                findPreference("colors_detailed_weather_view");

        if (weatherAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.dk_weather_disabled_summary
                    : R.string.dk_weather_disabled_tablet_summary);
            detailedWeather.setSummary(summary);
        } else if (weatherAvailability == WeatherHelper.PACKAGE_MISSING) {
            detailedWeather.setSummary(
                    getResources().getString(R.string.dk_weather_missing_summary));
        }
        detailedWeather.setEnabled(isWeatherAvailable);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;

        if (preference == mTheme) {
            try {
                intValue = Integer.parseInt((String) newValue);
                mUiModeManager.setNightMode(intValue);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
            mCurrentTheme = mUiModeManager.getNightMode();
            Settings.Secure.putInt(mResolver, Settings.Secure.UI_NIGHT_MODE, mCurrentTheme);
            refreshSettings();
        } else if (preference == mAutoNightMode) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_NIGHT_AUTO_MODE, intValue);
            return true;
        }
        return false;
    }
}
