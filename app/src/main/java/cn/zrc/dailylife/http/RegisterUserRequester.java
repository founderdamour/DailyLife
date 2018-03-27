package cn.zrc.dailylife.http;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by yangzhizhong
 */

public class RegisterUserRequester extends SimpleRequester<JSONObject> {

    private String name;
    private String pwd;
    private boolean isLogin;
    private boolean isModify;

    public RegisterUserRequester(String name, String pwd, OnHttpCodeListener<JSONObject> onHttpResponseListener) {
        super(onHttpResponseListener);
        this.name = name;
        this.pwd = pwd;
        this.isModify = true;
    }

    public RegisterUserRequester(String name, String pwd, boolean isLogin, OnHttpCodeListener<JSONObject> onHttpResponseListener) {
        super(onHttpResponseListener);
        this.name = name;
        this.pwd = pwd;
        this.isLogin = isLogin;
    }

    @Override
    protected JSONObject onDumpData(JSONObject jsonObject) throws JSONException {
        return jsonObject;
    }

    @NonNull
    @Override
    protected String getServerUrl() {
        if (isLogin){
            return "https://www.yekongxingchen.com/api/userLogin/";
        }else {
            if (isModify){
                return "https://www.yekongxingchen.com/api/userUpdatePwd/";
            }else {
                return "https://www.yekongxingchen.com/api/userRegister/";
            }
        }
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("user_name", name);
        params.put("user_pwd", pwd);
    }
}
