package cn.zrc.dailylife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.OnClick;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.dialog.ExitAccountDialog;

/**
 * Created by yangzhizhong
 */

public class AccountManagerActivity extends BaseActivity {

    @FindViewById(R.id.exit_account)
    private TextView exitAccount;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AccountManagerActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        ViewInject.inject(this);
    }

    @OnClick(R.id.modify_pwd)
    private void onModifyPwd(View view) {
        showToast("修改密码");
    }

    @OnClick(R.id.contact_us)
    private void onContactUs(View view) {
        showToast("联系我们");
    }

    @OnClick(R.id.exit_account)
    private void onExitAccount(View view) {
        ExitAccountDialog exitAccountDialog = new ExitAccountDialog(getContext(),"退出登录","你确定要退出本次登录吗？","退出","取消");
        exitAccountDialog.setOnSureListener(new ExitAccountDialog.OnSureListener() {
            @Override
            public void onSure() {
                finish();
            }
        });
        exitAccountDialog.show();
    }
}
