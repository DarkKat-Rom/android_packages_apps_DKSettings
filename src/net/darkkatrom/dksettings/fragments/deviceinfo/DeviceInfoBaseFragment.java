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

package net.darkkatrom.dksettings.fragments.deviceinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.fragments.SettingsBaseFragment;

public class DeviceInfoBaseFragment extends SettingsBaseFragment {
    private static final String TAG = "DeviceInfoBaseFragment";

    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
    private static final String FILENAME_PROC_MEMINFO = "/proc/meminfo";
    private static final String FILENAME_PROC_CPUINFO = "/proc/cpuinfo";

    protected boolean isWifiOnly(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return (cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE) == false);
    }

    protected void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    protected void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {
            // No recovery
        }
    }

    protected String getCPUInfo() {
        String result = null;
        int coreCount = 0;

        try {
            /* The expected /proc/cpuinfo output is as follows:
             * Processor	: ARMv7 Processor rev 2 (v7l)
             * BogoMIPS	: 272.62
             * BogoMIPS	: 272.62
             *
             * On kernel 3.10 this changed, it is now the last
             * line. So let's read the whole thing, search
             * specifically for "Processor" or "model name"
             * and retain the old
             * "first line" as fallback.
             * Also, use "processor : <id>" to count cores
             */
            BufferedReader ci = new BufferedReader(new FileReader(FILENAME_PROC_CPUINFO));
            String firstLine = ci.readLine();
            String latestLine = firstLine;
            while (latestLine != null) {
                if (latestLine.startsWith("Processor")
                    || latestLine.startsWith("model name"))
                  result = latestLine.split(":")[1].trim();
                if (latestLine.startsWith("processor"))
                  coreCount++;
                latestLine = ci.readLine();
            }
            if (result == null && firstLine != null) {
                result = firstLine.split(":")[1].trim();
            }
            /* Don't do this. hotplug throws off the count
            if (coreCount > 1) {
                result = result + " (x" + coreCount + ")";
            }
            */
            ci.close();
        } catch (IOException e) {}

        if (result == null || result.equals("0")) {
            if (!getCpuInfoOverlay().isEmpty()) {
                result = getCpuInfoOverlay();
            }
        }
        return result;
    }

    private String getCpuInfoOverlay() {
        return getActivity().getResources().getString(
                R.string.device_cpu_value);
    }

    protected static String getMemInfo() {
        String result = null;
        BufferedReader reader = null;

        try {
            /* /proc/meminfo entries follow this format:
             * MemTotal:         362096 kB
             * MemFree:           29144 kB
             * Buffers:            5236 kB
             * Cached:            81652 kB
             */
            String firstLine = readLine(FILENAME_PROC_MEMINFO);
            if (firstLine != null) {
                String parts[] = firstLine.split("\\s+");
                if (parts.length == 3) {
                    result = Long.parseLong(parts[1])/1024 + " MB";
                }
            }
        } catch (IOException e) {}

        return result;
    }

    /** The following code is a copy from:
     *  com.android.settingslib.DeviceInfoUtils
     *  (frameworks/base/packages/SettingsLib/src/com/android/settingslib.DeviceInfoUtils)
     */

    /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                "(#\\d+) " +              /* group 3: "#1" */
                "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns "".
     * @return a string to append to the model number description.
     */
    public static String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException|NumberFormatException e) {
            // Fail quietly, as the file may not exist on some devices, or may be unreadable
        }
        return "";
    }
}
