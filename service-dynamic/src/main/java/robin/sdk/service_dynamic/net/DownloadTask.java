package robin.sdk.service_dynamic.net;

import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import robin.sdk.sdk_impl.util.LogUtil;

public final class DownloadTask extends AsyncTask<Void, Void, String> {
    public interface DownloadListener {
        void onSuccess();

        void onFail(String msg, int code);
    }

    private static final String DOWNLOAD_TAG = "DOWNLOAD";
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUN_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 10000;
    private static final int QUEUE_SIZE = 5;
    private String url = "";
    private int statusCode = 1009;
    private String statusMessage = "";
    private String fileAbsolutePath;
    private String request;
    private DownloadListener listener;
    private static final ThreadPoolExecutor TPE = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUN_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),
            new ThreadPoolExecutor.DiscardPolicy());

    /**
     * @param url server url
     */
    public DownloadTask(String url, String fileAbsolutePath, DownloadListener listener) {
        LogUtil.e(DOWNLOAD_TAG, "Dyjar url:" + url);
        this.url = url;
        this.request = request == null ? "" : request;
        this.fileAbsolutePath = fileAbsolutePath;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        LogUtil.e(DOWNLOAD_TAG, "start download Dyjar...");
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            is = connection.getInputStream();
            File file = new File(fileAbsolutePath);
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            listener.onSuccess();
            statusCode = connection.getResponseCode();
            statusMessage = connection.getResponseMessage();
            LogUtil.e(DOWNLOAD_TAG, "finish download Dyjar statusCode:" + statusCode + " " + statusMessage);
        } catch (Throwable e) {
            listener.onFail(e.getLocalizedMessage(), statusCode);
            LogUtil.e(DOWNLOAD_TAG, "download Dyjar error:" + statusCode + " " + e.getLocalizedMessage());
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    public void executeWithThreadPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.executeOnExecutor(TPE);
        } else {
            this.execute();
        }
    }
}
