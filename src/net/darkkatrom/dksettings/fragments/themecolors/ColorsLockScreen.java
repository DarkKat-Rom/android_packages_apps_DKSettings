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
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.ColorConstants;
import com.android.internal.util.darkkat.LockScreenColorHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dkcolorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorsLockScreen extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_COLORIZE_VISUALIZER =
            "colors_lock_screen_colorize_visualizer";
    private static final String PREF_TEXT_COLOR_DARK_WALLPAPER =
            "colors_lock_screen_text_color_dark_wallpaper";
    private static final String PREF_ICON_COLOR_DARK_WALLPAPER =
            "colors_lock_screen_icon_color_dark_wallpaper";
    private static final String PREF_TEXT_COLOR_LIGHT_WALLPAPER =
            "colors_lock_screen_text_color_light_wallpaper";
    private static final String PREF_ICON_COLOR_LIGHT_WALLPAPER =
            "colors_lock_screen_icon_color_light_wallpaper";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mColorizeVisualizer;
    private ColorPickerPreference mTextColorDarkWallpaper;
    private ColorPickerPreference mIconColorDarkWallpaper;
    private ColorPickerPreference mTextColorLightWallpaper;
    private ColorPickerPreference mIconColorLightWallpaper;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.colors_lock_screen);
        mResolver = getContentResolver();

        int intColor;

        mColorizeVisualizer = (SwitchPreference) findPreference(PREF_COLORIZE_VISUALIZER);
        mColorizeVisualizer.setChecked(LockScreenColorHelper.colorizeVisualizer(getActivity()));
        mColorizeVisualizer.setOnPreferenceChangeListener(this);

        mTextColorDarkWallpaper =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_DARK_WALLPAPER);
        intColor = LockScreenColorHelper.getPrimaryTextColorDark(getActivity());
        mTextColorDarkWallpaper.setNewColor(intColor);
        mTextColorDarkWallpaper.setResetColors(ColorConstants.WHITE,
                ColorConstants.HOLO_BLUE_LIGHT);
        mTextColorDarkWallpaper.setOnPreferenceChangeListener(this);

        mIconColorDarkWallpaper =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_WALLPAPER);
        intColor = LockScreenColorHelper.getIconColorDark(getActivity());
        mIconColorDarkWallpaper.setNewColor(intColor);
        mIconColorDarkWallpaper.setResetColors(ColorConstants.WHITE,
                ColorConstants.HOLO_BLUE_LIGHT);
        mIconColorDarkWallpaper.setOnPreferenceChangeListener(this);

        mTextColorLightWallpaper =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_LIGHT_WALLPAPER);
        intColor = LockScreenColorHelper.getPrimaryTextColorLight(getActivity(), true);
        mTextColorLightWallpaper.setNewColor(intColor);
        mTextColorLightWallpaper.setResetColors(ColorConstants.BLACK,
                ColorConstants.HOLO_BLUE_LIGHT);
        mTextColorLightWallpaper.setOnPreferenceChangeListener(this);

        mIconColorLightWallpaper =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_LIGHT_WALLPAPER);
        intColor = LockScreenColorHelper.getIconColorLight(getActivity());
        mIconColorLightWallpaper.setNewColor(intColor);
        mIconColorLightWallpaper.setResetColors(ColorConstants.BLACK,
                ColorConstants.HOLO_BLUE_LIGHT);
        mIconColorLightWallpaper.setOnPreferenceChangeListener(this);

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

        if (preference == mColorizeVisualizer) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_COLORIZE_VISUALIZER, value ? 1 : 0);
            return true;
        } else if (preference == mTextColorDarkWallpaper) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_TEXT_COLOR_DARK, intHex);
            return true;
        } else if (preference == mIconColorDarkWallpaper) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_ICON_COLOR_DARK, intHex);
            return true;
        } else if (preference == mTextColorLightWallpaper) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_TEXT_COLOR_LIGHT, intHex);
            return true;
        } else if (preference == mIconColorLightWallpaper) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_ICON_COLOR_LIGHT, intHex);
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

        ColorsLockScreen getOwner() {
            return (ColorsLockScreen) getTargetFragment();
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
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR_DARK, ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR_DARK, ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR_LIGHT, ColorConstants.BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR_LIGHT, ColorConstants.BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR_DARK,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR_DARK,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR_LIGHT,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR_LIGHT,
                                    ColorConstants.HOLO_BLUE_LIGHT);
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
