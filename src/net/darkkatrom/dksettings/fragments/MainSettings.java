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

import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceCategory;

import com.android.internal.util.darkkat.AmbientDisplayHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.deviceinfo.DeviceInfoBaseFragment;

public class MainSettings extends SettingsBaseFragment {
    private static final String PROP_DK_VERSION = "ro.dk.version";

    private static final String PREF_CAT_EXPERIMENTAL              = "settings_category_experimental";
    private static final String PREF_EXPERIMENTAL_DISPLAY_SETTINGS = "experimental_display_settings";

    private static final String PREF_DARKKAT = "about_darkkat";
    private static final String PREF_ANDROID = "about_android";
    private static final String PREF_DEVICE  = "about_device";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_settings);

        if (AmbientDisplayHelper.deviceHasProximitySensor(getActivity())) {
            PreferenceCategory catExperimental =
                    (PreferenceCategory) findPreference(PREF_CAT_EXPERIMENTAL);
            catExperimental.removePreference(findPreference(PREF_EXPERIMENTAL_DISPLAY_SETTINGS));
            removePreference(PREF_CAT_EXPERIMENTAL);
        }

        final String summaryDarkKat = getResources().getString(
                R.string.about_darkkat_summary, getPropValue(PROP_DK_VERSION));
        final String summaryAndroid = getResources().getString(
                R.string.about_android_summary, Build.VERSION.RELEASE);
        final String summaryDevice = getResources().getString(
                R.string.about_device_summary, Build.MODEL + DeviceInfoBaseFragment.getMsvSuffix());

        setStringSummary(PREF_DARKKAT, summaryDarkKat);
        setStringSummary(PREF_ANDROID, summaryAndroid);
        setStringSummary(PREF_DEVICE, summaryDevice);
    }


    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    private static String getPropValue(String property) {
        return SystemProperties.get(property);
    }
}
