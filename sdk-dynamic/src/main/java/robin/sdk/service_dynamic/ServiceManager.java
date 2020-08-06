package robin.sdk.service_dynamic;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import dalvik.system.DexClassLoader;
import robin.sdk.proxy.ServiceProxy;
import robin.sdk.sdk_impl.ServiceImpl;
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
            newJar.delete();
        } else {
            LogUtil.e(DYNAMIC_TAG, "未检测到新的动态包");
        }

        lib = new ServiceImpl();

        try {
            DyInfo dyInfo = SpUtil.getDyInfo(context);
            if (checkJar(dyInfo, usingJar)) {
                DexClassLoader dexClassLoader = new DexClassLoader(usingJar.getAbsolutePath(),
                        context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
                Class libclass = dexClassLoader.loadClass(dyInfo.className);
                lib = (ServiceProxy) libclass.newInstance();
                SpUtil.setPatchVersion(context, dyInfo.subVersion);
                LogUtil.e(DYNAMIC_TAG, "动态包已加载成功 ");
            }
        } catch (Throwable throwable) {
            LogUtil.e(DYNAMIC_TAG, "动态包加载异常:" + throwable.getLocalizedMessage());
            lib = new ServiceImpl();
        }
        return lib;
    }

    private boolean checkJar(DyInfo dyInfo, File jar) {
        if(dyInfo == null || TextUtils.isEmpty(dyInfo.className) || !jar.exists()) {
            return false;
        }
        String md5 = Md5Util.getMd5(jar);
        String spjarMd5 = dyInfo.checksumValue;
        return !TextUtils.isEmpty(spjarMd5) && spjarMd5.equals(md5);
    }
}
