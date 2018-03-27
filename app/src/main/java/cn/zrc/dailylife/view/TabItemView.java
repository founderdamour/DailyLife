package cn.zrc.dailylife.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class TabItemView extends FrameLayout {

    /**
     * 图标
     */
    private Drawable selectedDrawable;
    /**
     * 图标 选中
     */
    private Drawable normalDrawable;

    @ColorInt
    private int normalColor;

    @ColorInt
    private int selectedColor;

    private boolean isItemSelected = false;

    private Class<? extends Fragment> fragmentClass;

    @FindViewById(R.id.tab_item_icon)
    private ImageView iconView;
    @FindViewById(R.id.tab_item_text)
    private TextView textView;
    @FindViewById(R.id.unreadCount)
    private TextView unReadCount;
    @FindViewById(R.id.dot)
    private View dot;

    private OnTabItemStateWillChangeDelegate delegate;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClickable(true);

        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_item, this, false);
        addView(view);

        ViewInject.inject(this, view);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabItemView);


        String text = typedArray.getString(R.styleable.TabItemView_itemText);
        setText(text);

        selectedDrawable = typedArray.getDrawable(R.styleable.TabItemView_itemImageSelected);
        normalDrawable = typedArray.getDrawable(R.styleable.TabItemView_itemImage);

        normalColor = typedArray.getColor(R.styleable.TabItemView_itemTextColor, Color.parseColor("#8d8d8d"));
        selectedColor = typedArray.getColor(R.styleable.TabItemView_itemTextColorSelected, Color.parseColor("#11d7aa"));

        isItemSelected = typedArray.getBoolean(R.styleable.TabItemView_isItemSelected, false);

        checkState();

        typedArray.recycle();

        // 状态改变监听
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate == null) {
                    isItemSelected = !isItemSelected;
                    checkState();
                } else {
                    boolean shouldChange = delegate.shouldChangeTabItemState(TabItemView.this);
                    if (shouldChange) {
                        checkState();
                        delegate.onTabItemStatChanged(TabItemView.this);
                    }
                }
            }
        });
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public void setFragmentClass(Class<? extends Fragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);

    }

    public boolean isItemSelected() {
        return isItemSelected;
    }

    public void setItemSelected(boolean isItemSelected) {
        this.isItemSelected = isItemSelected;
        checkState();
    }

    private void checkState() {
        if (isItemSelected) {
            textView.setTextColor(selectedColor);
            iconView.setImageDrawable(selectedDrawable);
        } else {
            textView.setTextColor(normalColor);
            iconView.setImageDrawable(normalDrawable);
        }
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
        checkState();
    }

    public void setSelectedDrawable(Drawable selectedDrawable) {
        this.selectedDrawable = selectedDrawable;
        checkState();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    /**
     * 选中状态将要改变的回调
     * 询问状态是否改变
     *
     * @param onTabItemStateWillChangeDelegate 返回True：将要改变
     */
    public void setDelegate(OnTabItemStateWillChangeDelegate onTabItemStateWillChangeDelegate) {
        this.delegate = onTabItemStateWillChangeDelegate;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        throw new RuntimeException("setSelected 不支持");
    }

    public void setUnReadCount(int count) {
        if (count > 0) {
            unReadCount.setVisibility(VISIBLE);
            if (count > 99) {
                unReadCount.setText("99");
            } else {
                unReadCount.setText(count + "");
            }
        } else {
            unReadCount.setVisibility(GONE);
        }

    }

    public void setDotVisibily(boolean visibly) {
        int visibility = visibly ? View.VISIBLE : View.GONE;
        if (dot.getVisibility() != visibility)
            dot.setVisibility(visibility);
    }

    public interface OnTabItemStateWillChangeDelegate {
        boolean shouldChangeTabItemState(TabItemView tabItemView);

        void onTabItemStatChanged(TabItemView tabItemView);
    }

}
