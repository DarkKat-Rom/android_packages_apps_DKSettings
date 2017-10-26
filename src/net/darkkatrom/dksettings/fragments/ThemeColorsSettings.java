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

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import net.darkkatrom.dksettings.R;

public class ThemeColorsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeColorsSettings";

    private static final String PREF_DAY_NIGHT_MODE = "day_night_mode";
    private static final String PREF_NIGHT_THEME = "night_theme";
    private static final String PREF_DAY_THEME = "day_theme";

    private UiModeManager mUiModeManager;
    private ContentResolver mResolver;

    private ListPreference mDayNightMode;
    private ListPreference mNightTheme;
    private ListPreference mDayTheme;

    private int mCurrentDayNightMode;

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

        mDayNightMode = (ListPreference) findPreference(PREF_DAY_NIGHT_MODE);
        mCurrentDayNightMode = mUiModeManager.getNightMode();
        mDayNightMode.setValue(String.valueOf(mCurrentDayNightMode));
        mDayNightMode.setOnPreferenceChangeListener(this);

        boolean showNightTheme = false;
        boolean showDayTheme = false;
        switch (mCurrentDayNightMode) {
            case UiModeManager.MODE_NIGHT_YES:
                showNightTheme = true;
                break;
            case UiModeManager.MODE_NIGHT_NO:
                showDayTheme = true;
                break;
            default:
                showNightTheme = true;
                showDayTheme = true;
                break;
        }

        if (showNightTheme) {
            mNightTheme = (ListPreference) findPreference(PREF_NIGHT_THEME);
            final int nightTheme = Settings.Secure.getInt(mResolver,
                    Settings.Secure.UI_NIGHT_THEME, UiModeManager.MODE_NIGHT_YES);
            mNightTheme.setValue(String.valueOf(nightTheme));
            mNightTheme.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NIGHT_THEME);
        }

        if (showDayTheme) {
            mDayTheme = (ListPreference) findPreference(PREF_DAY_THEME);
            final int dayTheme = Settings.Secure.getInt(mResolver,
                    Settings.Secure.UI_DAY_THEME, UiModeManager.MODE_NIGHT_NO);
            mDayTheme.setValue(String.valueOf(dayTheme));
            mDayTheme.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_DAY_THEME);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;

        if (preference == mDayNightMode) {
            try {
                intValue = Integer.parseInt((String) newValue);
                mUiModeManager.setNightMode(intValue);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
            mCurrentDayNightMode = mUiModeManager.getNightMode();
            refreshSettings();
        } else if (preference == mNightTheme) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_NIGHT_THEME, intValue);
            return true;
        } else if (preference == mDayTheme) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_DAY_THEME, intValue);
            return true;
        }
        return false;
    }
}
