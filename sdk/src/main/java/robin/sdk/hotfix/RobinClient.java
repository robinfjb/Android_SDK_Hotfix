package robin.sdk.hotfix;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import robin.sdk.sdk_impl.Constants;
import robin.sdk.sdk_impl.util.LogUtil;

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
                    LogUtil.e(TAG_CLIENT, "Client receive:" + msg.obj.toString());
                default:
            }
        }
    }

    public void start(Context context,SdkListener sdkListener) {
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
            Object obj = new Object();
            LogUtil.e(TAG_CLIENT, "Client send:" + obj.toString());
            message.obj = obj;
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
