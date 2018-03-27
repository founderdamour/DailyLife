package cn.zrc.dailylife.util;

import android.app.ActivityManager;
import android.content.Context;

import cn.zrc.dailylife.app.BaseActivity;

/**
 * Created by yangzhizhong
 */

public class CommonUtils {

    /**
     * 获得进程名字
     */
    public static String getUIPName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

    /**
     * 将dip转换为px
     *
     * @param a_fDipValue
     * @return
     */
    public static int dipToPx(Context a_oContext, float a_fDipValue) {
        return BaseActivity.dipToPx(a_oContext, a_fDipValue);
    }
}
