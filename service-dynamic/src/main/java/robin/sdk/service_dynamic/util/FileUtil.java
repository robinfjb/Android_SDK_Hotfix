package robin.sdk.service_dynamic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public final class FileUtil {
    public static void fileCopy(File src, File tar) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(src);
            output = new FileOutputStream(tar);
            byte[] bt = new byte[1024];
            int realbyte = 0;
            while ((realbyte = input.read(bt)) > 0) {
                output.write(bt, 0, realbyte);
            }
            output.close();
        } catch (Exception ignored) {
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

}
