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
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.DetailedWeatherColorHelper;
import com.android.internal.util.darkkat.DetailedWeatherThemeHelper;
import com.android.internal.util.darkkat.ThemeHelper;
import com.android.internal.util.darkkat.WeatherHelper;
import com.android.internal.util.darkkat.WeatherServiceControllerImpl;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsColorPickerFragment;

import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorsDetailedWeatherView extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_BG_COLORS =
            "colors_detailed_weather_cat_background_colors";
    private static final String PREF_CAT_TEXT_COLORS =
            "colors_detailed_weather_cat_text_colors";
    private static final String PREF_CAT_ICON_COLORS =
            "colors_detailed_weather_cat_icon_colors";
    private static final String PREF_CAT_RIPPLE_COLORS =
            "colors_detailed_weather_cat_ripple_colors";
    private static final String PREF_USE_THEME_COLORS =
            "colors_detailed_weather_view_use_theme_colors";
    private static final String PREF_ACCENT_COLOR =
            "colors_detailed_weather_accent_color";
    private static final String PREF_STATUS_BAR_BG_COLOR =
            "colors_detailed_weather_status_bar_bg_color";
    private static final String PREF_ACTION_BAR_BG_COLOR =
            "colors_detailed_weather_action_bar_bg_color";
    private static final String PREF_CONTENT_BG_COLOR =
            "colors_detailed_weather_content_bg_color";
    private static final String PREF_CARD_BG_COLOR =
            "colors_detailed_weather_card_bg_color";
    private static final String PREF_ACTION_BAR_TEXT_COLOR =
            "colors_detailed_weather_action_bar_text_color";
    private static final String PREF_CARD_TEXT_COLOR =
            "colors_detailed_weather_card_text_color";
    private static final String PREF_ACTION_BAR_ICON_COLOR =
            "colors_detailed_weather_action_bar_icon_color";
    private static final String PREF_CARD_ICON_COLOR =
            "colors_detailed_weather_card_icon_color";
    private static final String PREF_ACTION_BAR_RIPPLE_COLOR =
            "colors_detailed_weather_action_bar_ripple_color";
    private static final String PREF_CARD_RIPPLE_COLOR =
            "colors_detailed_weather_card_ripple_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int MENU_SHOW  = 2;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mUseThemeColors;
    private ColorPickerPreference mAccentColor;
    private ColorPickerPreference mStatusBarBgColor;
    private ColorPickerPreference mActionBarBgColor;
    private ColorPickerPreference mContentBgColor;
    private ColorPickerPreference mCardBgColor;
    private ColorPickerPreference mActionBarTextColor;
    private ColorPickerPreference mCardTextColor;
    private ColorPickerPreference mActionBarIconColor;
    private ColorPickerPreference mCardIconColor;
    private ColorPickerPreference mActionBarRippleColor;
    private ColorPickerPreference mCardRippleColor;

    private boolean mCustomizeColors;
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

        addPreferencesFromResource(R.xml.colors_detailed_weather_view);
        mResolver = getContentResolver();

        PreferenceCategory catBgColors =
                (PreferenceCategory) findPreference(PREF_CAT_BG_COLORS);
        PreferenceCategory catTextColors =
                (PreferenceCategory) findPreference(PREF_CAT_TEXT_COLORS);
        PreferenceCategory catIconColors =
                (PreferenceCategory) findPreference(PREF_CAT_ICON_COLORS);
        PreferenceCategory catRippleColors =
                (PreferenceCategory) findPreference(PREF_CAT_RIPPLE_COLORS);

        mCustomizeColors = !ThemeHelper.detailedWeatherUseThemeColors(getActivity());

        mUseThemeColors = (SwitchPreference) findPreference(PREF_USE_THEME_COLORS);
        mUseThemeColors.setChecked(!mCustomizeColors);
        mUseThemeColors.setOnPreferenceChangeListener(this);

        if (mCustomizeColors) {
            int intColor;
            int defaultColor;

            mAccentColor =
                    (ColorPickerPreference) findPreference(PREF_ACCENT_COLOR);
            intColor = DetailedWeatherColorHelper.getAccentColor(getActivity());
            mAccentColor.setNewColor(intColor);
            mAccentColor.setResetColor(DetailedWeatherThemeHelper.getAccentColor(getActivity()));
            mAccentColor.setResetColorTitle(R.string.reset_theme_default_title);
            mAccentColor.setOnPreferenceChangeListener(this);

            mStatusBarBgColor =
                    (ColorPickerPreference) findPreference(PREF_STATUS_BAR_BG_COLOR);
            intColor = DetailedWeatherColorHelper.getStatusBarBgColor(getActivity());
            mStatusBarBgColor.setNewColor(intColor);
            mStatusBarBgColor.setResetColor(
                    DetailedWeatherThemeHelper.getStatusBarBgColor(getActivity()));
            mStatusBarBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mStatusBarBgColor.setOnPreferenceChangeListener(this);

            mActionBarBgColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_BG_COLOR);
            intColor = DetailedWeatherColorHelper.getActionBarBgColor(getActivity());
            mActionBarBgColor.setNewColor(intColor);
            mActionBarBgColor.setResetColor(
                    DetailedWeatherThemeHelper.getActionBarBgColor(getActivity()));
            mActionBarBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarBgColor.setOnPreferenceChangeListener(this);

            mContentBgColor =
                    (ColorPickerPreference) findPreference(PREF_CONTENT_BG_COLOR);
            intColor = DetailedWeatherColorHelper.getContentBgColor(getActivity());
            mContentBgColor.setNewColor(intColor);
            mContentBgColor.setResetColor(DetailedWeatherThemeHelper.getContentBgColor(getActivity()));
            mContentBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mContentBgColor.setOnPreferenceChangeListener(this);

            mCardBgColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_BG_COLOR);
            intColor = DetailedWeatherColorHelper.getCardBgColor(getActivity());
            mCardBgColor.setNewColor(intColor);
            mCardBgColor.setResetColor(DetailedWeatherThemeHelper.getCardBgColor(getActivity()));
            mCardBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardBgColor.setOnPreferenceChangeListener(this);

            mActionBarTextColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_TEXT_COLOR);
            intColor = DetailedWeatherColorHelper.getActionBarPrimaryTextColor(getActivity());
            mActionBarTextColor.setNewColor(intColor);
            mActionBarTextColor.setResetColor(DetailedWeatherThemeHelper.getActionBarTextColor());
            mActionBarTextColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarTextColor.setOnPreferenceChangeListener(this);

            mCardTextColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_TEXT_COLOR);
            intColor = DetailedWeatherColorHelper.getCardPrimaryTextColor(getActivity());
            mCardTextColor.setNewColor(intColor);
            mCardTextColor.setResetColor(DetailedWeatherThemeHelper.getPrimaryTextColor(getActivity()));
            mCardTextColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardTextColor.setOnPreferenceChangeListener(this);

            mActionBarIconColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_ICON_COLOR);
            intColor = DetailedWeatherColorHelper.getActionBarIconColor(getActivity());
            mActionBarIconColor.setNewColor(intColor);
            mActionBarIconColor.setResetColor(DetailedWeatherThemeHelper.getActionBarIconColor());
            mActionBarIconColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarIconColor.setOnPreferenceChangeListener(this);

            mCardIconColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_ICON_COLOR);
            intColor = DetailedWeatherColorHelper.getCardIconColor(getActivity());
            mCardIconColor.setNewColor(intColor);
            mCardIconColor.setResetColor(DetailedWeatherThemeHelper.getIconColor(getActivity()));
            mCardIconColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardIconColor.setOnPreferenceChangeListener(this);

            mActionBarRippleColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_RIPPLE_COLOR);
            intColor = DetailedWeatherColorHelper.getActionBarRippleColor(getActivity());
            mActionBarRippleColor.setNewColor(intColor);
            mActionBarRippleColor.setResetColor(DetailedWeatherThemeHelper.getActionBarRippleColor());
            mActionBarRippleColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarRippleColor.setOnPreferenceChangeListener(this);

            mCardRippleColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_RIPPLE_COLOR);
            intColor = DetailedWeatherColorHelper.getCardRippleColor(getActivity());
            mCardRippleColor.setNewColor(intColor);
            mCardRippleColor.setResetColor(DetailedWeatherThemeHelper.getRippleColor(getActivity()));
            mCardRippleColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardRippleColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_ACCENT_COLOR);
            catBgColors.removePreference(findPreference(PREF_STATUS_BAR_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_ACTION_BAR_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_CONTENT_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_CARD_BG_COLOR));
            catTextColors.removePreference(findPreference(PREF_ACTION_BAR_TEXT_COLOR));
            catTextColors.removePreference(findPreference(PREF_CARD_TEXT_COLOR));
            catIconColors.removePreference(findPreference(PREF_ACTION_BAR_ICON_COLOR));
            catIconColors.removePreference(findPreference(PREF_CARD_ICON_COLOR));
            catRippleColors.removePreference(findPreference(PREF_ACTION_BAR_RIPPLE_COLOR));
            catRippleColors.removePreference(findPreference(PREF_CARD_RIPPLE_COLOR));
            removePreference(PREF_CAT_BG_COLORS);
            removePreference(PREF_CAT_TEXT_COLORS);
            removePreference(PREF_CAT_ICON_COLORS);
            removePreference(PREF_CAT_RIPPLE_COLORS);
        }

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, MENU_SHOW, 1, R.string.action_show_detailed_weather_view_title)
                .setIcon(R.drawable.ic_action_show_detailed_weather_view)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(mCustomizeColors);
        menu.getItem(0).setVisible(mCustomizeColors);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
            case MENU_SHOW:
                Bundle b = new Bundle();
                b.putInt(WeatherHelper.DAY_INDEX, 0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(WeatherServiceControllerImpl.COMPONENT_DETAILED_WEATHER);
                intent.putExtras(b);
                startActivity(intent);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intHex;
        String hex;

        if (preference == mUseThemeColors) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_USE_THEME_COLORS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mAccentColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACCENT_COLOR, intHex);
            return true;
        } else if (preference == mStatusBarBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_STATUS_BAR_BG_COLOR, intHex);
            return true;
        } else if (preference == mActionBarBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_BG_COLOR, intHex);
            return true;
        } else if (preference == mContentBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CONTENT_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARD_BG_COLOR, intHex);
            return true;
        } else if (preference == mActionBarTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mCardTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARD_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mActionBarIconColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_ICON_COLOR, intHex);
            return true;
        } else if (preference == mCardIconColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARD_ICON_COLOR, intHex);
            return true;
        } else if (preference == mActionBarRippleColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_RIPPLE_COLOR, intHex);
            return true;
        } else if (preference == mCardRippleColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARD_RIPPLE_COLOR, intHex);
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

        ColorsDetailedWeatherView getOwner() {
            return (ColorsDetailedWeatherView) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_theme_default_colors_message)
                    .setNegativeButton(R.string.dlg_cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACCENT_COLOR,
                                    DetailedWeatherThemeHelper.getAccentColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_STATUS_BAR_BG_COLOR,
                                    DetailedWeatherThemeHelper.getStatusBarBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_BG_COLOR,
                                    DetailedWeatherThemeHelper.getActionBarBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CONTENT_BG_COLOR,
                                    DetailedWeatherThemeHelper.getContentBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARD_BG_COLOR,
                                    DetailedWeatherThemeHelper.getCardBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_TEXT_COLOR,
                                    DetailedWeatherThemeHelper.getActionBarTextColor());
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARD_TEXT_COLOR,
                                    DetailedWeatherThemeHelper.getPrimaryTextColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_ICON_COLOR,
                                    DetailedWeatherThemeHelper.getActionBarIconColor());
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARD_ICON_COLOR,
                                    DetailedWeatherThemeHelper.getIconColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_RIPPLE_COLOR,
                                    DetailedWeatherThemeHelper.getActionBarRippleColor());
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARD_RIPPLE_COLOR,
                                    DetailedWeatherThemeHelper.getRippleColor(getActivity()));
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
