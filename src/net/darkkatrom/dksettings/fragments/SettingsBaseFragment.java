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

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsBaseFragment extends PreferenceFragment {

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getActionBar() != null && getSubtitleResId() > 0) {
            getActivity().getActionBar().setSubtitle(getSubtitleResId());
        }
    }

    protected int getSubtitleResId() {
        return 0;
    }

    protected void removeSubtitle() {
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setSubtitle(null);
        }
    }

    protected void removePreference(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }

    protected ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    protected Object getSystemService(final String name) {
        return getActivity().getSystemService(name);
    }

    protected PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }
}
