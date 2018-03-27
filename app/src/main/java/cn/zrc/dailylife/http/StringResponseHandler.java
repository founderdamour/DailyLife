package cn.zrc.dailylife.http;

import java.io.UnsupportedEncodingException;

/**
 * Created by yangzhizhong
 */

public abstract class StringResponseHandler implements IResponseHandler {

    @Override
    public final void onResult(int code, byte[] data) {
        try {
            String string = new String(data, "UTF-8");
            onResult(code, string);
        } catch (UnsupportedEncodingException e) {
            onError(e);
        }
    }

    public abstract void onResult(int code, String content);
}
