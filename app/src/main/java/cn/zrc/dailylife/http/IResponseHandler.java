package cn.zrc.dailylife.http;

/**
 * Created by yangzhizhong
 */

public interface IResponseHandler {

    public void onResult(int code, byte[] data);

    public void onError(Exception exception);
}
