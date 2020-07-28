package robin.sdk.service_dynamic;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;

import dalvik.system.DexClassLoader;
import robin.sdk.sdk_impl.ServiceImpl;
import robin.sdk.sdk_impl.ServiceProxy;
import robin.sdk.service_dynamic.net.DyInfo;
import robin.sdk.service_dynamic.util.FileUtil;
import robin.sdk.sdk_impl.util.LogUtil;
import robin.sdk.service_dynamic.util.Md5Util;
import robin.sdk.service_dynamic.util.SpUtil;

/**
 * @author hao
 */

public class ServiceManager {
    private static final String DYNAMIC_TAG = "DYNAMIC";
    private static final ServiceManager MANAGER = new ServiceManager();

    public static ServiceManager getManager() {
        return MANAGER;
    }

    public ServiceProxy getServiceProxy(Context context) {
        ServiceProxy libNat = null;
        ServiceProxy libJar = null;
        ServiceProxy lib = null;

        File usingJar = new File(UpdateManager.getUsingJarPath(context));
        File newJar = new File(UpdateManager.getDyJarPath(context));
        if (newJar.exists()) {
            LogUtil.e(DYNAMIC_TAG, "检测到新的动态包");
            LogUtil.e(DYNAMIC_TAG, "动态包已开始加载 md5 :" + Md5Util.getMd5(newJar) + " 文件大小:" + newJar.length() / 1024 + "KB");
            if (usingJar.exists()) {
                LogUtil.e(DYNAMIC_TAG, "原动态包已删除");
                usingJar.delete();
            }
            FileUtil.fileCopy(newJar, usingJar);
            LogUtil.e(DYNAMIC_TAG, "动态包替换完成");
        } else {
            LogUtil.e(DYNAMIC_TAG, "未检测到动态包");
        }

        //TODO 应该默认使用上一个应用成功的
        libNat = new ServiceImpl();

        try {
            String dyinfoStr = SpUtil.getDyInfo(context);
            DyInfo dyInfo = new DyInfo(new JSONObject(dyinfoStr));
            if (checkJar(dyInfo, usingJar)) {
                DexClassLoader dexClassLoader = new DexClassLoader(usingJar.getAbsolutePath(),
                        context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
                Class libclass = dexClassLoader.loadClass(dyInfo.packageName + ".ServiceImpl");
                libJar = (ServiceProxy) libclass.newInstance();
                LogUtil.e(DYNAMIC_TAG, "动态包已加载成功 ");
                newJar.delete();
            }
        } catch (Throwable throwable) {
            LogUtil.e(DYNAMIC_TAG, "动态包加载异常:" + throwable.getLocalizedMessage());
            libJar = null;
        }

        if (libJar != null) {
            int libJarVersionCode = libJar.getVersionCode();
            int libNatVersionCode = libNat.getVersionCode();
            LogUtil.e(DYNAMIC_TAG, "动态包版本号：" + libJarVersionCode + "  本地包版本号：" + libNatVersionCode);

            if (libJar.getVersionCode() >= libNat.getVersionCode()) {
                LogUtil.e(DYNAMIC_TAG, "动态包版本大于等于本地包，使用动态包");
                lib = libJar;
            } else {
                LogUtil.e(DYNAMIC_TAG, "动态包版本小于本地包，使用本地包");
                lib = libNat;
            }
        } else {
            LogUtil.e(DYNAMIC_TAG, "未检索到动态包，使用本地包");
            lib = libNat;
        }
        return lib;
    }

    private boolean checkJar(DyInfo dyInfo, File jar) {
        if(dyInfo == null || TextUtils.isEmpty(dyInfo.packageName) || !jar.exists()) {
            return false;
        }
        String md5 = Md5Util.getMd5(jar);
        String spjarMd5 = dyInfo.checksumValue;
        return !TextUtils.isEmpty(spjarMd5) && spjarMd5.equals(md5);
    }
}
