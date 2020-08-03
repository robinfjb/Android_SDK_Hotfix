package robin.sdk.service_dynamic.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public final class Md5Util {
    private final static String[] STR_HEX = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String getMd5(File file) {
        FileInputStream fis = null;
        try {
            StringBuilder sb = new StringBuilder();
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();
            for (byte aDigest : digest) {
                int d = aDigest;
                if (d < 0) {
                    d += 256;
                }
                int d1 = d / 16;
                int d2 = d % 16;
                sb.append(STR_HEX[d1]).append(STR_HEX[d2]);
            }
            return sb.toString();
        } catch (Exception ignored) {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
            }
        }
        return "";
    }
}
