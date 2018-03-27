package cn.zrc.dailylife.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.CircleTimerView;
import cn.zrc.dailylife.view.HActionBar;

/**
 * Created by yangzhizhong
 */

public class SleepMusicActivity extends BaseActivity {

    public static final String TITLE = "title";
    public static final String TIME = "time";

    @FindViewById(R.id.top_bar)
    private HActionBar topBar;

    @FindViewById(R.id.timer_time)
    private CircleTimerView timerView;

    @FindViewById(R.id.time)
    private TextView timeTv;

    @FindViewById(R.id.music_one)
    private Button musicOne;

    @FindViewById(R.id.music_two)
    private Button musicTwo;

    @FindViewById(R.id.music_three)
    private Button musicThree;

    private boolean isFirstEntry = true;

    private int leftTime = 360;

    private TimerTask timerTask;
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (leftTime > 0) {
                        leftTime--;
                    } else {
                        timer.cancel();
                        timerTask.cancel();
                    }
                    timeTv.setText(String.valueOf(leftTime) + "秒");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private MediaPlayer mediaPlayer;
    private String topTitle;

    public static void startActivity(Context context, String topTitle, int playTime) {
        Intent intent = new Intent();
        intent.putExtra(TITLE, topTitle);
        intent.putExtra(TIME, playTime);
        intent.setClass(context, SleepMusicActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_music);
        ViewInject.inject(this);

        leftTime = getIntent().getIntExtra(TIME, 0);
        String topTitle = getIntent().getStringExtra(TITLE);
        topBar.setTitleText(topTitle);

        timeTv.setText(String.valueOf(leftTime)+ "秒");
        startPlayMusic(musicOne, R.raw.one);
        startPlayMusic(musicTwo, R.raw.two);
        startPlayMusic(musicThree, R.raw.three);
    }

    private void startPlayMusic(final Button button, final int resId) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstEntry) {
                    if (leftTime == 0) return;
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    };
                    timer.scheduleAtFixedRate(timerTask, 0, 1000);
                    isFirstEntry = false;
                }

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    timer.cancel();
                    isFirstEntry = true;
                    stopVoice();
                    showToast("当前播放音乐<<" + button.getText() + ">>已暂停");
                } else {
                    playVoice(getContext(), resId);
                    showToast("正在播放<<" + button.getText() + ">>音乐");
                }
            }
        });
    }

    public void playVoice(Context context, int resId) {
        try {
            mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止播放声音
    public void stopVoice() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
        }
    }

}
