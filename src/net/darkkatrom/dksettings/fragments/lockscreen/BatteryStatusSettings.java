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

package net.darkkatrom.dksettings.fragments.lockscreen;

import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.internal.util.darkkat.LockscreenHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class BatteryStatusSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_AMBIENT_DISPLAY =
            "lock_screen_category_ambient_display";
    private static final String PREF_BATTERY_STATUS_TYPE =
            "lock_screen_battery_status_type";
    private static final String PREF_SHOW_BATTERY_TEMP =
            "lock_screen_show_battery_temp";
    private static final String PREF_SHOW_ADVANCED_BATTERY_CHARGING_INFO =
            "lock_screen_show_advanced_battery_charging_info";
    private static final String PREF_AMBIENT_DISPLAY_SHOW_BATTERY_STATUS =
            "ambient_display_show_battery_status";

    private ListPreference mBatteryStatusType;
    private SwitchPreference mShowBatteryTemp;
    private SwitchPreference mShowAdvancedBatteryChargingInfo;
    private SwitchPreference mAmbientDisplayShowBatteryStatus;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.lock_screen_battery_status_settings);

        mResolver = getContentResolver();

        final boolean showAnyBatteryStatus = LockscreenHelper.showAnyBatteryStatus(getActivity());
        final boolean showBatteryChargingStatus = LockscreenHelper.showBatteryChargingStatus(getActivity());

        PreferenceCategory catAmbientDisplay =
                (PreferenceCategory) findPreference(PREF_CAT_AMBIENT_DISPLAY);

        mBatteryStatusType =
                (ListPreference) findPreference(PREF_BATTERY_STATUS_TYPE);
        mBatteryStatusType.setValue(String.valueOf(LockscreenHelper.getBatteryStatusType(getActivity())));
        mBatteryStatusType.setOnPreferenceChangeListener(this);

        if (showAnyBatteryStatus) {
            mShowBatteryTemp =
                    (SwitchPreference) findPreference(PREF_SHOW_BATTERY_TEMP);
            mShowBatteryTemp.setChecked(LockscreenHelper.showBatteryTemp(getActivity()));
            mShowBatteryTemp.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_BATTERY_TEMP);
        }

        if (showBatteryChargingStatus) {
            mShowAdvancedBatteryChargingInfo =
                    (SwitchPreference) findPreference(PREF_SHOW_ADVANCED_BATTERY_CHARGING_INFO);
            mShowAdvancedBatteryChargingInfo
                    .setChecked(LockscreenHelper.showAdvancedBatteryChargingInfo(getActivity()));
            mShowAdvancedBatteryChargingInfo.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_ADVANCED_BATTERY_CHARGING_INFO);
        }

        if (isDozeAvailable() && showAnyBatteryStatus) {
            mAmbientDisplayShowBatteryStatus =
                    (SwitchPreference) findPreference(PREF_AMBIENT_DISPLAY_SHOW_BATTERY_STATUS);
            mAmbientDisplayShowBatteryStatus
                    .setChecked(LockscreenHelper.showBatteryStatusOnAmbientDisplayEnabled(getActivity()));
            mAmbientDisplayShowBatteryStatus.setOnPreferenceChangeListener(this);
        } else {
            catAmbientDisplay.removePreference(findPreference(PREF_AMBIENT_DISPLAY_SHOW_BATTERY_STATUS));
            removePreference(PREF_CAT_AMBIENT_DISPLAY);
        }
    }

    private boolean isDozeAvailable() {
        String name = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(name)) {
            name = getActivity().getResources().getString(
                    com.android.internal.R.string.config_dozeComponent);
        }
        return !TextUtils.isEmpty(name);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mBatteryStatusType) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mBatteryStatusType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BATTERY_STATUS_TYPE, intValue);
            refreshSettings();
            return true;
        } else if (preference == mShowBatteryTemp) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_BATTERY_TEMP, value ? 1 : 0);
            return true;
        } else if (preference == mShowAdvancedBatteryChargingInfo) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_ADVANCED_BATTERY_CHARGING_INFO, value ? 1 : 0);
            return true;
        } else if (preference == mAmbientDisplayShowBatteryStatus) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY_STATUS, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
