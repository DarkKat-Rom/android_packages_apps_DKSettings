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

package net.darkkatrom.dksettings.fragments.themecolors;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;

import com.android.internal.util.darkkat.SystemUIThemeOverlayHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class ThemeSystemUI extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_THEME_OVERLAY_MODE = "theme_systemui_overlay_mode";

    private ListPreference mThemeOverlayMode;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_systemui_settings);

        mResolver = getContentResolver();

        mThemeOverlayMode =
                (ListPreference) findPreference(PREF_THEME_OVERLAY_MODE);
        int themeOverlayMode = SystemUIThemeOverlayHelper.getThemeOverlayMode(getActivity());
        mThemeOverlayMode.setValue(String.valueOf(themeOverlayMode));
        mThemeOverlayMode.setSummary(mThemeOverlayMode.getEntry());
        mThemeOverlayMode.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_theme_system_ui;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mThemeOverlayMode) {
            int themeOverlayMode = Integer.valueOf((String) newValue);
            int index = mThemeOverlayMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SYSTEMUI_THEME_OVERLAY_MODE, themeOverlayMode);
            preference.setSummary(mThemeOverlayMode.getEntries()[index]);
            return true;
        }
        return false;
    }
}
