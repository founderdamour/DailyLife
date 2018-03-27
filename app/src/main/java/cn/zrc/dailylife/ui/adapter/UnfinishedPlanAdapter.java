package cn.zrc.dailylife.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.zrc.dailylife.entity.PlanInfo;

/**
 * Created by yangzhizhong
 */

public class UnfinishedPlanAdapter extends BaseAdapter {

    private Context context;

    private List<PlanInfo> planInfos;

    public UnfinishedPlanAdapter(Context context, List<PlanInfo> planInfos) {
        this.context = context;
        this.planInfos = planInfos;
    }

    @Override
    public int getCount() {
        return planInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return planInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
