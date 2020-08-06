package robin.sdk.testapp;

import android.content.Context;
import android.os.Environment;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import dalvik.system.DexClassLoader;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onDestroy() {
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        String path = Environment.getExternalStorageDirectory().getPath() + "/app.jar";

        File sdcardDir = Environment.getExternalStorageDirectory(); // /storage/emulated/0/
        path = sdcardDir + File.separator + "Android" +
                File.separator + "data" + File.separator + "robin.sdk.testapp/driver/jar" + File.separator + "app.jar";
        String dex = sdcardDir + File.separator + "Android" +
                File.separator + "data" + File.separator + "robin.sdk.testapp/driver/dex/";

        File file = new File(path);
        assertTrue(file.exists());
        try {
            DexClassLoader dexClassLoader = new DexClassLoader(path, dex, null, appContext.getClassLoader());
            Class libclass = dexClassLoader.loadClass("robin.sdk.sdk_impl.ServiceImpl2");
            Object libJar = libclass.newInstance();
        } catch (Throwable throwable) {
            System.out.println(throwable.getLocalizedMessage());
        }
    }
}