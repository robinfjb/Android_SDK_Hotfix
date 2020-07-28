package robin.sdk.service_dynamic;

import android.content.Context;
import android.text.TextUtils;


import org.json.JSONObject;

import java.io.File;

import robin.sdk.service_dynamic.net.DownloadTask;
import robin.sdk.service_dynamic.net.DyInfo;
import robin.sdk.service_dynamic.net.HttpUrlTask;
import robin.sdk.sdk_impl.util.LogUtil;
import robin.sdk.service_dynamic.util.Md5Util;
import robin.sdk.service_dynamic.util.SpUtil;


public class UpdateManager {
    private static final String UPDATE_TAG = "UPDATE";
    public static final String REPLACE_FILE_NAME = "new.jar";//新的jar包
    public static final String JAR_FILE_NAME = "app.jar";//当前使用的jar包

    public static String getDyJarPath(Context context) {
        return getNewfileDir(context) + File.separator + REPLACE_FILE_NAME;
    }

    public static String getUsingJarPath(Context context) {
        return getNewfileDir(context) + File.separator + JAR_FILE_NAME;
    }

    public static String getNewfileDir(Context context) {
        try {
            File f = new File(context.getFilesDir() + File.separator + "robin");
            if (!f.exists()) {
                f.mkdirs();
            }
            return f.getAbsolutePath();
        } catch (Exception var1) {
            return null;
        }
    }

    private static void getDyInfo(final Context context) {
        new HttpUrlTask(10000, "", "get", "http://",
                "", false, new HttpUrlTask.TaskListener() {
            @Override
            public void onTaskComplete(int statusCode, String statusMessage, String response) {
                if (statusCode == 200) {
                    if (response != null && !TextUtils.isEmpty(response)) {
                        try {
                            DyInfo dyInfo = new DyInfo(new JSONObject(response));
                            if (checkDyInfo(dyInfo)) {//检查配置文件有效性
                                SpUtil.setDyInfo(context, response);
                                downloadDyJar(dyInfo.packageUrl, context);
                            }
                        } catch (Exception ignored) {
                            LogUtil.e(UPDATE_TAG, "parse Json fail :" + ignored.getLocalizedMessage());
                        }
                    }
                    SpUtil.setLastUpdateFail(context, 0);
                } else {
                    SpUtil.setLastUpdateFail(context, System.currentTimeMillis());
                }
            }
        }).executeWithThreadPool();
    }

    private static boolean checkDyInfo(DyInfo dyInfo) {
        if (dyInfo == null) {
            return false;
        }
        LogUtil.e(UPDATE_TAG, "dyInfo:" + dyInfo.toString());
        /* just support md5 */
        if (!"md5".equals(dyInfo.checksumType)) {
            return false;
        }
        if (TextUtils.isEmpty(dyInfo.packageUrl)) {
            return false;
        }
        return BuildConfig.VERSION_CODE < dyInfo.version && BuildConfig.VERSION_CODE > dyInfo.minSupportVersion;
    }

    public static void checkUpdate(Context context) {
        if (checkDate(context)) {
            SpUtil.setLastUpdate(context, System.currentTimeMillis());
            getDyInfo(context);
        }
    }

    private static boolean checkDate(Context context) {//防止频繁更新
        try {
            long lastUpdate = SpUtil.getLastCheckUpdate(context);
            long lastUpdateFail = SpUtil.getLastUpdateFail(context);
            LogUtil.e(UPDATE_TAG,
                    "checkDate:\nlastCheckUpdate:" + (lastUpdate == 0 ? " never update before " : ((System.currentTimeMillis() - lastUpdate) / (1000 * 60 * 60)) + " hour")
                            + "\nlastCheckUpdateFail:" + (lastUpdateFail == 0 ? " lastUpdateSuccess " : ((System.currentTimeMillis() - lastUpdateFail) / (1000 * 60 * 60)) + " hour")
            );
                /* 首次安装 */
            return lastUpdate == 0
                /* 一小时后 */
                    || (System.currentTimeMillis() - lastUpdate) > 60 * 60 * 1000
                /* 上次更新失败 24小时后*/
                    || ((System.currentTimeMillis() - lastUpdateFail) > 1000 * 60 * 60 * 24) && lastUpdateFail != 0;
        } catch (Throwable e) {
        }
        return true;
    }

    private static void downloadDyJar(String url, final Context context) {
        try {
            new DownloadTask(url, getDyJarPath(context), new DownloadTask.DownloadListener() {
                @Override
                public void onSuccess() {
                    try {
                        if (!checkJarMd5(context)) {
                            LogUtil.e(UPDATE_TAG, "checkJarMd5 fail");
                            deleteFailjar(context);
                            SpUtil.setLastUpdateFail(context, System.currentTimeMillis());
                        } else {
                            SpUtil.setLastUpdateFail(context, 0);
                            LogUtil.e(UPDATE_TAG, "checkJarMd5 success");
                        }
                    } catch (Throwable ignored) {
                    }
                }

                @Override
                public void onFail(String msg, int code) {
                    SpUtil.setLastUpdateFail(context, System.currentTimeMillis());
                }
            }).executeWithThreadPool();
        } catch (Throwable e) {
        }
    }

    private static void deleteFailjar(Context context) {
        File file = new File(getDyJarPath(context));
        file.delete();
    }

    public static boolean checkJarMd5(Context context) {
        File file = new File(getDyJarPath(context));
        String md5 = Md5Util.getMd5(file);
        String dyinfoStr = SpUtil.getDyInfo(context);
        String spjarMd5 = null;
        try {
            DyInfo dyInfo = new DyInfo(new JSONObject(dyinfoStr));
            spjarMd5 = dyInfo.checksumValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !TextUtils.isEmpty(spjarMd5) && !TextUtils.isEmpty(md5) && spjarMd5.equals(md5);
    }
}
