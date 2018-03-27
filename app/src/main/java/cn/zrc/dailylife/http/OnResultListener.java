package cn.zrc.dailylife.http;

/**
 * Created by yangzhizhong
 */

public interface OnResultListener<Data> {

    /**
     * 请求成功
     */
    int RESULT_SUCCESS = 0;

    /**
     * 请求失败
     */
    int RESULT_FAILED = -1;

    /**
     * 请求结果回调
     *
     * @param result 请求结果
     * @param data   结果
     */
    void onResult(int result, Data data);
}
