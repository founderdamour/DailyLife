package cn.zrc.dailylife.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.app.PassKeys;
import cn.zrc.dailylife.util.Preferences;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;
import cn.zrc.dailylife.view.DragLayout;
import cn.zrc.dailylife.view.InterceptTouchEventLinearLayout;
import cn.zrc.dailylife.view.TabItemView;

public class MainActivity extends BaseActivity implements TabItemView.OnTabItemStateWillChangeDelegate {

    public static final int INDEX_NONE = -1;
    public static final int INDEX_HOME = 0;
    public static final int INDEX_MICRO_SLEEP = 1;

    private static final long TIME_SPACE = 3000;

    private long mBackPressedTime;

    @FindViewById(R.id.drag_layout)
    private DragLayout dragLayout;

    @FindViewById(R.id.lv_left)
    private ListView lvLeft;

    @FindViewById(R.id.ll_main)
    private InterceptTouchEventLinearLayout llMain;

    @FindViewById(R.id.tab_home)
    private TabItemView homeItem;

    @FindViewById(R.id.tab_micro_sleep)
    private TabItemView microSleepItem;

    @FindViewById(R.id.tab_user)
    private TabItemView userItem;

    private List<TabItemView> tabs = new ArrayList<>();

    private String[] leftTitle = new String[]{"账号管理", "联系我们"};

    public static void startActivity(Context aContext) {
        if (aContext == null) {
            return;
        }
        Intent lIntent = new Intent(aContext, MainActivity.class);
        aContext.startActivity(lIntent);
    }

    public static void startActivity(Context aContext, @HomeTabIndexMode int index) {
        if (aContext == null) {
            return;
        }
        Intent lIntent = new Intent(aContext, MainActivity.class);
        lIntent.putExtra(PassKeys.KEY_INDEX, index);
        lIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        aContext.startActivity(lIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsMainActivityOn(true);
        setContentView(R.layout.activity_main);
        initView();
        handleNewIntent(getIntent());
        setAdapter();
        setListener();
    }

    private void setListener() {
        llMain.setDragLayout(dragLayout);
    }

    private void setAdapter() {
        lvLeft.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, leftTitle) {
            @NonNull
            @Override
            public View getView(int position, View convertView,
                                @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                return tv;
            }
        });

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        AccountManagerActivity.startActivity(getContext());
                        break;
                    case 1:
                        break;
                }
            }
        });
    }

    private void initView() {
        ViewInject.inject(this);

        homeItem.setFragmentClass(HomeFragment.class);
        microSleepItem.setFragmentClass(MicroSleepFragment.class);
        userItem.setFragmentClass(UserInfoFragment.class);

        homeItem.setDelegate(this);
        microSleepItem.setDelegate(this);
        userItem.setDelegate(this);

        tabs.add(homeItem);
        tabs.add(microSleepItem);
        tabs.add(userItem);

    }

    @Override
    protected void onStop() {
        super.onStop();
        dragLayout.close();
    }

    @Override
    public void finish() {
        super.finish();
        setIsMainActivityOn(false);
    }

    @Override
    public boolean shouldChangeTabItemState(TabItemView tabItemView) {
        return !tabItemView.isItemSelected();
    }

    @Override
    public void onTabItemStatChanged(TabItemView tabItemView) {
        int index = 0;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i) == tabItemView) {
                index = i;
                break;
            }
        }
        setIndexWithoutException(index);
    }

    private void handleNewIntent(Intent intent) {
        int tabId = intent.getIntExtra(PassKeys.KEY_INDEX, INDEX_HOME);
        changeTabTag(tabId);
    }

    /**
     * 切换底部栏
     *
     * @param index 统一调用这个TABHOSTACT页面的参数 如INDEX_HOME等等
     */
    private void changeTabTag(int index) {
        if (isFinishing()) {
            return;
        }
        if (index != INDEX_NONE) {
            setIndexWithoutException(index);
        }
    }

    @Override
    public void onBackPressed() {
        long ct = SystemClock.uptimeMillis();
        if (ct - mBackPressedTime <= TIME_SPACE) {
            finish();
            Preferences.setBooleanValue(Preferences.IS_MAIN_DESTORY, true);
        } else {
            mBackPressedTime = ct;
            showToast(R.string.exit_application);
        }
    }

    /**
     * 选中某项标签
     */
    private void setIndexWithoutException(@IntRange(from = 0) int index) {
        try {
            TabItemView tab = tabs.get(index);
            tab.setItemSelected(!tab.isItemSelected());
            setIndex(index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIndex(int index) throws Exception {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < tabs.size(); i++) {
            TabItemView tab = tabs.get(i);
            Class<? extends Fragment> fragmentClass = tab.getFragmentClass();
            String tag = fragmentClass.getName();
            Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);

            if (i == index) {
                tab.setItemSelected(true);
                // 添加tab
                if (fragmentByTag == null) {
                    fragmentTransaction.add(R.id.container, fragmentClass.newInstance(), tag);
                } else {
                    if (fragmentByTag.isDetached()) {
                        fragmentTransaction.attach(fragmentByTag);
                    }
                }
            } else if (tab.isItemSelected()) {
                tab.setItemSelected(false);
                // 移除tab
                if (fragmentByTag != null) {
                    fragmentTransaction.detach(fragmentByTag);
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @IntDef({INDEX_NONE, INDEX_HOME, INDEX_MICRO_SLEEP})
    public @interface HomeTabIndexMode {
    }
}
