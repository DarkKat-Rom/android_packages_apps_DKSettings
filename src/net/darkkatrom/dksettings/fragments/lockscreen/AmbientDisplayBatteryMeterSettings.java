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

package net.darkkatrom.dksettings.fragments.lockscreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsBaseFragment;

public class AmbientDisplayBatteryMeterSettings extends SettingsBaseFragment implements
        Preference.OnPreferenceChangeListener {

    public static final int ICON_VERTICAL = 0;
    public static final int CIRCLE        = 3;
    public static final int ARCS          = 4;
    public static final int TEXT_ONLY     = 5;
    public static final int HIDDEN        = 6;

    private static final String PREF_CAT_CIRCLE_DOTTED =
            "ambient_display_battery_meter_cat_circle_dotted";
    private static final String PREF_CAT_TEXT =
            "ambient_display_battery_meter_cat_text";
    private static final String PREF_TYPE =
            "ambient_display_battery_meter_type";
    private static final String PREF_SHOW_TEXT =
            "ambient_display_battery_meter_show_text";
    private static final String PREF_CIRCLE_DOT_INTERVAL =
            "ambient_display_battery_meter_circle_dot_interval";
    private static final String PREF_CIRCLE_DOT_LENGTH =
            "ambient_display_battery_meter_circle_dot_length";
    private static final String PREF_CUT_OUT_TEXT =
            "ambient_display_battery_meter_cut_out_text";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ListPreference mType;
    private SwitchPreference mShowText;
    private ListPreference mCircleDotInterval;
    private ListPreference mCircleDotLength;
    private SwitchPreference mCutOutText;

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

        addPreferencesFromResource(R.xml.ambient_display_battery_meter_settings);
        mResolver = getActivity().getContentResolver();

        final boolean showBattery = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, 0) != HIDDEN;
        final boolean isBatteryIconCircle = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, 0) == CIRCLE;
        final boolean isBatteryIconArcs = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, 0) == ARCS;
        final boolean isBatteryTextOnly = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, 0) == TEXT_ONLY;
        final boolean showText = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_SHOW_TEXT, 0) == 1;
        final boolean showCircleDotted = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_INTERVAL, 0) != 0;

        PreferenceCategory catCircleDotted =
                (PreferenceCategory) findPreference(PREF_CAT_CIRCLE_DOTTED);
        PreferenceCategory catText =
                (PreferenceCategory) findPreference(PREF_CAT_TEXT);

        mType = (ListPreference) findPreference(PREF_TYPE);
        int type = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, ICON_VERTICAL);
        mType.setValue(String.valueOf(type));
        mType.setSummary(mType.getEntry());
        mType.setOnPreferenceChangeListener(this);

        if (showBattery) {
            if (!isBatteryTextOnly) {
                mShowText = (SwitchPreference) findPreference(PREF_SHOW_TEXT);
                mShowText.setChecked(showText);
                mShowText.setOnPreferenceChangeListener(this);

                mCutOutText = (SwitchPreference) findPreference(PREF_CUT_OUT_TEXT);
                mCutOutText.setChecked(Settings.System.getInt(mResolver,
                       Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CUT_OUT_TEXT, 1) == 1);
                mCutOutText.setOnPreferenceChangeListener(this);
            } else {
                removePreference(PREF_SHOW_TEXT);
                catText.removePreference(findPreference(PREF_CUT_OUT_TEXT));
                removePreference(PREF_CAT_TEXT);
            }

            if (isBatteryIconCircle) {
                mCircleDotInterval = (ListPreference) findPreference(PREF_CIRCLE_DOT_INTERVAL);
                int circleDotInterval = Settings.System.getInt(mResolver,
                        Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_INTERVAL, 0);
                mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
                mCircleDotInterval.setOnPreferenceChangeListener(this);
                updateCircleDotIntervalSummary(circleDotInterval);

                if (showCircleDotted) {
                    mCircleDotLength = (ListPreference) findPreference(PREF_CIRCLE_DOT_LENGTH);
                    int circleDotLength = Settings.System.getInt(mResolver,
                            Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_LENGTH, 0);
                    mCircleDotLength.setValue(String.valueOf(circleDotLength));
                    mCircleDotLength.setSummary(mCircleDotLength.getEntry());
                    mCircleDotLength.setOnPreferenceChangeListener(this);
                } else {
                    catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_LENGTH));
                }
            } else {
                catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_INTERVAL));
                catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_LENGTH));
                removePreference(PREF_CAT_CIRCLE_DOTTED);
            }

            if (isBatteryIconCircle || isBatteryIconArcs) {
                catText.removePreference(findPreference(PREF_CUT_OUT_TEXT));
                removePreference(PREF_CAT_TEXT);
            }
        } else {
            removePreference(PREF_SHOW_TEXT);
            catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_INTERVAL));
            catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_LENGTH));
            catText.removePreference(findPreference(PREF_CUT_OUT_TEXT));
            removePreference(PREF_CAT_CIRCLE_DOTTED);
            removePreference(PREF_CAT_TEXT);
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intValue;
        int index;

        if (preference == mType) {
            intValue = Integer.valueOf((String) newValue);
            index = mType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE,
                    intValue);
            mType.setSummary(mType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_SHOW_TEXT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mCircleDotInterval) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_INTERVAL,
                    intValue);
            refreshSettings();
            return true;
        } else if (preference == mCircleDotLength) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_LENGTH,
                    intValue);
            mCircleDotLength.setSummary(mCircleDotLength.getEntries()[index]);
            return true;
        } else if (preference == mCutOutText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CUT_OUT_TEXT,
                    value ? 1 : 0);
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

        AmbientDisplayBatteryMeterSettings getOwner() {
            return (AmbientDisplayBatteryMeterSettings) getTargetFragment();
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
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, ICON_VERTICAL);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_SHOW_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_INTERVAL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_LENGTH, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CUT_OUT_TEXT, 1);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_TYPE, CIRCLE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_SHOW_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BATTERY_METER_CUT_OUT_TEXT, 0);
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

    private void updateCircleDotIntervalSummary(int circleDotInterval) {
        CharSequence summary;
        if (circleDotInterval != 0) {
            summary = mCircleDotInterval.getEntry();
        } else {
            summary = getResources().getString(R.string.battery_meter_circle_dot_no_dot_summary);
        }
        mCircleDotInterval.setSummary(summary);
    }
}