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

package net.darkkatrom.dksettings.fragments.themecolors;

import android.provider.Settings;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.SettingsColorPickerFragment;
import net.darkkatrom.dksettings.utils.NotificationUtil;
import net.darkkatrom.dksettings.utils.PreferenceUtils;

public class NotificationTestSettings extends SettingsColorPickerFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_ACTIONS =
            PreferenceUtils.KEY_CAT_ACTIONS;
    private static final String PREF_TYPE =
            PreferenceUtils.KEY_TYPE;
    private static final String PREF_NUMBER_OF_ACTIONS =
            PreferenceUtils.KEY_NUMBER_OF_ACTIONS;
    private static final String PREF_SHOW_EMPHASIZED_ACTIONS =
            PreferenceUtils.KEY_SHOW_EMPHASIZED_ACTIONS;
    private static final String PREF_SHOW_TOMBSTONE_ACTIONS =
            PreferenceUtils.KEY_SHOW_TOMBSTONE_ACTIONS;
    private static final String PREF_SHOW_REPLY_ACTION =
            PreferenceUtils.KEY_SHOW_REPLY_ACTION;
    private static final String PREF_COLOR =
            PreferenceUtils.KEY_NOTIFICATION_COLOR;

    private static final int MENU_SEND = Menu.FIRST;

    private PreferenceUtils mUtils;

    private Preference mType;
    private Preference mNumberOfActions;
    private Preference mShowEmphasizedActions;
    private Preference mShowReplyAction;
    private ColorPickerPreference mColor;

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

        addPreferencesFromResource(R.xml.notification_test_settings);

        mUtils = new PreferenceUtils(getActivity().getApplicationContext());

        PreferenceCategory catActions =
                (PreferenceCategory) findPreference(PREF_CAT_ACTIONS);

        mType = findPreference(PREF_TYPE);
        mType.setOnPreferenceChangeListener(this);

        if (mUtils.getType() != PreferenceUtils.TYPE_MEDIA) {
            mNumberOfActions = findPreference(PREF_NUMBER_OF_ACTIONS);
            mNumberOfActions.setOnPreferenceChangeListener(this);

            int numberOfActions = mUtils.getNumberOfActions();
            if (numberOfActions == PreferenceUtils.NO_ACTIONS) {
                catActions.removePreference(findPreference(PREF_SHOW_EMPHASIZED_ACTIONS));
                catActions.removePreference(findPreference(PREF_SHOW_TOMBSTONE_ACTIONS));
                catActions.removePreference(findPreference(PREF_SHOW_REPLY_ACTION));
            } else {
                mShowEmphasizedActions = findPreference(PREF_SHOW_EMPHASIZED_ACTIONS);
                mShowEmphasizedActions.setOnPreferenceChangeListener(this);

                mShowReplyAction = findPreference(PREF_SHOW_REPLY_ACTION);
                mShowReplyAction.setOnPreferenceChangeListener(this);

                updateTombstonePreferenceEnabledState();
            }
        } else {
            catActions.removePreference(findPreference(PREF_NUMBER_OF_ACTIONS));
            catActions.removePreference(findPreference(PREF_SHOW_EMPHASIZED_ACTIONS));
            catActions.removePreference(findPreference(PREF_SHOW_TOMBSTONE_ACTIONS));
            catActions.removePreference(findPreference(PREF_SHOW_REPLY_ACTION));
            removePreference(PREF_CAT_ACTIONS);
        }

        mColor = (ColorPickerPreference) findPreference(PREF_COLOR);
        mColor.setNewColor(mUtils.getNotificationColor());
        mColor.setResetColor(mUtils.getDefaultNotificationColor());
        mColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_SEND, 0, R.string.send_title)
                .setIcon(R.drawable.ic_action_send)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEND:
                sendNotification();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mType || preference == mNumberOfActions) {
            ((ListPreference) preference).setValue((String) newValue);
            refreshSettings();
            return true;
        } else if (preference == mShowEmphasizedActions || preference == mShowReplyAction) {
            ((SwitchPreference) preference).setChecked((Boolean) newValue);
            updateTombstonePreferenceEnabledState();
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_TEST_NOTIFICATION_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void sendNotification() {
        NotificationUtil notificationUtil = new NotificationUtil(getActivity(), mUtils);
        notificationUtil.sendNotification();
    }

    private void updateTombstonePreferenceEnabledState() {
        int numberOfActions = mUtils.getNumberOfActions();
        boolean showOneAction = numberOfActions == PreferenceUtils.ONE_ACTION;
        boolean disableTombstoneActions = mUtils.getShowEmphasizedActions()
                || (mUtils.getShowReplyAction() && showOneAction);
        int summaryResid = disableTombstoneActions
                ? R.string.notification_test_show_tombstone_actions_disabled_summary
                : R.string.notification_test_show_tombstone_actions_summary;
        Preference tombstone = findPreference(PREF_SHOW_TOMBSTONE_ACTIONS);
        tombstone.setEnabled(!disableTombstoneActions);
        tombstone.setSummary(summaryResid);
    }
}
