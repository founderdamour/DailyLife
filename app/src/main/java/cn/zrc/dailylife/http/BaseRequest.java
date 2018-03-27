package cn.zrc.dailylife.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import cn.zrc.dailylife.util.FileUtils;

/**
 * Created by yangzhizhong
 */

public abstract class BaseRequest<Data> implements Runnable {

    public static Executor executor = Executors.newCachedThreadPool();

    private OnResultListener<Data> onResultListener;

    private String url = "";

    public BaseRequest(OnResultListener<Data> onResultListener) {
        this.onResultListener = onResultListener;
    }

    private static InputStream getInputStream(HttpURLConnection httpURLConnection, int code) throws IOException {
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


    @Override
    public void run() {

        Map<String, String> params = getParams();
        String result = postUrl(url, params);

        JSONObject resultJson = null;
        try {
            int code = resultJson.getInt("code");
            if (!result.equals("")) {
                resultJson = new JSONObject(result);
                onFinish(resultJson);
            }
            onResultListener.onResult(code, (Data) resultJson);
        } catch (JSONException e) {
            e.printStackTrace();
            onResultListener.onResult(-1, null);
        }

    }

    private String changeParams(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!isFirst)
                stringBuilder.append("/");
            else
                isFirst = false;

            try {
                stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }


    public String postUrl(final String urlString, final Map<String, String> params) {
        String result = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(60000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.addRequestProperty("Accept-Encoding", "gzip");
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            if (params != null)
                outputStream.write(changeParams(params).getBytes("UTF-8"));
            outputStream.close();

            int code = httpURLConnection.getResponseCode();
            // 返回成功
            if (code == 200) {
                InputStream inputStream = getInputStream(httpURLConnection, code);
                result = FileUtils.getStringBySream(inputStream);
                return result;
            } else {
                return null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return result;
    }

    public void execute() {
        url = getUrl();
        executor.execute(this);
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params = onGetParams(params);
        return params;
    }

    public abstract Map<String, String> onGetParams(Map<String, String> params);

    public abstract String getUrl();

    public abstract void onFinish(JSONObject jsonObject) throws JSONException;

}
