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

package net.darkkatrom.dksettings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.TypedValue;

import net.darkkatrom.dksettings.R;

public final class PreferenceUtils {

    public static final String TEXT_REPLY = "key_text_reply";
    public static final String NOTIFICATION_ID = "notification_id";

    public static final String KEY_CAT_ACTIONS             = "notification_test_cat_actions";
    public static final String KEY_TYPE                    = "notification_test_type";
    public static final String KEY_PRIORITY                = "notification_test_priority";
    public static final String KEY_NUMBER_OF_ACTIONS       = "notification_test_number_of_actions";
    public static final String KEY_SHOW_EMPHASIZED_ACTIONS = "notification_test_show_emphasized_actions";
    public static final String KEY_SHOW_TOMBSTONE_ACTIONS  = "notification_test_show_tombstone_actions";
    public static final String KEY_SHOW_REPLY_ACTION       = "notification_test_show_reply_action";
    public static final String KEY_NOTIFICATION_COLOR      = "notification_test_color";

    public static final int NUMBER_OF_ACTIONS_DEFAULT  = 0;

    public static final int TYPE_DEFAULT  = 0;
    public static final int TYPE_TEXT     = 1;
    public static final int TYPE_PICTURE  = 2;
    public static final int TYPE_INBOX    = 3;
    public static final int TYPE_MESSAGE  = 4;
    public static final int TYPE_MEDIA    = 5;

    public static final int PRIORITY_NONE    = 0;
    public static final int PRIORITY_MIN     = 1;
    public static final int PRIORITY_LOW     = 2;
    public static final int PRIORITY_DEFAULT = 3;
    public static final int PRIORITY_HIGH    = 4;
    public static final int PRIORITY_MAX     = 5;

    public static final int NO_ACTIONS  = 0;
    public static final int ONE_ACTION  = 1;

    private static final String TYPE_DEFAULT_VALUE     = "0";
    private static final String PRIORITY_DEFAULT_VALUE = "0";
    private static final String NO_ACTIONS_VALUE       = "0";

    private static PreferenceUtils sInstance;
    private final Context mContext;

    private final SharedPreferences mPreferences;

    public PreferenceUtils(final Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context);
        }
        return sInstance;
    }

    public int getType() {
        String value = mPreferences.getString(KEY_TYPE, TYPE_DEFAULT_VALUE);
        return Integer.valueOf(value);
    }

    public int getPriority() {
        String value = mPreferences.getString(KEY_PRIORITY, PRIORITY_DEFAULT_VALUE);
        return Integer.valueOf(value);
    }

    public int getNumberOfActions() {
        String value = mPreferences.getString(KEY_NUMBER_OF_ACTIONS, NO_ACTIONS_VALUE);
        return Integer.valueOf(value);
    }

    public boolean getShowEmphasizedActions() {
        return mPreferences.getBoolean(KEY_SHOW_EMPHASIZED_ACTIONS, false);
    }

    public boolean getShowTombstoneActions() {
        return mPreferences.getBoolean(KEY_SHOW_TOMBSTONE_ACTIONS, false);
    }

    public boolean getShowReplyAction() {
        return mPreferences.getBoolean(KEY_SHOW_REPLY_ACTION, false);
    }

    public int getNotificationColor() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NOTIFICATION_TEST_NOTIFICATION_COLOR, getDefaultNotificationColor());
    }

    public int getDefaultNotificationColor() {
        // The default notification color is related to the the accent color of the current theme, 
        // as the app uses different themes when launched via system settings or launcher,
        // we have to optain the current accent color via theme attribute here.
        TypedValue tv = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.colorAccent, tv, true);
        int defaultColor;
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            defaultColor = tv.data;
        } else {
            defaultColor = mContext.getColor(tv.resourceId);
        }
        return defaultColor;
    }

    public int getNotificationId() {
        return mPreferences.getInt(NOTIFICATION_ID, 1);
    }

    public void setNotificationId(int id) {
        mPreferences.edit().putInt(NOTIFICATION_ID, id).commit();
    }
}
