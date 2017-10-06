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

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;
import com.android.internal.util.darkkat.WeatherServiceControllerImpl;

import net.darkkatrom.dksettings.MainActivity;
import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class WeatherSettings extends SettingsBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.weather_settings);

        final boolean isWeatherAvailable =
                WeatherHelper.isWeatherAvailable(getActivity());
        final int weatherAvailability = WeatherHelper.getWeatherAvailability(getActivity());

        Preference weatherConfig =
                findPreference("weather_config");

        if (weatherAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.dk_weather_disabled_summary
                    : R.string.dk_weather_disabled_tablet_summary);
            weatherConfig.setSummary(summary);
        } else if (weatherAvailability == WeatherHelper.PACKAGE_MISSING) {
            weatherConfig.setSummary(
                    getResources().getString(R.string.dk_weather_missing_summary));
        }
        weatherConfig.setEnabled(isWeatherAvailable);

        if (getActivity() instanceof MainActivity) {
            weatherConfig.setIntent(WeatherHelper.getWeatherAppSettingsIntent());
        }

        if (!isWeatherAvailable) {
            removePreference("weather_detailed_weather_view_settings");
        } else {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, Menu.FIRST, 0, R.string.action_show_detailed_weather_view_title)
                .setIcon(R.drawable.ic_action_show_detailed_weather_view)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(WeatherServiceControllerImpl.COMPONENT_DK_WEATHER);
                startActivity(intent);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }
}
