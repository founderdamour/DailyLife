package cn.zrc.dailylife.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class ItemView extends LinearLayout {

    @FindViewById(R.id.icon)
    private ImageView icon;
    @FindViewById(R.id.title)
    private TextView title;
    @FindViewById(R.id.content)
    private TextView content;
    @FindViewById(R.id.arrow)
    private ImageView arrow;
    @FindViewById(R.id.cutline)
    private View cutline;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);

        setIcon(typedArray.getDrawable(R.styleable.ItemView_icon));
        setTitle(typedArray.getString(R.styleable.ItemView_title));
        setContent(typedArray.getString(R.styleable.ItemView_content));
        setContentColor(typedArray.getColor(R.styleable.ItemView_contentcolor, getResources().getColor(R.color.textNormal)));
        setArrowVisiable(typedArray.getBoolean(R.styleable.ItemView_arrowvisiable, false));
        setCutlineVisiable(typedArray.getBoolean(R.styleable.ItemView_cutlinevisiable, false));

        typedArray.recycle();

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_item, this, false);
        ViewInject.inject(this, view);
        this.addView(view);
    }

    public void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
    }

    public void setIcon(int id) {
        setIcon(getContext().getResources().getDrawable(id));
    }

    public void setTitle(String string) {
        title.setText(string);
    }

    public void setTitle(int id) {
        title.setText(id);
    }

    public void setContent(String string) {
        content.setText(string);
    }

    public String getContent() {
        return content.getText().toString();
    }

    public void setContent(int id) {
        content.setText(id);
    }

    public void setContentTextSize(int size) {
        content.setTextSize(size);
    }

    public void setContentColor(int id) {
        content.setTextColor(id);
    }

    public void setArrowVisiable(boolean visiable) {
        if (visiable) {
            arrow.setVisibility(VISIBLE);
        } else {
            arrow.setVisibility(INVISIBLE);
        }
    }

    public void setCutlineVisiable(boolean visiable) {
        if (visiable) {
            cutline.setVisibility(VISIBLE);
        } else {
            cutline.setVisibility(INVISIBLE);
        }
    }

}
