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
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.util.darkkat.ThemeOverlayHelper;

import net.darkkatrom.dksettings.MainActivity;
import net.darkkatrom.dksettings.R;

public class ThemeColorsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeColorsSettings";

    private static final String PREF_THEME_OVERLAY                  = "theme_overlay";
    private static final String PREF_THEME_OVERLAY_AUTO_DARK_THEME  = "theme_overlay_auto_dark_theme";
    private static final String PREF_THEME_OVERLAY_AUTO_LIGHT_THEME = "theme_overlay_auto_light_theme";
    private static final String PREF_USE_LIGHT_STATUS_BAR           = "use_light_status_bar";
    private static final String PREF_USE_LIGHT_NAVIGATION_BAR       = "use_light_navigation_bar";

    private ContentResolver mResolver;

    private ListPreference mThemeOverlay;
    private ListPreference mThemeOverlayAutoDarkTheme;
    private ListPreference mThemeOverlayAutoLightTheme;
    private SwitchPreference mUseLightStatusBar;
    private SwitchPreference mUseLightNavigationBar;

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

        mResolver = getContentResolver();

        mThemeOverlay = (ListPreference) findPreference(PREF_THEME_OVERLAY);
        mThemeOverlay.setValue(String.valueOf(ThemeOverlayHelper.getThemeOverlay(getActivity())));
        mThemeOverlay.setOnPreferenceChangeListener(this);

        mThemeOverlayAutoDarkTheme =
                (ListPreference) findPreference(PREF_THEME_OVERLAY_AUTO_DARK_THEME);
        mThemeOverlayAutoDarkTheme.setValue(String.valueOf(
                ThemeOverlayHelper.getThemeOverlayAutoDarkTheme(getActivity())));
        mThemeOverlayAutoDarkTheme.setOnPreferenceChangeListener(this);

        mThemeOverlayAutoLightTheme =
                (ListPreference) findPreference(PREF_THEME_OVERLAY_AUTO_LIGHT_THEME);
        mThemeOverlayAutoLightTheme.setValue(String.valueOf(
                ThemeOverlayHelper.getThemeOverlayAutoLightTheme(getActivity())));
        mThemeOverlayAutoLightTheme.setOnPreferenceChangeListener(this);

        if (ThemeOverlayHelper.themeSupportsOptionalĹightSB(getActivity())) {
            mUseLightStatusBar =
                    (SwitchPreference) findPreference(PREF_USE_LIGHT_STATUS_BAR);
            mUseLightStatusBar.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.USE_LIGHT_STATUS_BAR, 0) == 1);
            mUseLightStatusBar.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_USE_LIGHT_STATUS_BAR);
        }

        if (ThemeOverlayHelper.themeSupportsOptionalĹightNB(getActivity())) {
            mUseLightNavigationBar =
                    (SwitchPreference) findPreference(PREF_USE_LIGHT_NAVIGATION_BAR);
            mUseLightNavigationBar.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.USE_LIGHT_NAVIGATION_BAR, 0) == 1);
            mUseLightNavigationBar.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_USE_LIGHT_NAVIGATION_BAR);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        boolean value;

        if (preference == mThemeOverlay) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.THEME_OVERLAY, intValue);
            return true;
        } else if (preference == mThemeOverlayAutoDarkTheme) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.THEME_OVERLAY_AUTO_DARK_THEME, intValue);
            return true;
        } else if (preference == mThemeOverlayAutoLightTheme) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.THEME_OVERLAY_AUTO_LIGHT_THEME, intValue);
            return true;
        } else if (preference == mUseLightStatusBar) {
            value = (Boolean) newValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.USE_LIGHT_STATUS_BAR, value ? 1 : 0);
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        } else if (preference == mUseLightNavigationBar) {
            value = (Boolean) newValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.USE_LIGHT_NAVIGATION_BAR, value ? 1 : 0);
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        }
        return false;
    }
}
