package cn.zrc.dailylife.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzhizhong
 */

public abstract class SimpleRequester<Data> extends HttpRequester {
    protected OnHttpCodeListener<Data> onHttpResponseListener;

    public SimpleRequester(OnHttpCodeListener<Data> onHttpResponseListener) {
        this.onHttpResponseListener = onHttpResponseListener;
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        onHttpResponseListener.onHttpResponse(ResultCode.RESULT_CODE_NET_ERROR, null, "");
    }

    @Override
    public void onResult(int code, JSONObject content) throws JSONException {
        Data data = null;
        if (code == ResultCode.RESULT_CODE_OK) {
            data = onDumpData(content);
        }
        onHttpResponseListener.onHttpResponse(code, data, content == null ? "" : content.optString("message"));
    }

    protected abstract Data onDumpData(JSONObject jsonObject) throws JSONException;
}