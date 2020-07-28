package robin.sdk.hotfix;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import robin.sdk.sdk_impl.ServiceImpl;
import robin.sdk.sdk_impl.util.LogUtil;

public class RobinClient {
    private SdkListener sdkListener;

    public void start(Context context,SdkListener sdkListener) {
        this.sdkListener = sdkListener;

        Intent intent = new Intent(context, RobinService.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void stop(Context context) {
        this.sdkListener = null;
        context.getApplicationContext().unbindService(connection);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            String serviceData = ((ServiceImpl.MyBinder)service).getData();
            LogUtil.e("RobinClient", "receive data from service:" + serviceData);
            if(sdkListener != null) {
                sdkListener.onSuccess(serviceData);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
