package cn.zrc.dailylife.http;

import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.zrc.dailylife.util.IOUtils;

import static org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;


public class AsyncHttpClient {
    public static Handler handler = new Handler(Looper.getMainLooper());
    private static Executor executorServer = Executors.newCachedThreadPool();

    /**
     * 发起GET请求
     *
     * @param urlString        请求地址
     * @param iResponseHandler 请求回调
     */
    public static void get(final String urlString, final IResponseHandler iResponseHandler) {
        get(urlString, null, iResponseHandler);
    }

    /**
     * 发起get请求
     *
     * @param urlString        请求地址
     * @param params           参数
     * @param iResponseHandler 回调
     */
    public static void get(String urlString, Map<String, String> params, IResponseHandler iResponseHandler) {
        get(urlString, params, null, iResponseHandler);
    }

    /**
     * 发起get请求
     *
     * @param urlString        请求地址
     * @param params           参数
     * @param headers          请求头
     * @param iResponseHandler 请求回调
     */
    public static void get(final String urlString, final Map<String, String> params,
                           final Map<String, String> headers, final IResponseHandler iResponseHandler) {
        executorServer.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    String url;
                    if (params != null && params.size() > 0)
                        url = urlString + "?" + changeParams(params);
                    else
                        url = urlString;
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(15000);
                    httpURLConnection.addRequestProperty("Accept-Encoding", "gzip");
                    if (headers != null) {
                        addRequestProperty(httpURLConnection, headers);
                    }
                    httpURLConnection.connect();

                    processResult(iResponseHandler, httpURLConnection);

                } catch (Exception exception) {
                    onError(iResponseHandler, exception);
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }

        });
    }

    private static void processResult(final IResponseHandler iResponseHandler,
                                      HttpURLConnection httpURLConnection) throws IOException {
        int code = httpURLConnection.getResponseCode();
        InputStream inputStream = getInputStream(httpURLConnection, code);
        byte[] data = IOUtils.readInputStream(inputStream);
        inputStream.close();

        onResult(iResponseHandler, code, data);
    }

    private static void onResult(final IResponseHandler iResponseHandler, final int code,
                                 final byte[] data) {
        handler.post(() -> iResponseHandler.onResult(code, data));
    }

    private static void onError(final IResponseHandler iResponseHandler, final Exception exception) {
        handler.post(() -> iResponseHandler.onError(exception));
    }

    private static InputStream getInputStream(HttpURLConnection httpURLConnection, int code)
            throws IOException {
        InputStream inputStream;
        if (code == HttpURLConnection.HTTP_OK)
            inputStream = httpURLConnection.getInputStream();
        else
            inputStream = httpURLConnection.getErrorStream();

        String contentEncoding = httpURLConnection.getContentEncoding();
        if (contentEncoding != null && contentEncoding.contains("gzip"))
            inputStream = new GZIPInputStream(inputStream);
        return inputStream;
    }

    /**
     * 延时发起请求
     *
     * @param urlString        请求地址
     * @param params           参数
     * @param delay            延时时间
     * @param iResponseHandler 请求回调
     */
    public static void postDelay(final String urlString, final Map<String, String> params, final Map<String, String> headers,
                                 int delay, final IResponseHandler iResponseHandler) {
        handler.postDelayed(() -> post(urlString, params, headers, iResponseHandler), delay);
    }

    public static void post(final String urlString, final Map<String, String> params,
                            final IResponseHandler iResponseHandler) {
        post(urlString, params, null, iResponseHandler);
    }

    /**
     * application/x-www-form-urlencoded 方式 发起POST请求
     *
     * @param urlString        请求地址
     * @param params           参数
     * @param headers          请求头
     * @param iResponseHandler 请求回调
     */
    public static void post(final String urlString, final Map<String, String> params, final Map<String, String> headers,
                            final IResponseHandler iResponseHandler) {
        executorServer.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = getPostConnection(urlString,
                            "application/x-www-form-urlencoded; charset=UTF-8");
                    if (headers != null && headers.size() > 0) {
                        addRequestProperty(httpURLConnection, headers);
                    }
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    if (params != null)
                        outputStream.write(changeParams(params).getBytes("UTF-8"));
                    outputStream.close();

                    processResult(iResponseHandler, httpURLConnection);
                } catch (Exception exception) {
                    onError(iResponseHandler, exception);
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }
        });
    }

    private static void addRequestProperty(HttpURLConnection httpURLConnection, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    public static String changeParams(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!isFirst)
                stringBuilder.append("&");
            else
                isFirst = false;

            try {
                stringBuilder.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 以application/octet-stream方式上传数据 post
     *
     * @param url              上传地址
     *                         数据
     * @param iResponseHandler 回调结果
     */
    public static void upload(final String url, final byte data[],
                              final IResponseHandler iResponseHandler, final OnProgressListener onProgressListener) {
        executorServer.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = getPostConnection(url, "application/octet-stream");
                    httpURLConnection.addRequestProperty("Content-length",
                            String.valueOf(data.length));
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                    IOUtils.copy(inputStream, outputStream, new OnProgressListenerOnUI(
                            onProgressListener, data.length));
                    outputStream.close();
                    inputStream.close();
                    processResult(iResponseHandler, httpURLConnection);
                } catch (Exception exception) {
                    onError(iResponseHandler, exception);
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }
        });
    }

    /**
     * 以application/octet-stream方式上传文件 post
     *
     * @param url              上传地址
     * @param file             文件
     * @param iResponseHandler 回调结果
     */
    public static void uploadFile(final String url, final File file,
                                  final IResponseHandler iResponseHandler, final OnProgressListener onProgressListener) {
        executorServer.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                FileInputStream fileInputStream = null;
                try {
                    httpURLConnection = getPostConnection(url, "application/octet-stream");
                    httpURLConnection.addRequestProperty("Content-length",
                            String.valueOf(file.length()));
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    fileInputStream = new FileInputStream(file);
                    IOUtils.copy(fileInputStream, outputStream, new OnProgressListenerOnUI(
                            onProgressListener, (int) file.length()));
                    outputStream.close();

                    processResult(iResponseHandler, httpURLConnection);
                } catch (Exception exception) {
                    onError(iResponseHandler, exception);
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    IOUtils.close(fileInputStream);
                }
            }
        });
    }

    private static HttpsURLConnection getPostConnection(final String url, String contentType)
            throws Exception {
        HttpsURLConnection httpURLConnection;
        httpURLConnection = (HttpsURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setConnectTimeout(6000);

        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();
        httpURLConnection.setSSLSocketFactory(sslSocketFactory);
        httpURLConnection.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setDefaultUseCaches(false);
        httpURLConnection.addRequestProperty("Content-Type", contentType);
        httpURLConnection.addRequestProperty("Accept-Encoding", "gzip");
        return httpURLConnection;
    }

    private static class OnProgressListenerOnUI implements OnProgressCallback {
        OnProgressListener onProgressListener;
        int length;
        long lastUpdateTime = System.currentTimeMillis();

        OnProgressListenerOnUI(OnProgressListener onProgressListener, int length) {
            super();
            this.onProgressListener = onProgressListener;
            this.length = length;
        }

        @Override
        public void onProgressChange(final int progress) {
            if (System.currentTimeMillis() - lastUpdateTime > 100 || length == progress) {
                handler.post(() -> {
                    if (onProgressListener != null)
                        onProgressListener.onProgressChange(length, progress);
                });
                lastUpdateTime = System.currentTimeMillis();
            }
        }
    }



    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return false;
        }
    };
}
