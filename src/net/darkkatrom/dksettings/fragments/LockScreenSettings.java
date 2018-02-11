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

package net.darkkatrom.dksettings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;


import net.darkkatrom.dksettings.R;

public class LockScreenSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_SHOW_VISUALIZER =
            "lock_screen_show_visualizer";

    private SwitchPreference mShowVizualizer;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen_settings);

        mResolver = getContentResolver();

        mShowVizualizer = (SwitchPreference) findPreference(PREF_SHOW_VISUALIZER);
        mShowVizualizer.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_VISUALIZER, 0) == 1);
        mShowVizualizer.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowVizualizer) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_VISUALIZER, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
