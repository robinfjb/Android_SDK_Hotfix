package robin.sdk.hotfix;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import robin.sdk.proxy.ServiceProxy;
import robin.sdk.service_dynamic.ServiceManager;
import robin.sdk.service_dynamic.UpdateManager;

public final class RobinService extends Service {
    public Context context = null;
    private ServiceProxy proxy;

    public RobinService() {
    }

    @Override
    public void onCreate() {
        try {
            context = getApplicationContext();
            proxy = serviceLoad();
            proxy.onCreate(this);
            checkUpdate();
        } catch (Throwable e) {
        }
    }

    private ServiceProxy serviceLoad() {
        return ServiceManager.getManager().getServiceProxy(this);
    }

    private void checkUpdate() {
        UpdateManager.checkUpdate(this);
    }

    @Override
    public int onStartCommand(Intent var1, int var2, int var3) {
        return proxy.onStartCommand(var1, var2, var3);
    }

    @Override
    public IBinder onBind(Intent var1) {
        return proxy.onBind(var1);
    }

    @Override
    public boolean onUnbind(Intent var1) {
        return proxy.onUnBind(var1);
    }

    @Override
    public void onDestroy() {
        proxy.onDestroy();
    }
}
