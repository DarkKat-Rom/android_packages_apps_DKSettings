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

package net.darkkatrom.dksettings.fragments.colors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.ColorConstants;
import com.android.internal.util.darkkat.NavigationBarColorHelper;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dkcolorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorsNavigationBar extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_LIGHT_MODE =
            "colors_navigation_bar_cat_light_mode";
    private static final String PREF_CAT_DARK_MODE =
            "colors_navigation_bar_cat_dark_mode";
    private static final String PREF_ICON_COLOR_FOR_RIPPLE =
            "colors_navigation_bar_icon_color_for_ripple";
    private static final String PREF_ICON_COLOR_LIGHT_MODE =
            "colors_navigation_bar_icon_light_mode";
    private static final String PREF_RIPPLE_COLOR_LIGHT_MODE =
            "colors_navigation_bar_ripple_light_mode";
    private static final String PREF_ICON_COLOR_DARK_MODE =
            "colors_navigation_bar_icon_dark_mode";
    private static final String PREF_RIPPLE_COLOR_DARK_MODE =
            "colors_navigation_bar_ripple_dark_mode";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mIconColorForRipple;
    private ColorPickerPreference mIconColorLightMode;
    private ColorPickerPreference mRippleColorLightMode;
    private ColorPickerPreference mIconColorDarkMode;
    private ColorPickerPreference mRippleColorDarkMode;

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

        addPreferencesFromResource(R.xml.colors_navigation_bar);
        mResolver = getContentResolver();

        final boolean iconColorForRipple = NavigationBarColorHelper.iconColorForRipple(getActivity());
        int intColor;

        PreferenceCategory catLightMode =
                (PreferenceCategory) findPreference(PREF_CAT_LIGHT_MODE);
        PreferenceCategory catDarkMode =
                (PreferenceCategory) findPreference(PREF_CAT_DARK_MODE);

        mIconColorForRipple = (SwitchPreference) findPreference(PREF_ICON_COLOR_FOR_RIPPLE);
        mIconColorForRipple.setChecked(iconColorForRipple);
        mIconColorForRipple.setOnPreferenceChangeListener(this);

        mIconColorLightMode =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_LIGHT_MODE);
        intColor = NavigationBarColorHelper.getIconColorLightMode(getActivity());
        mIconColorLightMode.setNewColor(intColor);
        mIconColorLightMode.setResetColors(ColorConstants.WHITE,
                ColorConstants.HOLO_BLUE_LIGHT);
        mIconColorLightMode.setOnPreferenceChangeListener(this);

        mIconColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_MODE);
        intColor = NavigationBarColorHelper.getIconColorDarkMode(getActivity());
        mIconColorDarkMode.setNewColor(intColor);
        mIconColorLightMode.setResetColors(ColorConstants.BLACK,
                ColorConstants.BLACK);
        mIconColorDarkMode.setOnPreferenceChangeListener(this);

        if (!iconColorForRipple) {
            mRippleColorLightMode =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR_LIGHT_MODE);
            intColor = NavigationBarColorHelper.getRippleColorLightMode(getActivity());
            mRippleColorLightMode.setNewColor(intColor);
            mRippleColorLightMode.setResetColors(ColorConstants.WHITE,
                    ColorConstants.HOLO_BLUE_LIGHT);
            mRippleColorLightMode.setOnPreferenceChangeListener(this);

            mRippleColorDarkMode =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR_DARK_MODE);
            intColor = NavigationBarColorHelper.getRippleColorDarkMode(getActivity());
            mRippleColorDarkMode.setNewColor(intColor);
            mRippleColorDarkMode.setResetColors(ColorConstants.BLACK,
                    ColorConstants.BLACK);
            mRippleColorDarkMode.setOnPreferenceChangeListener(this);
        } else {
            catLightMode.removePreference(findPreference(PREF_RIPPLE_COLOR_LIGHT_MODE));
            catDarkMode.removePreference(findPreference(PREF_RIPPLE_COLOR_DARK_MODE));
        }

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

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_colors_navigation_bar;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mIconColorForRipple) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ICON_COLOR_FOR_RIPPLE, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mIconColorLightMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ICON_COLOR_LIGHT_MODE, intHex);
            return true;
        } else if (preference == mRippleColorLightMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_LIGHT_MODE, intHex);
            return true;
        } else if (preference == mIconColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ICON_COLOR_DARK_MODE, intHex);
            return true;
        } else if (preference == mRippleColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_DARK_MODE, intHex);
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

        ColorsNavigationBar getOwner() {
            return (ColorsNavigationBar) getTargetFragment();
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
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_FOR_RIPPLE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_LIGHT_MODE,
                                    ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_LIGHT_MODE,
                                    ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_DARK_MODE,
                                    ColorConstants.BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_DARK_MODE,
                                    ColorConstants.BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_FOR_RIPPLE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_LIGHT_MODE,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_LIGHT_MODE,
                                    ColorConstants.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_DARK_MODE,
                                    ColorConstants.BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_RIPPLE_COLOR_DARK_MODE,
                                    ColorConstants.BLACK);
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
