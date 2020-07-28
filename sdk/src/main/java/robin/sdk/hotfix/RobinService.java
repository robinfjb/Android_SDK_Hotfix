package robin.sdk.hotfix;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import robin.sdk.service_dynamic.ServiceManager;
import robin.sdk.sdk_impl.ServiceProxy;
import robin.sdk.service_dynamic.UpdateManager;

public final class RobinService extends Service {
    public Context context = null;
    private ServiceProxy lib;

    public RobinService() {
    }

    @Override
    public void onCreate() {
        try {
            context = getApplicationContext();
            lib = serviceLoad();
            lib.onCreate(this);
            checkUpdate();
        } catch (Throwable e) {
        }
    }

    private ServiceProxy serviceLoad() {
        ServiceManager serviceManager = new ServiceManager();
        return serviceManager.getServiceProxy(this);
    }

    private void checkUpdate() {
        UpdateManager.checkUpdate(this);
    }

    @Override
    public int onStartCommand(Intent var1, int var2, int var3) {
        try {
            if (lib != null) {
                return lib.onStartCommand(var1, var2, var3);
            } else {
                return Service.START_NOT_STICKY;
            }
        } catch (Throwable e) {
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent var1) {
        try {
            if (lib != null) {
                return lib.onBind(var1);
            } else {
                return null;
            }
        } catch (Throwable e) {
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent var1) {
        try {
            return lib == null || lib.onUnBind(var1);
        } catch (Throwable e) {
        }
        return false;
    }

    @Override
    public void onDestroy() {
        try {
            lib.onDestroy();
        } catch (Throwable e) {
        }
    }
}
