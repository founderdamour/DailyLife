package cn.zrc.dailylife.http;

import android.widget.Toast;

import cn.zrc.dailylife.app.App;

/**
 * Created by yangzhizhong
 */

public class OnHttpCodeListener<Data> implements OnHttpResponseListener<Data> {

    @Override
    public void onHttpResponse(int code, Data data, String message) {
        if (code == RESULT_CODE_TIME_OUT) {
            Toast.makeText(App.getInstance(), "当前网络不顺畅，请检查网络", Toast.LENGTH_SHORT).show();
        } else if (code == RESULT_CODE_NET_ERROR) {
            Toast.makeText(App.getInstance(), "当前没有网络，请检查网络是否开启", Toast.LENGTH_SHORT).show();
        } else if (code != RESULT_CODE_OK) {
            onServerCode(code, data, message);
        }

    }

    public void onServerCode(int code, Data data, String message) {
        Toast.makeText(App.getInstance(), "服务器异常，请重试", Toast.LENGTH_SHORT).show();
    }
}