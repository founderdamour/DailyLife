package cn.zrc.dailylife.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.entity.PlanInfo;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.HActionBar;

/**
 * Created by yangzhizhong
 */

public class PlanActivity extends BaseActivity {

    @FindViewById(R.id.top_bar)
    private HActionBar topBar;

    @FindViewById(R.id.plan_list_view)
    private ListView planListView;

    @FindViewById(R.id.add_plan)
    private Button button;

    @FindViewById(R.id.look)
    private Button look;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        ViewInject.inject(this);
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        String[] array = getResources().getStringArray(R.array.unfinished_plan);
        List<PlanInfo> unfinishedPlanInfos = new ArrayList<>();
        for (CharSequence anArray : array) {
            String item = anArray.toString();
            String[] split = item.split("\\|");
            if (item.contains("|") && split.length == 3) {
                String title = split[0];
                String needDay = split[1];
                String day = split[2];
                unfinishedPlanInfos.add(new PlanInfo(title, needDay, day));
            }
        }
    }
}
