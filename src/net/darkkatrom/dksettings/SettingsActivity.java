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

package net.darkkatrom.dksettings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dksettings.fragments.LockScreenSettings;
import net.darkkatrom.dksettings.fragments.MainSettings;
import net.darkkatrom.dksettings.fragments.StatusBarSettings;
import net.darkkatrom.dksettings.fragments.StatusBarExpandedSettings;
import net.darkkatrom.dksettings.fragments.ThemeColorsSettings;
import net.darkkatrom.dksettings.fragments.WeatherSettings;
import net.darkkatrom.dksettings.fragments.statusbar.ClockDateSettings;
import net.darkkatrom.dksettings.fragments.statusbar.NetworkTrafficSettings;
import net.darkkatrom.dksettings.fragments.statusbar.TickerSettings;
import net.darkkatrom.dksettings.fragments.statusbar.StatusBarWeatherSettings;
import net.darkkatrom.dksettings.fragments.statusbarexpanded.QSSettings;
import net.darkkatrom.dksettings.fragments.themecolors.ColorsDetailedWeatherView;
import net.darkkatrom.dksettings.fragments.themecolors.ColorsStatusBar;
import net.darkkatrom.dksettings.fragments.themecolors.ColorsStatusBarExpanded;
import net.darkkatrom.dksettings.fragments.weather.DetailedWeatherViewSettings;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String[] ENTRY_FRAGMENTS = {
        ColorPickerFragment.class.getName(),
        LockScreenSettings.class.getName(),
        MainSettings.class.getName(),
        StatusBarSettings.class.getName(),
        StatusBarExpandedSettings.class.getName(),
        ThemeColorsSettings.class.getName(),
        WeatherSettings.class.getName(),
        ClockDateSettings.class.getName(),
        NetworkTrafficSettings.class.getName(),
        TickerSettings.class.getName(),
        StatusBarWeatherSettings.class.getName(),
        QSSettings.class.getName(),
        ColorsDetailedWeatherView.class.getName(),
        ColorsStatusBar.class.getName(),
        ColorsStatusBarExpanded.class.getName(),
        DetailedWeatherViewSettings.class.getName(),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainSettings())
                    .commit();
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        if (pref instanceof ColorPickerPreference) {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), caller, ColorPickerPreference.RESULT_REQUEST_CODE);
        } else {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), null, 0);
        }
        return true;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        for (int i = 0; i < ENTRY_FRAGMENTS.length; i++) {
            if (ENTRY_FRAGMENTS[i].equals(fragmentName)) return true;
        }
        return super.isValidFragment(fragmentName);
    }
}
