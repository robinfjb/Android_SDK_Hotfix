/*
 * Copyright (C) 2017 GlobalEarth, Inc. All Rights Reserved.
 */

package robin.sdk.sdk_impl.util;

import android.util.Log;

import robin.sdk.sdk_impl.BuildConfig;

public final class LogUtil {
    private static int LOG_MAXLENGTH = 2000;
    private static int MAX_LOG_LINE = 200;
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void e_(String TAG, String msg) {
        if (DEBUG) {
            Log.i("[" + getTag(TAG, 0) + "]", msg);
        }
    }

    private static String getTag(String tag, int i) {
        return "[" + tag + "-" + i + " on Thread:" + Thread.currentThread().getName() + "]";
    }

    public static void e(String TAG, String msg) {
        if (DEBUG) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < MAX_LOG_LINE; i++) {
                if (strLength > end) {
                    Log.i(getTag(TAG, i), msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.i(getTag(TAG, i), msg.substring(start, strLength));
                    break;
                }
            }
        }
    }
}
