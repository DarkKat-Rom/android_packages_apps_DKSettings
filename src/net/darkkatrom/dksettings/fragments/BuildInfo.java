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

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemProperties;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class BuildInfo extends SettingsBaseFragment {
    private static final String PROP_DK_VERSION = "ro.dk.version";

    private static final String PREF_DARKKAT   = "darkkat";
    private static final String PREF_ANDROID   = "android";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.build_info);

        final String summaryDarkKat = getResources().getString(
                R.string.darkkat_summary, getPropValue(PROP_DK_VERSION));
        final String summaryAndroid = isWifiOnly(getActivity())
                ? getResources().getString(R.string.android_wifi_only_summary)
                : getResources().getString(R.string.android_summary);

        setStringSummary(PREF_DARKKAT, summaryDarkKat);
        setStringSummary(PREF_ANDROID, summaryAndroid);
    }

    private boolean isWifiOnly(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return (cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE) == false);
    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.build_info_default));
        }
    }

    private static String getPropValue(String property) {
        return SystemProperties.get(property);
    }
}
