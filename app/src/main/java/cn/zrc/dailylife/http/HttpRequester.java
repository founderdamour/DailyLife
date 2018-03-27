package cn.zrc.dailylife.http;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangzhizhong
 */

public abstract class HttpRequester {

    private StringResponseHandler handler = new StringResponseHandler() {
        @Override
        public void onResult(int code, String content) {

            try {
                if (code == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonObject = new JSONObject(content);
                    HttpRequester.this.onResult(parseCode(jsonObject), jsonObject);
                } else {
                    onError(new IOException("HTTP CODE NOT 200"));
                }
            } catch (JSONException e) {
                onError(e);
            }
        }

        @Override
        public void onError(Exception exception) {
            HttpRequester.this.onError(exception);
        }
    };

    protected int parseCode(JSONObject jsonObject) {
        return jsonObject.optInt("code");
    }


    @NonNull
    protected abstract String getServerUrl();

    protected Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        onPutParams(params);
        return params;
    }

    public String makeQueryString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i != 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(params.get(key));
        }
        return stringBuilder.toString();
    }

    /**
     * 发起POST请求
     */
    public void doPost(int delay) {
        String url = getServerUrl();
        Map<String, String> params = changeParams(getParams());
        Map<String, String> header = getHeader(params);

        AsyncHttpClient.postDelay(url, params, header, delay, handler);
    }

    protected Map<String, String> getHeader(Map<String, String> params) {
        return null;
    }

    /**
     * 发起GET请求
     */
    public void doGet() {
        String url = getServerUrl();
        Map<String, String> params = changeParams(getParams());
        Map<String, String> header = getHeader(params);

        AsyncHttpClient.get(url, params, header, handler);
    }

    /**
     * 发起POST请求
     */
    public void doPost() {
        doPost(1000);
    }

    protected Map<String, String> changeParams(Map<String, Object> params) {
        Map<String, String> newParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            newParams.put(entry.getKey(), entry.getValue().toString());
        }
        return newParams;
    }

    /**
     * 放入请求参数,data_list中的参数
     *
     * @param params 参数  基本参数已经放入
     */
    protected abstract void onPutParams(Map<String, Object> params);

    /**
     * 请求成功了  服务器已经返回结果
     *
     * @param code    请求成功的HTTP返回吗，，一般code等于200表示请求成功
     * @param content 从服务器获取到的数据
     */
    protected abstract void onResult(int code, JSONObject content) throws JSONException;

    /**
     * 请求失败了
     *
     * @param exception 失败原因
     */
    protected abstract void onError(Exception exception);
}
