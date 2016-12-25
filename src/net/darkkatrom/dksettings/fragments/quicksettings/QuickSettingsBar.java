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

package net.darkkatrom.dksettings.fragments.quicksettings;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class QuickSettingsBar extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_ENABLE_SCROLL =
            "qs_bar_enable_scroll";

    private SwitchPreference mEnableScroll;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.quick_settings_bar);

        mResolver = getContentResolver();

        mEnableScroll = (SwitchPreference) findPreference(PREF_ENABLE_SCROLL);
        mEnableScroll.setChecked(Settings.System.getInt(mResolver,
               Settings.System.QS_BAR_ENABLE_SCROLL, 0) == 1);
        mEnableScroll.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableScroll) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_BAR_ENABLE_SCROLL,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
