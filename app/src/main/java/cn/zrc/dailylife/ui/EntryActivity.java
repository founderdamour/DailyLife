package cn.zrc.dailylife.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class EntryActivity extends BaseActivity {

    @FindViewById(R.id.lif_manager)
    private TextView lifeManager;

    private static final int HANDLER_WHAT_START = 2;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == HANDLER_WHAT_START) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                finish();
                return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ViewInject.inject(this);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(lifeManager, "alpha", 0f, 1f);
        objectAnimator1.setDuration(1000);
        objectAnimator1.start();

        handler.sendEmptyMessageDelayed(HANDLER_WHAT_START, 3000);
    }

}
