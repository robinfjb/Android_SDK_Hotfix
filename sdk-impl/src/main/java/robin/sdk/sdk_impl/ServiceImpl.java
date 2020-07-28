package robin.sdk.sdk_impl;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import robin.sdk.sdk_impl.util.LogUtil;

public class ServiceImpl implements ServiceProxy{
    public class MyBinder extends Binder {
        public String getData() {
            return "1";
        }
    }
    private MyBinder binder = new MyBinder();

    @Override
    public void onCreate(Context var1) {

    }

    @Override
    public int onStartCommand(Intent var1, int var2, int var3) {
        return 0;
    }

    @Override
    public IBinder onBind(Intent var1) {
        String data = var1.getStringExtra("data");
        LogUtil.e("ServiceImpl", "receive data from client:" + data);
        return binder;
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
}
