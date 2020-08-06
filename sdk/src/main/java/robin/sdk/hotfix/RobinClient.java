package robin.sdk.hotfix;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import robin.sdk.sdk_impl.Constants;
import robin.sdk.sdk_impl.util.LogUtil;
import robin.sdk.service_dynamic.util.SpUtil;

public class RobinClient {
    private static final String TAG_CLIENT = "TAG_CLIENT";
    private boolean mShouldUnbind;
    private SdkListener sdkListener;
    private Messenger mService;
    private Messenger mClient = new Messenger(new ReceiveHandler());
    private class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constants.MSG_FROM_SERVER:
                    LogUtil.e(TAG_CLIENT, "RobinClient receive:" + msg.obj.toString());
                default:
            }
        }
    }

    public RobinClient(Context context) {
        SpUtil.setVersionName(context.getApplicationContext(), BuildConfig.VERSION_NAME);

        try {
            ApplicationInfo applicationInfo = context.getApplicationContext().getPackageManager()
                    .getApplicationInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                String key = applicationInfo.metaData.getString("robin.appkey");
                SpUtil.setAppKey(context.getApplicationContext(), key);
            }
        } catch (Throwable ignored) {
        }
    }

    public void start(Context context, SdkListener sdkListener) {
        this.sdkListener = sdkListener;
        Intent intent = new Intent(context, RobinService.class);
        mShouldUnbind = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void stop(Context context) {
        this.sdkListener = null;
        if(mShouldUnbind) {
            mShouldUnbind = false;
            context.unbindService(connection);
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message message = Message.obtain(null, Constants.MSG_FROM_CLIENT);
            LogUtil.e(TAG_CLIENT, "RobinClient send:" + RobinClient.this.getClass().getName());
            message.obj = RobinClient.this.getClass().getName();
            message.replyTo = mClient;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
