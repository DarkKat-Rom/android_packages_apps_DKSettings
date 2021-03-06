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

import android.os.Bundle;
import android.os.SystemProperties;

import net.darkkatrom.dksettings.R;

public class AboutDarkKat extends DeviceInfoBaseFragment {
    private static final String PROP_DK_VERSION_SHORT =
            "ro.dk.version_short";
    private static final String PROP_DK_BUILD_VERSION =
            "ro.dk.build.version";
    private static final String PROP_DK_BUILD_TYPE =
            "ro.dk.build.type";
    private static final String PROP_DK_BUILD_DATE =
            "ro.build.date";

    private static final String PREF_DARKKAT_VERSION =
            "darkkat_version";
    private static final String PREF_DARKKAT_BUILD_VERSION =
            "darkkat_build_version";
    private static final String PREF_DARKKAT_BUILD_TYPE =
            "darkkat_build_type";
    private static final String PREF_DARKKAT_BUILD_DATE =
            "darkkat_build_date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_darkkat);

        setValueSummary(PREF_DARKKAT_VERSION, PROP_DK_VERSION_SHORT);
        setValueSummary(PREF_DARKKAT_BUILD_VERSION, PROP_DK_BUILD_VERSION);
        setValueSummary(PREF_DARKKAT_BUILD_TYPE, PROP_DK_BUILD_TYPE);
        setValueSummary(PREF_DARKKAT_BUILD_DATE, PROP_DK_BUILD_DATE);
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_about_darkkat;
    }
}
