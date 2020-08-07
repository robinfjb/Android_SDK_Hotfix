package robin.sdk.sdk_impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import robin.sdk.proxy.BuildConfig;
import robin.sdk.proxy.ServiceProxy;
import robin.sdk.sdk_common.Constants;
import robin.sdk.sdk_common.util.LogUtil;

public class ServiceImpl implements ServiceProxy {
    private static final String TAG_SERVICE = "TAG_SERVICE";
    private Messenger mService = new Messenger(new ReceiverHandler());
    private Messenger mClient;
    private Message receiveMsg;
    private BizTest bizTest2;

    class ReceiverHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constants.MSG_FROM_CLIENT:
                    receiveMsg = msg;
                    LogUtil.e(TAG_SERVICE, "Service receive:" + msg.obj.toString());
                    sendMsg2Client();
                default:
            }
        }
    }

    @Override
    public void onCreate(Context var1) {
        bizTest2 = new BizTest();
        LogUtil.e(TAG_SERVICE, "get str=" + bizTest2.testStr());
        LogUtil.e(TAG_SERVICE, "get static=" + BizTest.testStatic());
    }

    @Override
    public int onStartCommand(Intent var1, int var2, int var3) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent var1) {
        return mService.getBinder();
    }

    @Override
    public boolean onUnBind(Intent var1) {
        return false;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    private void sendMsg2Client() {
        Message message = Message.obtain(null, Constants.MSG_FROM_SERVER);
        LogUtil.e(TAG_SERVICE, "Service send:" + ServiceImpl.class.getName());
        message.obj = ServiceImpl.class.getName();
        mClient = receiveMsg.replyTo;
        try {
            mClient.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
