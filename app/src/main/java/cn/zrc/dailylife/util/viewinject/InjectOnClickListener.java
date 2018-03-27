package cn.zrc.dailylife.util.viewinject;

import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by yangzhizhong
 */

public class InjectOnClickListener implements View.OnClickListener {
    private Method method;
    private Object target;

    public InjectOnClickListener(Method method, Object target) {
        super();
        this.method = method;
        this.target = target;
    }

    @Override
    public void onClick(View v) {
        try {
            method.invoke(target, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
