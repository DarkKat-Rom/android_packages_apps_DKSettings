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
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.View;

import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dksettings.fragments.PowerButtonSettings;
import net.darkkatrom.dksettings.fragments.MainSettings;

public class MainActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean useOptionalLightStatusBar = ThemeHelper.themeSupportsOptionalĹightSB(this)
                && ThemeHelper.useLightStatusBar(this);
        boolean useOptionalLightNavigationBar = ThemeHelper.themeSupportsOptionalĹightNB(this)
                && ThemeHelper.useLightNavigationBar(this);
        int themeResId = 0;

        if (useOptionalLightStatusBar && useOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_Light_LightStatusNavigationBar;
        } else if (useOptionalLightStatusBar) {
            themeResId = R.style.ThemeOverlay_Light_LightStatusBar;
        } else if (useOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_Light_LightNavigationBar;
        } else {
            themeResId = R.style.AppTheme;
        }
        setTheme(themeResId);

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!useOptionalLightStatusBar) {
            // Possibly we are using the Whiteout theme
            boolean isWhiteoutTheme =
                    ThemeHelper.getTheme(this) == UiModeManager.MODE_NIGHT_NO_WHITEOUT;
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set,
            // and we are not using the Whiteout theme,
            // (Whiteout theme should always use a light status bar).
            if (isLightStatusBar && !isWhiteoutTheme) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (useOptionalLightNavigationBar) {
            newFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
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

        if (savedInstanceState == null && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainSettings())
                    .commit();
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
}
