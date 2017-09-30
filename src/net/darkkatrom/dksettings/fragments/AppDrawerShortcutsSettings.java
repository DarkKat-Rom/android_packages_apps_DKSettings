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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class AppDrawerShortcutsSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_SHOW_DARKKAT_SHORTCUT =
            "app_drawer_show_darkkat_shortcut";

    private SwitchPreference mShowDarkKatShortcut;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.app_drawer_shortcuts_settings);

        mResolver = getContentResolver();

        mShowDarkKatShortcut = (SwitchPreference) findPreference(PREF_SHOW_DARKKAT_SHORTCUT);
        mShowDarkKatShortcut.setChecked(showDarkKatShortcut());
        mShowDarkKatShortcut.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowDarkKatShortcut) {
            boolean value = (Boolean) newValue;
            setShowDarkKatShortcut(value);
            return true;
        }
        return false;
    }

    private boolean showDarkKatShortcut() {
        PackageManager pm = getActivity().getPackageManager();
        ComponentName cn = new ComponentName(getActivity(), "net.darkkatrom.dksettings.LauncherActivity");
        int componentEnabledSetting = pm.getComponentEnabledSetting(cn);
        boolean isEnabled = componentEnabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        return isEnabled;
    }

    private void setShowDarkKatShortcut(boolean enabled) {
        if (showDarkKatShortcut() == enabled) {
            return;
        }
        PackageManager pm = getActivity().getPackageManager();
        ComponentName cn = new ComponentName(getActivity(), "net.darkkatrom.dksettings.LauncherActivity");
        int componentEnabledSetting = enabled
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(cn, componentEnabledSetting, PackageManager.DONT_KILL_APP);
    }
}
