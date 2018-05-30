/*
 * Copyright (C) 2008 The Android Open Source Project
 *
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

public class AboutAndroid extends DeviceInfoBaseFragment {
    private static final String PREF_ANDROID_VERSION =
            "android_version";
    private static final String PREF_BUILD_ID =
            "build_id";
    private static final String PREF_KERNEL_VERSION =
            "kernel_version";
    private static final String PREF_BOOTLOADER_VERSION =
            "bootloader_version";
    private static final String PREF_BASEBAND_VERSION =
            "baseband_version";
    private static final String PREF_BUILD_NUMBER =
            "build_number";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_android);

        setStringSummary(PREF_ANDROID_VERSION, Build.VERSION.RELEASE);
        setStringSummary(PREF_BUILD_ID, Build.ID);
        setStringSummary(PREF_KERNEL_VERSION, getFormattedKernelVersion());
        setStringSummary(PREF_BOOTLOADER_VERSION, Build.BOOTLOADER);

        // Remove Baseband version if wifi-only device
        if (isWifiOnly(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(PREF_BASEBAND_VERSION));
        } else {
            setStringSummary(PREF_BASEBAND_VERSION, Build.getRadioVersion());
        }

        setStringSummary(PREF_BUILD_NUMBER, Build.DISPLAY);
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_about_android;
    }
}
