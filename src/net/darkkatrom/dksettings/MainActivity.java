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
import net.darkkatrom.dksettings.fragments.PowerButtonSettings;
import net.darkkatrom.dksettings.fragments.MainSettings;

public class MainActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback, ColorPickerPreference.OwnerActivity {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    private int mThemeResId = 0;
    private boolean mCustomizeColors = false;
    private int mStatusBarColor = 0;
    private int mDefaultPrimaryColor = 0;
    private int mPrimaryColor = 0;
    private int mNavigationColor = 0;
    private boolean mColorizeNavigationBar = false;
    private boolean mLightStatusBar = false;
    private boolean mLightActionBar = false;
    private boolean mLightNavigationBar = false;
    private boolean mIsBlackoutTheme = false;
    private boolean mIsWhiteoutTheme = false;

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
        mCustomizeColors = ThemeColorHelper.customizeColors(this);
        mDefaultPrimaryColor = getColor(R.color.theme_primary);
        mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, mDefaultPrimaryColor);
        mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
        mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, mDefaultPrimaryColor);
        mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
        mLightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
        mLightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
        mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
        mIsBlackoutTheme = ThemeHelper.isWhiteoutTheme(this);
        mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);

        if (mLightActionBar && mLightNavigationBar) {
            mThemeResId = mLightStatusBar
                    ? R.style.AppTheme_LightStatusBar_LightNavigationBar
                    : R.style.AppTheme_LightActionBar_LightNavigationBar;
        } else if (mLightActionBar) {
            mThemeResId = mLightStatusBar
                    ? R.style.AppTheme_LightStatusBar
                    : R.style.AppTheme_LightActionBar;
        } else if (mLightNavigationBar) {
            mThemeResId = R.style.AppTheme_LightNavigationBar;
        } else {
            mThemeResId = R.style.AppTheme;
        }
        setTheme(mThemeResId);

        if (getThemeOverlayAccentResId() > 0) {
            getTheme().applyStyle(getThemeOverlayAccentResId(), true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mLightStatusBar) {
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set.
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mLightNavigationBar) {
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

        if (mCustomizeColors && !mIsBlackoutTheme && !mIsWhiteoutTheme) {
            getWindow().setStatusBarColor(mStatusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        }
        if (mNavigationColor != 0) {
            getWindow().setNavigationBarColor(mNavigationColor);
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

    private int getThemeOverlayAccentResId() {
        int index = ThemeColorHelper.getIndexForAccentColor(this);

        int resId = 0;
        if (index == 1) {
            resId = R.style.ThemeOverlay_Accent_HoloBlueLight;
        } else if (index == 2) {
            resId = R.style.ThemeOverlay_Accent_MaterialBlueGray500;
        } else if (index == 3) {
            resId = R.style.ThemeOverlay_Accent_MaterialBlue500;
        } else if (index == 4) {
            resId = R.style.ThemeOverlay_Accent_MaterialLightBlue500;
        } else if (index == 5) {
            resId = R.style.ThemeOverlay_Accent_MaterialCyan500;
        } else if (index == 6) {
            resId = R.style.ThemeOverlay_Accent_MaterialDeepTeal500;
        } else if (index == 7) {
            resId = R.style.ThemeOverlay_Accent_MaterialIndigo500;
        } else if (index == 8) {
            resId = R.style.ThemeOverlay_Accent_MaterialPurple500;
        } else if (index == 9) {
            resId = R.style.ThemeOverlay_Accent_MaterialDeepPurple500;
        } else if (index == 10) {
            resId = R.style.ThemeOverlay_Accent_MaterialPink500;
        } else if (index == 11) {
            resId = R.style.ThemeOverlay_Accent_MaterialOrange500;
        } else if (index == 12) {
            resId = R.style.ThemeOverlay_Accent_MaterialDeepOrange500;
        } else if (index == 13) {
            resId = R.style.ThemeOverlay_Accent_MaterialRed500;
        } else if (index == 14) {
            resId = R.style.ThemeOverlay_Accent_MaterialYellow500;
        } else if (index == 15) {
            resId = R.style.ThemeOverlay_Accent_MaterialAmber500;
        } else if (index == 16) {
            resId = R.style.ThemeOverlay_Accent_MaterialGreen500;
        } else if (index == 17) {
            resId = R.style.ThemeOverlay_Accent_MaterialLightGreen500;
        } else if (index == 18) {
            resId = R.style.ThemeOverlay_Accent_MaterialLime500;
        } else if (index == 19) {
            resId = R.style.ThemeOverlay_Accent_Black;
        } else if (index == 20) {
            resId = R.style.ThemeOverlay_Accent_White;
        } else if (index == 21) {
            resId = R.style.ThemeOverlay_Accent_Blue;
        } else if (index == 22) {
            resId = R.style.ThemeOverlay_Accent_Purple;
        } else if (index == 23) {
            resId = R.style.ThemeOverlay_Accent_Orange;
        } else if (index == 24) {
            resId = R.style.ThemeOverlay_Accent_Red;
        } else if (index == 25) {
            resId = R.style.ThemeOverlay_Accent_Yellow;
        } else if (index == 26) {
            resId = R.style.ThemeOverlay_Accent_Green;
        }
        return resId;
    }

    @Override
    public int getThemeResId() {
        return mThemeResId;
    }

    @Override
    public boolean customizeColors() {
        return mCustomizeColors;
    }

    @Override
    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    @Override
    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    @Override
    public int getNavigationColor() {
        return mNavigationColor;
    }

    @Override
    public boolean colorizeNavigationBar() {
        return mColorizeNavigationBar;
    }

    @Override
    public boolean lightStatusBar() {
        return mLightStatusBar;
    }

    @Override
    public boolean lightActionBar() {
        return mLightActionBar;
    }

    @Override
    public boolean lightNavigationBar() {
        return mLightNavigationBar;
    }

    @Override
    public boolean isWhiteoutTheme() {
        return mIsWhiteoutTheme;
    }

    @Override
    public boolean isBlackoutTheme() {
        return mIsBlackoutTheme;
    }
}
