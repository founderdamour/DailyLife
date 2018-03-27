package cn.zrc.dailylife.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseFragment;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class MicroSleepFragment extends BaseFragment {

    @FindViewById(R.id.sleep_one)
    private LinearLayout sleepOne;

    @FindViewById(R.id.sleep_two)
    private LinearLayout sleepTwo;

    @FindViewById(R.id.sleep_three)
    private LinearLayout sleepThree;

    @FindViewById(R.id.sleep_four)
    private LinearLayout sleepFour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_micro_sleep, container, false);
        ViewInject.inject(this, rootView);
        setListener();
        return rootView;
    }

    private void setListener() {
        sleepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepMusicActivity.startActivity(getContext(), "科学小盹", 60 * 10);
            }
        });

        sleepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepMusicActivity.startActivity(getContext(), "高效午睡", 60 * 24);
            }
        });

        sleepThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepMusicActivity.startActivity(getContext(), "差旅模式", 60 * 40);
            }
        });

        sleepFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepMusicActivity.startActivity(getContext(), "完整午休", 60 * 90);
            }
        });
    }
}
