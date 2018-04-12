package cn.zrc.dailylife.manager;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zrc.dailylife.app.App;
import cn.zrc.dailylife.app.BaseManager;
import cn.zrc.dailylife.entity.AccountInfo;
import cn.zrc.dailylife.util.json.JsonHelper;

/**
 * Created by yangzhizhong
 */

public class UserManager extends BaseManager {

    private static final String SHARED_PREFERENCES_NAME = "user_info";

    private boolean isOnLogin = false;

    private AccountInfo accountInfo;

    @Override
    public void onManagerCreate(App app) {
        accountInfo = loadUser();
    }


    // 持久化用户信息
    private void saveUserInfo(AccountInfo accountInfo) {

        JSONObject accountJson = JsonHelper.toJSONObject(accountInfo);
        if (accountJson != null) {
            SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
            sharedPreferences.edit().putString("pes_user", accountInfo.toString()).apply();
        } else {
            throw new RuntimeException("用户信息保存错误！！！！");
        }
    }

    /**
     * 加载用户信息
     */
    private AccountInfo loadUser() {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        String userString = sharedPreferences.getString("pes_user", null);
        AccountInfo accountInfo;
        if (userString != null) {
            try {
                accountInfo = JsonHelper.toObject(new JSONObject(userString), AccountInfo.class);
                return accountInfo;
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        return null;
    }

    public String getAccountId() {
        return accountInfo.getAccountId();
    }

}
