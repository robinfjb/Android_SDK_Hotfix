package robin.sdk.service_dynamic.net;

import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import robin.sdk.sdk_common.util.LogUtil;

public final class HttpUrlTask extends AsyncTask<Void, Void, String> {
    public interface TaskListener {
        /**
         * call back for ilptask
         * @param statusCode
         * @param statusMessage
         * @param response
         */
        void onTaskComplete(int statusCode, String statusMessage, String response);
    }

    private static final String HTTP_TAG = "HTTP";
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUN_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 10000;
    private static final int QUEUE_SIZE = 5;
    private String url = "";
    private TaskListener taskListener = null;
    private int statusCode = 1009;
    private String statusMessage = "";
    private String agent;
    private String request;
    private boolean isGzip = true;
    private int timeOut;
    private String method;
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final ThreadPoolExecutor TPE = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUN_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),
            new ThreadPoolExecutor.DiscardPolicy());

    /**
     * @param url         server url
     * @param taskHandler the task handler
     */
    public HttpUrlTask(int timeOut, String agent, String method, String url, String request, boolean isGZIP, TaskListener taskHandler) {
        this.url = url;
        this.agent = agent;
        this.taskListener = taskHandler;
        this.request = request == null ? "" : request;
        this.isGzip = isGZIP;
        this.timeOut = timeOut;
        this.method = method;
    }

    @Override
    protected String doInBackground(Void... voids) {
        OutputStream os = null;
        try {
            LogUtil.e(HTTP_TAG, "[请求]url: " + url);
            LogUtil.e(HTTP_TAG, "[请求]request:" + request);
            URL httpUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            if (method.equalsIgnoreCase("POST")) {
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
            }
            timeOut = (timeOut <= 0 || timeOut >= 30 * 1000) ? DEFAULT_TIMEOUT : timeOut;
            httpURLConnection.setReadTimeout(timeOut);
            httpURLConnection.setConnectTimeout(timeOut);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod(method);
            if (!TextUtils.isEmpty(agent)) {
                httpURLConnection.setRequestProperty("User-Agent", agent);
            }
            if (isGzip) {
                httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            if (method.equalsIgnoreCase("POST")) {
                os = httpURLConnection.getOutputStream();
                byte[] bytes = request.getBytes(Charset.defaultCharset());
                if (isGzip) {
                    compress(bytes, os);
                } else {
                    os.write(bytes);
                    os.flush();
                }
            }

            statusCode = httpURLConnection.getResponseCode();
            statusMessage = httpURLConnection.getResponseMessage();
            LogUtil.e(HTTP_TAG, "[请求]statusCode:" + statusCode + " statusMessage:" + statusMessage);
            return parseResponse(httpURLConnection.getInputStream());
        } catch (Throwable e) {
            statusMessage = e.getMessage();
            LogUtil.e(HTTP_TAG, e.toString());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignored) {
                LogUtil.e(HTTP_TAG, ignored.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        LogUtil.e(HTTP_TAG,"result:" + result);
        if (taskListener != null) {
            taskListener.onTaskComplete(statusCode, statusMessage, result);
        } else {
            LogUtil.e(HTTP_TAG, "taskListener is null");
        }
    }

    private static void compress(byte[] bytes, OutputStream os) throws IOException {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            GZIPOutputStream gos = new GZIPOutputStream(os);
            int count;
            byte data[] = new byte[1024];
            while ((count = is.read(data)) != -1) {
                gos.write(data, 0, count);
            }
            gos.flush();
            gos.close();
            is.close();
        } catch (Throwable e) {
        }
    }

    /**
     * @param inputStream the input stream
     * @return parsed response
     */
    private String parseResponse(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                LogUtil.e(HTTP_TAG, "[请求]results：" + stringBuilder.toString());
                return stringBuilder.toString();
            } catch (Exception ignored) {
            }

        }
        return null;
    }

    public static boolean isHttpOK(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public static boolean isHttpClientError(int statusCode) {
        return statusCode >= 400 && statusCode < 500;
    }

    public void executeWithThreadPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.executeOnExecutor(TPE);
        } else {
            this.execute();
        }
    }
}
