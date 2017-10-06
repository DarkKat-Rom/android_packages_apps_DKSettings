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

import android.os.Bundle;
import android.preference.Preference;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class StatusBarSettings extends SettingsBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        final boolean isWeatherAvailable =
                WeatherHelper.isWeatherAvailable(getActivity());
        final int weatherAvailability =
                WeatherHelper.getWeatherAvailability(getActivity());

        Preference weather = findPreference("status_bar_weather_settings");

        if (weatherAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.dk_weather_disabled_summary
                    : R.string.dk_weather_disabled_tablet_summary);
            weather.setSummary(summary);
        } else if (weatherAvailability == WeatherHelper.PACKAGE_MISSING) {
            weather.setSummary(getResources().getString(R.string.dk_weather_missing_summary));
        }
        weather.setEnabled(isWeatherAvailable);
    }
}
