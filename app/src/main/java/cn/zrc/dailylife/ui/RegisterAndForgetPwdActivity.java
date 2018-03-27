package cn.zrc.dailylife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.entity.SimpleTextWatcher;
import cn.zrc.dailylife.http.OnHttpCodeListener;
import cn.zrc.dailylife.http.RegisterUserRequester;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.HActionBar;
import cn.zrc.dailylife.view.dialog.ExitAccountDialog;

/**
 * Created by yangzhizhong
 */

public class RegisterAndForgetPwdActivity extends BaseActivity {

    public static final String INTDEX = "index";

    public static final int INTDEX_FORGET_PWD = 0;
    public static final int INTDEX_REGISTER = 1;

    public String dialogTitle = "";
    public String dialogContent = "";
    public String dialogSure = "";
    public String dialogCancel = "";

    @FindViewById(R.id.top_bar)
    private HActionBar topBar;

    @FindViewById(R.id.user_account)
    private EditText account;

    @FindViewById(R.id.clear_account)
    private ImageView accountClear;

    @FindViewById(R.id.user_pwd)
    private EditText pwd;

    @FindViewById(R.id.clear_pwd)
    private ImageView pwdClear;

    @FindViewById(R.id.user_pwd_again)
    private EditText pwdAgain;

    @FindViewById(R.id.clear_pwd_again)
    private ImageView pwdClearAgain;
    private int index;

    public static void startActivity(Context context, int index) {
        Intent intent = new Intent();
        intent.putExtra(INTDEX, index);
        intent.setClass(context, RegisterAndForgetPwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ViewInject.inject(this);
        initView();
        setListener();
    }

    private void initView() {

        index = getIntent().getIntExtra(INTDEX, 0);
        if (index == INTDEX_REGISTER) {
            topBar.setTitleText("注册账号");
            topBar.setRightText("注册");
            dialogTitle = "注册账号";
            dialogContent = "注册账号是:";
            dialogSure = "注册";
            dialogCancel = "取消";
        } else {
            topBar.setTitleText("重置密码");
            topBar.setRightText("下一步");
            dialogTitle = "重置密码";
            dialogContent = "你确定要重置密码吗？";
            dialogSure = "重置";
            dialogCancel = "取消";
        }
    }

    private void setListener() {

        account.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (account.getText().length() != 0) {
                    accountClear.setVisibility(View.VISIBLE);
                } else {
                    accountClear.setVisibility(View.GONE);
                }
            }
        });

        accountClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account.setText("");
            }
        });

        pwd.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (pwd.getText().length() != 0) {
                    pwdClear.setVisibility(View.VISIBLE);
                } else {
                    pwdClear.setVisibility(View.GONE);
                }
            }
        });

        pwdClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd.setText("");
            }
        });

        pwdAgain.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (pwdAgain.getText().length() != 0) {
                    pwdClearAgain.setVisibility(View.VISIBLE);
                } else {
                    pwdClearAgain.setVisibility(View.GONE);
                }
            }
        });

        pwdClearAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdAgain.setText("");
            }
        });

        topBar.setOnActionBarClickListener(new HActionBar.OnActionBarClickListener() {
            @Override
            public boolean onActionBarClickListener(int function) {
                if (function == HActionBar.FUNCTION_TEXT_RIGHT) {
                    final String acc = account.getText().toString();
                    final String p = pwd.getText().toString();
                    String pa = pwdAgain.getText().toString();
                    if (TextUtils.isEmpty(acc)) {
                        showToast("请输入账号");
                        return false;
                    }
                    if (TextUtils.isEmpty(p) || TextUtils.isEmpty(pa)) {
                        showToast("请输入密码");
                        return false;
                    }
                    if (!p.equals(pa)) {
                        showToast("两次密码不一致");
                        return false;
                    }

                    if (index == INTDEX_REGISTER) {
                        dialogContent += acc;
                    }

                    final ExitAccountDialog exitAccountDialog = new ExitAccountDialog(getContext(), dialogTitle, dialogContent, dialogSure, dialogCancel);
                    exitAccountDialog.setOnSureListener(new ExitAccountDialog.OnSureListener() {
                        @Override
                        public void onSure() {
                            showIndeterminateProgressDialog();
                            if (index == INTDEX_REGISTER){
                                dialogContent = "注册账号是:";
                                new RegisterUserRequester(acc, p,false, new OnHttpCodeListener<JSONObject>() {
                                    @Override
                                    public void onHttpResponse(int code, JSONObject jsonObject, String message) {
                                        dismissIndeterminateProgressDialog();
                                        if (code == RESULT_CODE_OK) {
                                            showToast("注册成功");
                                            startActivity(new Intent(getContext(), LoginActivity.class));
                                            finish();
                                        } else {
                                            showToast(message);
                                        }
                                    }
                                }).doPost();
                            }else {
                                new RegisterUserRequester(acc, p, new OnHttpCodeListener<JSONObject>() {
                                    @Override
                                    public void onHttpResponse(int code, JSONObject jsonObject, String message) {
                                        dismissIndeterminateProgressDialog();
                                        if (code == RESULT_CODE_OK) {
                                            showToast("修改成功");
                                            startActivity(new Intent(getContext(), LoginActivity.class));
                                            finish();
                                        } else {
                                            showToast(message);
                                        }
                                    }
                                }).doPost();
                            }
                        }
                    });
                    exitAccountDialog.show();
                    return true;
                } else if (function == HActionBar.FUNCTION_BUTTON_LEFT) {
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

    }
}
