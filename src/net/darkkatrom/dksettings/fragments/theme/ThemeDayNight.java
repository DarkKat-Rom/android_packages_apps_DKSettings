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

package net.darkkatrom.dksettings.fragments.theme;

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dksettings.MainActivity;
import net.darkkatrom.dksettings.R;

public class ThemeDayNight extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeDayNight";

    private static final String PREF_CAT_COLORS               = "theme_day_night_cat_colors";
    private static final String PREF_CAT_BARS                 = "theme_day_night_cat_bars";
    private static final String PREF_DAY_NIGHT_MODE           = "day_night_mode";
    private static final String PREF_NIGHT_THEME              = "night_theme";
    private static final String PREF_DAY_THEME                = "day_theme";
    private static final String PREF_CUSTOMIZE_COLORS         = "customize_colors";
    private static final String PREF_PRIMARY_COLOR            = "primary_color";
    private static final String PREF_ACCENT_COLOR             = "accent_color";
    private static final String PREF_COLORIZE_NAVIGATION_BAR  = "colorize_navigation_bar";
    private static final String PREF_USE_LIGHT_STATUS_BAR     = "use_light_status_bar";
    private static final String PREF_USE_LIGHT_NAVIGATION_BAR = "use_light_navigation_bar";

    private ContentResolver mResolver;

    private ListPreference mDayNightMode;
    private ListPreference mNightTheme;
    private ListPreference mDayTheme;
    private SwitchPreference mCustomizeColors;
    private ColorPickerPreference mPrimaryColor;
    private ListPreference mAccentColor;
    private SwitchPreference mColorizeNavigationBar;
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

        addPreferencesFromResource(R.xml.theme_day_night_settings);

        mResolver = getContentResolver();

        mDayNightMode = (ListPreference) findPreference(PREF_DAY_NIGHT_MODE);
        mDayNightMode.setValue(String.valueOf(ThemeHelper.getNightMode(getActivity())));
        mDayNightMode.setOnPreferenceChangeListener(this);

        mNightTheme = (ListPreference) findPreference(PREF_NIGHT_THEME);
        final int nightTheme = Settings.Secure.getInt(mResolver,
                Settings.Secure.UI_NIGHT_THEME, UiModeManager.MODE_NIGHT_YES);
        mNightTheme.setValue(String.valueOf(nightTheme));
        mNightTheme.setOnPreferenceChangeListener(this);

        mDayTheme = (ListPreference) findPreference(PREF_DAY_THEME);
        final int dayTheme = Settings.Secure.getInt(mResolver,
                Settings.Secure.UI_DAY_THEME, UiModeManager.MODE_NIGHT_NO);
        mDayTheme.setValue(String.valueOf(dayTheme));
        mDayTheme.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        PreferenceCategory catBars =
                (PreferenceCategory) findPreference(PREF_CAT_BARS);

        if (ThemeHelper.isBlackoutTheme(getActivity())
                || ThemeHelper.isWhiteoutTheme(getActivity())) {
            catColors.removePreference(findPreference(PREF_CUSTOMIZE_COLORS));
        } else {
            mCustomizeColors =
                    (SwitchPreference) findPreference(PREF_CUSTOMIZE_COLORS);
            mCustomizeColors.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.CUSTOMIZE_THEME_COLORS, 0) == 1);
            mCustomizeColors.setOnPreferenceChangeListener(this);
        }

        if (ThemeHelper.isBlackoutTheme(getActivity())
                || ThemeHelper.isWhiteoutTheme(getActivity())
                || !ThemeColorHelper.customizeColors(getActivity())) {
            catColors.removePreference(findPreference(PREF_PRIMARY_COLOR));
            catColors.removePreference(findPreference(PREF_ACCENT_COLOR));
        } else {
            int defaultPrimaryColor =
                    getActivity().getColor(com.android.internal.R.color.primary_color_darkkat);
            mPrimaryColor = (ColorPickerPreference) findPreference(PREF_PRIMARY_COLOR);
            int intColor = ThemeColorHelper.getPrimaryColor(getActivity(), defaultPrimaryColor);
            mPrimaryColor.setNewColor(intColor);
            mPrimaryColor.setOnPreferenceChangeListener(this);

            mAccentColor = (ListPreference) findPreference(PREF_ACCENT_COLOR);
            final int accentColor = ThemeColorHelper.getIndexForAccentColor(getActivity());
            mAccentColor.setValue(String.valueOf(accentColor));
            mAccentColor.setOnPreferenceChangeListener(this);
        }

        if (ThemeHelper.isNightMode(getActivity())
                || ThemeColorHelper.customizeColors(getActivity())
                || ThemeHelper.isWhiteoutTheme(getActivity())) {
            catBars.removePreference(findPreference(PREF_USE_LIGHT_STATUS_BAR));
        } else {
            mUseLightStatusBar =
                    (SwitchPreference) findPreference(PREF_USE_LIGHT_STATUS_BAR);
            mUseLightStatusBar.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.USE_LIGHT_STATUS_BAR, 0) == 1);
            mUseLightStatusBar.setOnPreferenceChangeListener(this);
        }

        if (ThemeHelper.isNightMode(getActivity())
                || ThemeColorHelper.colorizeNavigationBar(getActivity())
                || ThemeHelper.isWhiteoutTheme(getActivity())) {
            catBars.removePreference(findPreference(PREF_USE_LIGHT_NAVIGATION_BAR));
        } else {
            mUseLightNavigationBar =
                    (SwitchPreference) findPreference(PREF_USE_LIGHT_NAVIGATION_BAR);
            mUseLightNavigationBar.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.USE_LIGHT_NAVIGATION_BAR, 0) == 1);
            mUseLightNavigationBar.setOnPreferenceChangeListener(this);
        }

        if (ThemeHelper.isBlackoutTheme(getActivity())
                || ThemeHelper.isWhiteoutTheme(getActivity())) {
            catBars.removePreference(findPreference(PREF_COLORIZE_NAVIGATION_BAR));
            removePreference(PREF_CAT_COLORS);
            removePreference(PREF_CAT_BARS);
        } else {
            mColorizeNavigationBar =
                    (SwitchPreference) findPreference(PREF_COLORIZE_NAVIGATION_BAR);
            mColorizeNavigationBar.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.COLORIZE_NAVIGATION_BAR, 0) == 1);
            mColorizeNavigationBar.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_theme_day_night;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        boolean value;

        if (preference == mDayNightMode) {
            try {
                intValue = Integer.parseInt((String) newValue);
                UiModeManager uiModeManager =
                        (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
                uiModeManager.setNightMode(intValue);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
            return true;
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
        } else if (preference == mCustomizeColors) {
            value = (Boolean) newValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.CUSTOMIZE_THEME_COLORS, value ? 1 : 0);
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        } else if (preference == mPrimaryColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.THEME_PRIMARY_COLOR, intHex);
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        } else if (preference == mAccentColor) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.THEME_ACCENT_COLOR, intValue);
            ((MainActivity) getActivity()).recreateForThemeChange();
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
        } else if (preference == mColorizeNavigationBar) {
            value = (Boolean) newValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.COLORIZE_NAVIGATION_BAR, value ? 1 : 0);
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        }
        return false;
    }
}
