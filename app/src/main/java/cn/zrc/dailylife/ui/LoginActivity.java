package cn.zrc.dailylife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.entity.SimpleTextWatcher;
import cn.zrc.dailylife.http.OnHttpCodeListener;
import cn.zrc.dailylife.http.RegisterUserRequester;
import cn.zrc.dailylife.util.MD5Utils;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.CheckableImageView;

import static cn.zrc.dailylife.ui.RegisterAndForgetPwdActivity.INTDEX_FORGET_PWD;
import static cn.zrc.dailylife.ui.RegisterAndForgetPwdActivity.INTDEX_REGISTER;

/**
 * Created by yangzhizhong
 */

public class LoginActivity extends BaseActivity {

    @FindViewById(R.id.login_account)
    private EditText loginAccount;

    @FindViewById(R.id.login_account_clear)
    private ImageView loginAccountClear;

    @FindViewById(R.id.login_pwd)
    private EditText loginPwd;

    @FindViewById(R.id.login_pwd_show)
    private CheckableImageView loginPwdShow;

    @FindViewById(R.id.view_pwd_line)
    private View line;

    @FindViewById(R.id.login_pwd_clear)
    private ImageView loginPwdClear;

    @FindViewById(R.id.login_forget_pwd)
    private TextView loginForgetPwd;

    @FindViewById(R.id.login_btn)
    private Button loginBtn;

    @FindViewById(R.id.register)
    private TextView register;

    private boolean[] inputStatus = {false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewInject.inject(this);

        setListener();
    }

    private void setListener() {

        loginAccount.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (loginAccount.getText().toString().length() == 0) {
                    loginAccountClear.setVisibility(View.GONE);
                } else {
                    loginAccountClear.setVisibility(View.VISIBLE);
                }

                if (loginAccount.getText().length() != 0) {
                    inputStatus[1] = true;
                    if (inputStatus[0]) {
                        loginBtn.setEnabled(true);
                    }
                } else {
                    inputStatus[1] = false;
                    loginBtn.setEnabled(false);
                }
            }
        });

        loginPwd.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (loginPwd.getText().length() != 0) {
                    loginPwdClear.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    inputStatus[0] = true;
                    if (inputStatus[1]) {
                        loginBtn.setEnabled(true);
                    }
                } else {
                    loginPwdClear.setVisibility(View.GONE);
                    line.setVisibility(View.GONE);
                    inputStatus[0] = false;
                    loginBtn.setEnabled(false);
                }
            }
        });

        loginAccountClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount.setText("");
            }
        });
        loginPwdClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPwd.setText("");
            }
        });

        loginPwdShow.setOnCheckedChangeListener(new CheckableImageView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {
                if (isChecked) {
                    loginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    loginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                loginPwd.setSelection(loginPwd.getText().length());
            }
        });

        //忘记密码
        loginForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAndForgetPwdActivity.startActivity(getContext(), INTDEX_FORGET_PWD);
            }
        });

        // 登录
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account = loginAccount.getText().toString();
                String pwd = loginPwd.getText().toString();
                showIndeterminateProgressDialog();
                new RegisterUserRequester(account, MD5Utils.MD5(pwd), true, new OnHttpCodeListener<JSONObject>() {
                    @Override
                    public void onHttpResponse(int code, JSONObject jsonObject, String message) {
                        dismissIndeterminateProgressDialog();
                        if (code == RESULT_CODE_OK) {
                            showToast("登录成功");
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            finish();
                        } else {
                            showToast(message);
                        }
                    }
                }).doPost();
            }
        });

        // 立即注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAndForgetPwdActivity.startActivity(getContext(), INTDEX_REGISTER);
            }
        });

    }
}
