package robin.sdk.service_dynamic.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class SpUtil {
    public static void setDyInfo(Context context, String md5) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("dy_info", md5);
        editor.apply();
    }

    public static String getDyInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getString("dy_info", "");
    }

    public static void setLastUpdateFail(Context context, long lastUpdate) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("last_fail_time", lastUpdate);
        editor.apply();
    }

    public static long getLastUpdateFail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getLong("last_fail_time", 0);
    }

    public static void setLastUpdate(Context context, long lastUpdate) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("last_update_time", lastUpdate);
        editor.apply();
    }

    public static long getLastCheckUpdate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getLong("last_update_time", 0);
    }
}
