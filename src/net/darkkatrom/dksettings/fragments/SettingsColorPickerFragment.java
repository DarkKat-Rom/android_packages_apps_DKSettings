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

package net.darkkatrom.dksettings.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class SettingsColorPickerFragment extends SettingsBaseFragment {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ColorPickerPreference.RESULT_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            String extraNewColor = ColorPickerFragment.KEY_NEW_COLOR;
            if (extras != null && extras.getInt(extraNewColor) != 0) {
                String extraPrefKey = ColorPickerPreference.PREFERENCE_KEY;
                ((ColorPickerPreference) findPreference(extras.getString(extraPrefKey)))
                        .setNewColor(extras.getInt(extraNewColor));
            }
        }
    }
}
