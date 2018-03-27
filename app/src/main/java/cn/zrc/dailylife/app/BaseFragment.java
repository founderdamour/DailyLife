package cn.zrc.dailylife.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.zrc.dailylife.util.handler.MessageHandler;
import cn.zrc.dailylife.view.dialog.CustomProgressDialog;

/**
 * Created by yangzhizhong
 */

public class BaseFragment extends Fragment implements MessageHandler.HandlerMessageListener {
    private MessageHandler mHandler;
    private boolean isDestroy = false;


    private boolean mIsDestroy = false;
    private CustomProgressDialog mProgressDialog;
    private Toast mLastToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MessageHandler(this);
        isDestroy = false;
        App.getInstance().injectManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        mHandler.unregistMessages();
    }


    public boolean isDestroy() {
        return isDestroy;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public Context getContext() {
        return getBaseActivity();
    }

    /**
     * 获得父类Handler
     *
     * @return
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 注册全局消息,注意:全局消息ID不能重复,否则会出现问题
     *
     * @param msgWhat
     */
    public void registHandler(int msgWhat) {
        mHandler.registMessage(msgWhat);
    }

    /**
     * 注销全局消息
     *
     * @param msgWhat
     */
    public void unregistHandler(int msgWhat) {
        mHandler.unregistMessage(msgWhat);
    }

    @Override
    public void handleMessage(Message msg) {
    }


    protected View checkView(View lRootView) {
        ViewGroup parent = (ViewGroup) lRootView.getParent();
        if (parent != null) {
            parent.removeView(lRootView);
        }
        return lRootView;
    }


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
            mProgressDialog = progressDialog = CustomProgressDialog.createDialog(getContext());
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
     * 显示Toast
     */
    public void showToast(int a_iResId) {
        if (isDestroy) {
            return;
        }
        showToast(getResources().getString(a_iResId));
    }

    /**
     * 显示Toast
     */
    public void showToast(CharSequence text) {
        cancleToast();
        if (isDestroy) {
            return;
        }
        mLastToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        mLastToast.show();
    }

    public void cancleToast() {
        if (mLastToast != null)
            mLastToast.cancel();
    }
}
