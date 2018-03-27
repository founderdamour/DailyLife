package cn.zrc.dailylife.http;

/**
 * Created by yangzhizhong
 */

public interface ResultCode {

    /** 操作成功 */
    int RESULT_CODE_OK = 0;



    /** 无网络 */
    int RESULT_CODE_NET_ERROR = -10000;

    /** 请求超时 */
    int RESULT_CODE_TIME_OUT = -10001;
}
