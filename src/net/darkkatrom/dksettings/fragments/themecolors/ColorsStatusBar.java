/*
 * Copyright (C) 2015 DarkKat
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

package net.darkkatrom.dksettings.fragments.themecolors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.ColorConstants;
import com.android.internal.util.darkkat.StatusBarColorHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsColorPickerFragment;

import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorsStatusBar extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_TEXT_COLOR =
            "colors_status_bar_text_color";
    private static final String PREF_ICON_COLOR =
            "colors_status_bar_icon_color";
    private static final String PREF_TEXT_COLOR_DARK_MODE =
            "colors_status_bar_text_color_dark_mode";
    private static final String PREF_ICON_COLOR_DARK_MODE =
            "colors_status_bar_icon_color_dark_mode";
    private static final String PREF_BATTERY_TEXT_COLOR =
            "colors_status_bar_battery_text_color";
    private static final String PREF_BATTERY_TEXT_COLOR_DARK_MODE =
            "colors_status_bar_battery_text_color_dark_mode";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mTextColorDarkMode;
    private ColorPickerPreference mIconColorDarkMode;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextColorDarkMode;

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

        addPreferencesFromResource(R.xml.colors_status_bar);
        mResolver = getContentResolver();

        int intColor;

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = StatusBarColorHelper.getTextColor(getActivity());
        mTextColor.setNewColor(intColor);
        mTextColor.setResetColors(ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE,
                ColorConstants.HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = StatusBarColorHelper.getIconColor(getActivity());
        mIconColor.setNewColor(intColor);
        mIconColor.setResetColors(ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE,
                ColorConstants.HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        mTextColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getTextColorDarkMode(getActivity());
        mTextColorDarkMode.setNewColor(intColor);
        mTextColorDarkMode.setResetColors(ColorConstants.DARK_MODE_COLOR_SINGLE_TONE,
                ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
        mTextColorDarkMode.setOnPreferenceChangeListener(this);

        mIconColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getIconColorDarkMode(getActivity());
        mIconColorDarkMode.setNewColor(intColor);
        mIconColorDarkMode.setResetColors(ColorConstants.DARK_MODE_COLOR_SINGLE_TONE,
                ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
        mIconColorDarkMode.setOnPreferenceChangeListener(this);

        mBatteryTextColor =
                (ColorPickerPreference) findPreference(PREF_BATTERY_TEXT_COLOR);
        intColor = StatusBarColorHelper.getBatteryTextColor(getActivity());
        mBatteryTextColor.setNewColor(intColor);
        mBatteryTextColor.setResetColors(ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE,
                ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE);
        mBatteryTextColor.setOnPreferenceChangeListener(this);

        mBatteryTextColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_BATTERY_TEXT_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getBatteryTextColorDarkMode(getActivity());
        mBatteryTextColorDarkMode.setNewColor(intColor);
        mBatteryTextColorDarkMode.setResetColors(ColorConstants.DARK_MODE_COLOR_SINGLE_TONE,
                ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
        mBatteryTextColorDarkMode.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ICON_COLOR, intHex);
            return true;
        } else if (preference == mTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE, intHex);
            return true;
        } else if (preference == mIconColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE, intHex);
            return true;
        } else if (preference == mBatteryTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mBatteryTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE, intHex);
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        ColorsStatusBar getOwner() {
            return (ColorsStatusBar) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.dlg_cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR,
                                    ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR,
                                    ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    ColorConstants.LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE,
                                    ColorConstants.DARK_MODE_COLOR_SINGLE_TONE);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
