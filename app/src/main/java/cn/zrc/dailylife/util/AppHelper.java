package cn.zrc.dailylife.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.inputmethod.InputMethodManager;

public class AppHelper {
    /**
     * 获取渠道号
     *
     * @param context 上下文
     * @return 渠道号
     */
    public static int getChannelCode(Context context) {
        String code = getMetaData(context, "channel");
        if (code == null) {
            return -1;
        } else {
            int channel = -1;
            try {
                channel = Integer.valueOf(code);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return channel;
        }

    }

    /**
     * 获取META_DATA值
     *
     * @param context 上下文
     * @param key     键
     * @return 当存在key时返回数值否则返回null
     */
    private static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     * @return
     */
    public static final boolean hideSoftPad(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            return ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }
}
