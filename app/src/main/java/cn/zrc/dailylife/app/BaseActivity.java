package cn.zrc.dailylife.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import cn.zrc.dailylife.view.dialog.CustomProgressDialog;

/**
 * Created by yangzhizhong
 */

public abstract class BaseActivity extends FragmentActivity {

    private boolean mIsDestroy = false;
    private boolean mIsFinish = false;
    private static boolean isMainActivityOn = false;

    private CustomProgressDialog mProgressDialog;
    private Toast mLastToast;

    /**
     * DIP 转 PX
     */
    public static int dipToPx(float dip) {
        return dipToPx(App.getInstance(), dip);
    }

    public static int dipToPx(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /** 关闭界面 */
    public void onBackClick(View view) {
        finish();
    }

    public final Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsDestroy = false;
        mIsFinish = false;

        App.getInstance().injectManager(this);
    }

    @Override
    public void finish() {
        super.finish();
        mIsFinish = true;
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
    }


    /**
     * 主界面是否正在运行
     */
    public static boolean isMainActivityOn() {
        return isMainActivityOn;
    }

    public static void setIsMainActivityOn(boolean isMainActivityOn) {
        BaseActivity.isMainActivityOn = isMainActivityOn;
    }

    /**
     * 显示小菊花对话框
     */
    public void showIndeterminateProgressDialog() {
        showIndeterminateProgressDialog(false, CustomProgressDialog.STYLE_ONE);
    }

    public void showIndeterminateProgressDialog(int style) {
        showIndeterminateProgressDialog(false, style);
    }

    public void showIndeterminateProgressDialog(boolean cancelable) {
        showIndeterminateProgressDialog(cancelable, CustomProgressDialog.STYLE_ONE);
    }

    /**
     * 显示小菊花对话框
     *
     * @param cancelable 指定是否可以被取消
     */
    public void showIndeterminateProgressDialog(boolean cancelable, int style) {
        if (mIsDestroy) {
            return;
        }

        CustomProgressDialog progressDialog = mProgressDialog;
        if (progressDialog == null) {
            mProgressDialog = progressDialog = CustomProgressDialog.createDialog(this);
        }

        progressDialog.setCancelable(cancelable);
        progressDialog.setStyle(style);

        if (!progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 关闭小菊花对话框
     */
    public void dismissIndeterminateProgressDialog() {
        CustomProgressDialog progressDialog = mProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 判断该Activity是否被销毁，因为onDestroyed()回调方法不一定能及时执行，所以建议使用{@link #isActivityFinished()}方法
     */
    public boolean isActivityDestroyed() {
        return mIsDestroy;
    }

    /**
     * 判断该Activity是否关闭
     */
    public boolean isActivityFinished() {
        return mIsFinish;
    }

    /**
     * 程序的上下文
     */
    public App getHApplication() {
        return (App) getApplication();
    }

    /**
     * 显示Toast
     */
    public void showToast(int a_iResId) {
        showToast(getResources().getString(a_iResId));
    }

    /**
     * 显示Toast
     */
    public void showToast(CharSequence text) {
        cancelToast();
        mLastToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        mLastToast.show();
    }

    public void cancelToast() {
        if (mLastToast != null)
            mLastToast.cancel();
    }

    /**
     * 启动Activity
     *
     * @param cls 需要启动的界面
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    /**
     * 获取当前Activity类名（不包含包名）
     *
     * @return 获取当前Activity类名
     */
    protected String getClassName() {
        return getClass().getSimpleName();
    }

    /**
     * 获取BaseActivity上下文
     *
     * @return BaseActivity上下文
     */
    public BaseActivity getActivity() {
        return this;
    }
}
