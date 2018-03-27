package cn.zrc.dailylife.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.OnClick;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class ExitAccountDialog extends Dialog {

    private String title;

    private String content;

    private String sure;

    private String cancel;

    @FindViewById(R.id.title)
    private TextView dialogTitle;

    @FindViewById(R.id.content)
    private TextView dialogContent;

    @FindViewById(R.id.yes)
    private TextView dialogSure;

    @FindViewById(R.id.no)
    private TextView dialogCancel;


    private OnSureListener onSureListener;

    public OnSureListener getOnSureListener() {
        return onSureListener;
    }

    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public ExitAccountDialog(Context context, String title, String content, String sure, String cancel) {
        super(context, R.style.Translucent);
        this.title = title;
        this.content = content;
        this.sure = sure;
        this.cancel = cancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit_account_dialog);
        ViewInject.inject(this);
        initView();
    }

    private void initView() {
        dialogTitle.setText(title);
        dialogContent.setText(content);
        dialogSure.setText(sure);
        dialogCancel.setText(cancel);
    }

    @OnClick(R.id.outside_view)
    private void outsideViewClick(View view) {
        this.dismiss();
    }

    @OnClick(R.id.yes)
    private void onExit(View view) {
        if (onSureListener != null) {
            this.dismiss();
            onSureListener.onSure();
        }
    }

    @OnClick(R.id.no)
    private void onCancel(View view) {
        this.dismiss();
    }

    @OnClick(R.id.ll)
    private void onLinearLayout(View view) {
    }

    public interface OnSureListener {
        void onSure();
    }

}
