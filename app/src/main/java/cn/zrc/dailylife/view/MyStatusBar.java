package cn.zrc.dailylife.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import java.lang.reflect.Field;

import cn.zrc.dailylife.app.BaseActivity;

/**
 * Created by yangzhizhong
 */

public class MyStatusBar extends android.support.v7.widget.AppCompatImageView {

    public MyStatusBar(Context context) {
        this(context, null);
    }

    public MyStatusBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyStatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 返回状态栏高度
     *
     * @return 像素
     */
    public int getStatusBarHeight(Context context) {
        if (isNeedShowStatusBar()) {
            try {
                Class<?> aClass = Class.forName("com.android.internal.R$dimen");
                Object obj = aClass.newInstance();
                Field field = aClass.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                int y = context.getResources().getDimensionPixelSize(x);
                return y;
            } catch (Exception e) {
                e.printStackTrace();
                return BaseActivity.dipToPx(25);
            }
        } else {
            return 0;
        }
    }

    /**
     * 是否需要显示StatusBar
     *
     * @return true 显示StatusBar
     */
    private boolean isNeedShowStatusBar() {
        return isInEditMode() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }

    private int getStatusBarHeight() {
        return getStatusBarHeight(getContext());
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return getStatusBarHeight();
    }
}
