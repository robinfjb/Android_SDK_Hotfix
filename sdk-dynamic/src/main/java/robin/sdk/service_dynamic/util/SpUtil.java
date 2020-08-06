package robin.sdk.service_dynamic.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import robin.sdk.service_dynamic.net.DyInfo;

public final class SpUtil {
    public static void setDyInfo(Context context, DyInfo dyInfo) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("dy_info", dyInfo.toJsonStr());
        editor.apply();
    }

    public static DyInfo getDyInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String dyinfoStr = sp.getString("dy_info", "");
        DyInfo dyInfo = null;
        try {
            dyInfo = new DyInfo(new JSONObject(dyinfoStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dyInfo;
    }

    public static void setVersionName(Context context, String version) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("version", version);
        editor.apply();
    }

    public static String getVersionName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getString("version", null);
    }

    public static void setPatchVersion(Context context, int version) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("patch_version", version);
        editor.apply();
    }

    public static int getPatchVersionName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getInt("patch_version", -1);
    }

    public static void setAppKey(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("appkey", key);
        editor.apply();
    }

    public static String getAppKey(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getString("appkey", null);
    }

}
