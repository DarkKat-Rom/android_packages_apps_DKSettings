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

package net.darkkatrom.dksettings.fragments.deviceinfo;

import android.os.Build;
import android.os.Bundle;

import net.darkkatrom.dksettings.R;

public class AboutDevice extends DeviceInfoBaseFragment {

    private static final String PREF_DEVICE_MODEL =
            "device_model";
    private static final String PREF_DEVICE_CPU =
            "device_cpu";
    private static final String PREF_DEVICE_MEMORY =
            "device_memory";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getActionBar() != null) {
            final String subtitle = getResources().getString(
                    R.string.action_bar_subtitle_about_device, Build.MODEL + getMsvSuffix());
            getActivity().getActionBar().setSubtitle(subtitle);
        }

        addPreferencesFromResource(R.xml.about_device);

        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL);
        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL + getMsvSuffix());

        final String cpuInfo = getCPUInfo();
        final String memInfo = getMemInfo();

        if (cpuInfo != null) {
            setStringSummary(PREF_DEVICE_CPU, cpuInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_CPU));
        }

        if (memInfo != null) {
            setStringSummary(PREF_DEVICE_MEMORY, memInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_MEMORY));
        }
    }
}
