package robin.sdk.sdk_impl;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public interface ServiceProxy {
    void onCreate(Context var1);

    int onStartCommand(Intent var1, int var2, int var3);

    IBinder onBind(Intent var1);

    boolean onUnBind(Intent var1);

    void onDestroy();

    String getVersion();

    int getVersionCode();
}
