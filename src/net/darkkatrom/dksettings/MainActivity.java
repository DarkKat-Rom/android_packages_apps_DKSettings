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

package net.darkkatrom.dksettings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.View;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dkcolorpicker.util.ThemeInfo;
import net.darkkatrom.dksettings.fragments.PowerButtonSettings;
import net.darkkatrom.dksettings.fragments.MainSettings;

public class MainActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback, ColorPickerPreference.OwnerActivity {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainSettings())
                    .commit();
        }
    }

    private void updateTheme() {
        int defaultPrimaryColor = getColor(com.android.internal.R.color.primary_color_darkkat);
        int themeResId = ThemeHelper.getDKThemeResId(this);
        int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, defaultPrimaryColor);
        boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, defaultPrimaryColor);
        int statusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, defaultPrimaryColor);
        int primaryColor = ThemeColorHelper.getPrimaryColor(this, defaultPrimaryColor);
        boolean customizeColors = ThemeColorHelper.customizeColors(this);
        int navigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, defaultPrimaryColor);

        if (themeResId > 0) {
            setTheme(themeResId);
        }

        if (themeOverlayAccentResId > 0) {
            getTheme().applyStyle(themeOverlayAccentResId, true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!lightStatusBar) {
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set.
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!lightNavigationBar) {
            // Check if light navigation bar flag was set
            boolean isLightNavigationBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            if (isLightNavigationBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        if (oldFlags != newFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newFlags);
        }

        if (customizeColors && !ThemeHelper.isBlackoutTheme(this)
                    && !ThemeHelper.isWhiteoutTheme(this)) {
            getWindow().setStatusBarColor(statusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        }
        if (navigationColor != 0) {
            getWindow().setNavigationBarColor(navigationColor);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        if (pref instanceof ColorPickerPreference) {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), caller, ColorPickerPreference.RESULT_REQUEST_CODE);
        } else {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), null, 0);
        }
        return true;
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, int titleRes,
        CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        Fragment f = Fragment.instantiate(this, fragmentClass, args);
        if (resultTo != null) {
            f.setTargetFragment(resultTo, resultRequestCode);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, f);
        if (titleRes != 0) {
            transaction.setBreadCrumbTitle(titleRes);
        } else if (titleText != null) {
            transaction.setBreadCrumbTitle(titleText);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void recreateForThemeChange() {
        recreate();
    }

    @Override
    public ThemeInfo getThemeInfo() {
        return null;
    }
}
