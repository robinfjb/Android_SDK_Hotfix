package robin.sdk.service_dynamic;

import android.content.Context;
import android.text.TextUtils;


import org.json.JSONObject;

import java.io.File;

import robin.sdk.service_dynamic.net.DownloadTask;
import robin.sdk.service_dynamic.net.DyInfo;
import robin.sdk.service_dynamic.net.HttpUrlTask;
import robin.sdk.sdk_common.util.LogUtil;
import robin.sdk.service_dynamic.util.Md5Util;
import robin.sdk.service_dynamic.util.SpUtil;


public class UpdateManager {
    private static final String UPDATE_TAG = "UPDATE";
    public static final String REPLACE_FILE_NAME = "new.jar";//新的jar包
    public static final String JAR_FILE_NAME = "dex.jar";//当前使用的jar包

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
        new HttpUrlTask(10000, "", "GET", "http://robinfjb.github.io/dynamic_info.json",
                "", false, new HttpUrlTask.TaskListener() {
            @Override
            public void onTaskComplete(int statusCode, String statusMessage, String response) {
                if (statusCode == 200) {
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            DyInfo dyInfo = new DyInfo(new JSONObject(response));
                            if (checkDyInfo(context, dyInfo)) {//检查配置文件有效性
                                LogUtil.e(UPDATE_TAG, "downloadDyJar");
                                downloadDyJar(dyInfo, context);
                            }
                        } catch (Exception ignored) {
                            LogUtil.e(UPDATE_TAG, "parse Json fail :" + ignored.getLocalizedMessage());
                        }
                    } else {
                        LogUtil.e(UPDATE_TAG, "response empty");
                    }
                } else {
                    LogUtil.e(UPDATE_TAG, "statusCode = "+ statusCode);
                }
            }
        }).execute();
    }

    public static void checkUpdate(Context context) {
        getDyInfo(context);
    }

    /**
     * 是否需要下载运行补丁
     */
    public static boolean checkDyInfo(Context context, DyInfo dyInfo) {
        if (dyInfo == null) {
            return false;
        }
        LogUtil.e(UPDATE_TAG, "dyInfo:" + dyInfo.toString());
        if (!"md5".equals(dyInfo.checksumType)) {
            LogUtil.e(UPDATE_TAG, "checksumType error");
            return false;
        }
        if (TextUtils.isEmpty(dyInfo.packageUrl)) {
            LogUtil.e(UPDATE_TAG, "packageUrl error");
            return false;
        }
        String currentVersion = SpUtil.getVersionName(context);
        if (TextUtils.isEmpty(currentVersion)) {
            LogUtil.e(UPDATE_TAG, "currentVersion error");
            return false;
        }
        String key = SpUtil.getAppKey(context);
        if (TextUtils.isEmpty(key)) {
            LogUtil.e(UPDATE_TAG, "key error");
            return false;
        }
        //针对某个key更新
        if(!TextUtils.isEmpty(dyInfo.channel) && !dyInfo.channel.equalsIgnoreCase(key)) {
            LogUtil.e(UPDATE_TAG, "channel error");
            return false;
        }
        //目前运行的补丁号
        int patchVersion = SpUtil.getPatchVersionName(context);
        boolean result = currentVersion.equalsIgnoreCase(dyInfo.targetVersion) && patchVersion < dyInfo.subVersion;
        LogUtil.e(UPDATE_TAG, "currentVersion:" + currentVersion);
        LogUtil.e(UPDATE_TAG, "patchVersion:" + patchVersion);
        return result;
    }

    private static void downloadDyJar(final DyInfo dyInfo, final Context context) {
        try {
            new DownloadTask(dyInfo.packageUrl, getDyJarPath(context), new DownloadTask.DownloadListener() {
                @Override
                public void onSuccess() {
                    try {
                        if (!checkJarMd5(context, dyInfo.checksumValue)) {
                            LogUtil.e(UPDATE_TAG, "checkJarMd5 fail");
                            deleteFailjar(context);
                        } else {
                            //保存jar信息
                            SpUtil.setDyInfo(context, dyInfo);
                            LogUtil.e(UPDATE_TAG, "checkJarMd5 success");
                        }
                    } catch (Throwable ignored) {
                    }
                }

                @Override
                public void onFail(String msg, int code) {
                    LogUtil.e(UPDATE_TAG, "downlo jar fail:" + msg);
                }
            }).executeWithThreadPool();
        } catch (Throwable e) {
        }
    }

    private static void deleteFailjar(Context context) {
        File file = new File(getDyJarPath(context));
        file.delete();
    }

    public static boolean checkJarMd5(Context context, String md5Value) {
        File file = new File(getDyJarPath(context));
        String md5 = Md5Util.getMd5(file);
        boolean result = !TextUtils.isEmpty(md5Value) && !TextUtils.isEmpty(md5) && md5Value.equals(md5);
        LogUtil.e(UPDATE_TAG, "md5=" + md5);
        return result;
    }
}
