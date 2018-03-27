package cn.zrc.dailylife.http;

/**
 * Created by yangzhizhong
 */

public interface OnHttpResponseListener<Data> extends ResultCode {

    /**
     * 请求完成监听
     *
     * @param code    请求返回结果
     * @param data    请求的数据
     * @param message 错误信息
     */
    void onHttpResponse(int code, Data data, String message);
}
