package cn.zrc.dailylife.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.zrc.dailylife.R;
import cn.zrc.dailylife.app.App;
import cn.zrc.dailylife.app.BaseActivity;
import cn.zrc.dailylife.util.AppHelper;
import cn.zrc.dailylife.util.CommonUtils;
import cn.zrc.dailylife.util.viewinject.FindViewById;
import cn.zrc.dailylife.util.viewinject.ViewInject;

/**
 * Created by yangzhizhong
 */

public class HActionBar extends RelativeLayout implements View.OnClickListener {
    /** 左边的文本控件 */
    public static final int FUNCTION_TEXT_LEFT = 1;

    /** 右边的文本控件 */
    public static final int FUNCTION_TEXT_RIGHT = 2;

    /** 中间的文本控件 */
    public static final int FUNCTION_TEXT_TITLE = 4;

    /** 左边的按钮 */
    public static final int FUNCTION_BUTTON_LEFT = 8;

    /** 右边的按钮 */
    public static final int FUNCTION_BUTTON_RIGHT = 16;

    /** 中间的按钮 */
    public static final int FUNCTION_BUTTON_TITLE = 32;

    /** 中间的进度框 */
    public static final int FUNCTION_PROGRESSBAR_TITLE = 64;

    /** 左边第二按钮 */
    public static final int FUNCTION_BUTTON_LEFT_SECOND = 128;

    /** 左边第二文本控件 */
    public static final int FUNCTION_TEXT_LEFT_SECOND = 256;

    /** 默认文本颜色 */
    public static int defaultTextColor = Color.BLACK;
    /** 默认按下文本颜色 */
    public static int prsssTextColor = Color.parseColor("#1186DB");

    @FindViewById(R.id.action_bar_left_text)
    private TextView mLeftTextView;

    @FindViewById(R.id.action_bar_left_2nd_text)
    private TextView mLeft2NdTextView;

    @FindViewById(R.id.action_bar_right_text)
    private TextView mRightTextView;

    @FindViewById(R.id.action_bar_title)
    private TextView mTitleTextView;

    @FindViewById(R.id.action_bar_progress_bar)
    private ProgressBar mTitleProgressBar;

    @FindViewById(R.id.action_bar_left_btn)
    private ImageView mLeftButton;

    @FindViewById(R.id.action_bar_left_2nd_btn)
    private ImageView mLeft2NdButton;

    @FindViewById(R.id.action_bar_right_btn)
    private ImageView mRightButton;

    @FindViewById(R.id.action_bar_title_btn)
    private ImageView mTitleButton;

    @FindViewById(R.id.action_bar_container)
    private RelativeLayout mContainer;

    /** 默认打开的功能 */
    private int currentFunction = 0;

    private OnActionBarClickListener onActionBarClickListener;

    private ListView mListView;
    @FindViewById(R.id.statusBar)
    private MyStatusBar statusBar;

    public HActionBar(Context context) {
        this(context, null, 0);
    }

    public HActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createView(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HActionBar);
        // init
        Drawable background = typedArray.getDrawable(R.styleable.HActionBar_mBackground);
        if (background == null) {
            background = new ColorDrawable(getResources().getColor(R.color.action_bar_bg));
        }
        this.setBackground(background);

        // 设置功能
        int function = typedArray.getInt(R.styleable.HActionBar_function, 0);
        setFunction(function);

        setTitleText(typedArray.getString(R.styleable.HActionBar_text_title));
        setLeftText(typedArray.getString(R.styleable.HActionBar_text_left));
        setLeft2NdText(typedArray.getString(R.styleable.HActionBar_text_2nd_left));
        setRightText(typedArray.getString(R.styleable.HActionBar_text_right));

        setTitleColor(typedArray.getColor(R.styleable.HActionBar_titleTxColor, Color.parseColor("#000000")));
        setRightTextColor(typedArray.getColor(R.styleable.HActionBar_text_right_color, Color.parseColor("#000000")));
        setRightSrc(typedArray.getDrawable(R.styleable.HActionBar_srcRight));
        Drawable srcLeft = typedArray.getDrawable(R.styleable.HActionBar_srcLeft);
        if (srcLeft == null) {
            srcLeft = context.getResources().getDrawable(R.drawable.back_bg_selector);
        }
        setLeftSrc(srcLeft);

        Drawable src2NdLeft = typedArray.getDrawable(R.styleable.HActionBar_src2NdLeft);
        if (src2NdLeft == null) {
            src2NdLeft = context.getResources().getDrawable(R.drawable.ic_actionbar_close);
        }
        setLeft2NdSrc(src2NdLeft);

        setTitleSrc(typedArray.getDrawable(R.styleable.HActionBar_srcTitle));

        boolean isShowStatusBar = typedArray.getBoolean(R.styleable.HActionBar_statusBarEnable, true);
        setStatusBarEnable(isShowStatusBar);

        Drawable statusBarBg = typedArray.getDrawable(R.styleable.HActionBar_statusBarBg);
        if (statusBarBg == null) {
            int color;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = getResources().getColor(R.color.status_bar_bg_light);
            } else {
                color = getResources().getColor(R.color.status_bar_bg);
            }
            statusBarBg = new ColorDrawable(color);
        }
        setStatusBarBackground(statusBarBg);

        typedArray.recycle();
    }

    /**
     * 设置listView，用于点击顶部title时listView滚动到第一项
     * <p>
     * 设置listView后，如果还调用了setOnActionBarClickListerner方法，当事件ID等于
     * {@link HActionBar#FUNCTION_TEXT_TITLE} 时应当返回true,否则不会处理title点击事件。
     * </p>
     *
     * @param listView 点击title后要滑动的ListView
     */
    public void setListView(ListView listView) {
        this.mListView = listView;
    }

    @Override
    public void onClick(View v) {
        int function = 0;
        if (v == mLeftButton) {
            function = FUNCTION_BUTTON_LEFT;
        } else if (v == mLeftTextView) {
            function = FUNCTION_TEXT_LEFT;
        } else if (v == mRightButton) {
            function = FUNCTION_BUTTON_RIGHT;
        } else if (v == mRightTextView) {
            function = FUNCTION_TEXT_RIGHT;
        } else if (v == mTitleButton) {
            function = FUNCTION_BUTTON_TITLE;
        } else if (v == mTitleTextView) {
            function = FUNCTION_TEXT_TITLE;
        } else if (v == mTitleProgressBar) {
            function = FUNCTION_PROGRESSBAR_TITLE;
        } else if (v == mLeft2NdTextView) {
            function = FUNCTION_TEXT_LEFT_SECOND;
        } else if (v == mLeft2NdButton) {
            function = FUNCTION_BUTTON_LEFT_SECOND;
        }

        // 根据控件ID处理点击事件
        if (isAddFunction(function) && function != 0) {
            boolean result = true;
            if (onActionBarClickListener != null) {
                result = onActionBarClickListener.onActionBarClickListener(function);
            }
            if (result) {
                // 处理返回事件
                Context context = getContext();
                if (context instanceof Activity) {
                    switch (function) {
                        case FUNCTION_BUTTON_LEFT:
                            AppHelper.hideSoftPad((Activity) context);
                            ((Activity) context).finish();
                            break;
                        case FUNCTION_TEXT_TITLE:
                            if (mListView != null) {
                                mListView.smoothScrollToPosition(0);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void setStatusBarEnable(boolean isShowStatusBar) {
        statusBar.setVisibility(isShowStatusBar ? VISIBLE : GONE);
    }

    public void setContainerAlpha(float alpha) {
        mContainer.setAlpha(alpha);
    }

    /**
     * 设置StatusBarBackground的背景
     *
     * @param drawable 背景图片
     */
    @SuppressWarnings("deprecation")
    public void setStatusBarBackground(Drawable drawable) {
        statusBar.setBackgroundDrawable(drawable);
    }

    /** 初始化控件 */
    private void createView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_action_bar, this, false);
        this.addView(rootView);
        ViewInject.inject(this, rootView);
        mTitleTextView.setOnClickListener(this);
        mLeftTextView.setOnClickListener(this);
        mLeft2NdTextView.setOnClickListener(this);
        mRightTextView.setOnClickListener(this);
        mTitleButton.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mLeft2NdButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mTitleProgressBar.setOnClickListener(this);
    }

    /**
     * 设置按钮被点击的事件
     *
     * @param onActionBarClickListener
     */
    public void setOnActionBarClickListener(OnActionBarClickListener onActionBarClickListener) {
        this.onActionBarClickListener = onActionBarClickListener;
    }

    /**
     * 开启功能，参数有：</br> {@link HActionBar#FUNCTION_TEXT_LEFT}：左文本</br>
     * {@link HActionBar#FUNCTION_TEXT_RIGHT} ：右文本</br> {@link HActionBar#FUNCTION_TEXT_TITLE}
     * ：中间文本：title</br> {@link HActionBar#FUNCTION_BUTTON_LEFT}：左按钮</br>
     * {@link HActionBar#FUNCTION_BUTTON_RIGHT} ：右按钮</br> {@link HActionBar#FUNCTION_BUTTON_TITLE}
     * ：中间按钮
     *
     * @param function
     */
    public void addFunction(int function) {
        setFunction(this.currentFunction | function);
    }

    /**
     * 关闭功能，和开启功能函数相反
     *
     * @param function
     * @see HActionBar#addFunction(int)
     */
    public void removeFunction(int function) {
        setFunction(this.currentFunction & (~function));
    }

    public void showLeft2NdTextView() {
        mLeft2NdTextView.setVisibility(VISIBLE);
        this.invalidate();
    }

    public void hideLeft2NdTextView() {
        mLeft2NdTextView.setVisibility(GONE);
        this.invalidate();
    }

    public void showLeft2NdButton() {
        mLeft2NdButton.setVisibility(VISIBLE);
        this.invalidate();
    }

    public void hideLeft2NdButton() {
        mLeft2NdButton.setVisibility(GONE);
        this.invalidate();
    }

    public void showRightTextView() {
        mRightTextView.setVisibility(View.VISIBLE);
        this.invalidate();
    }

    public void hideRightTextView() {
        mRightTextView.setVisibility(View.GONE);
        this.invalidate();
    }

    public void showTitleProgressBar() {
        mTitleProgressBar.setVisibility(View.VISIBLE);
        this.invalidate();
    }

    public void hideTitleProgressBar() {
        mTitleProgressBar.setVisibility(View.GONE);
        this.invalidate();
    }

    /**
     * 检查功能是否开启，参数有：</br> {@link HActionBar#FUNCTION_TEXT_LEFT}：左文本</br>
     * {@link HActionBar#FUNCTION_TEXT_RIGHT} ：右文本</br> {@link HActionBar#FUNCTION_TEXT_TITLE}
     * ：中间文本：title</br> {@link HActionBar#FUNCTION_BUTTON_LEFT}：左按钮</br>
     * {@link HActionBar#FUNCTION_BUTTON_RIGHT} ：右按钮</br> {@link HActionBar#FUNCTION_BUTTON_TITLE}
     * ：中间按钮
     *
     * @param function
     */
    public boolean isAddFunction(int function) {
        return (this.currentFunction & function) == function;
    }

    /**
     * 同时开启多项功能，该函数区别于{@link HActionBar#addFunction(int)}
     * <p/>
     * <pre>
     * setFunction({@link HActionBar#FUNCTION_TEXT_LEFT} | {@link HActionBar#FUNCTION_TEXT_RIGHT});
     * </pre>
     *
     * @param function
     */
    public void setFunction(int function) {
        if (this.currentFunction == function) {
            return;
        }
        this.currentFunction = function;

        // 添加title
        mTitleTextView.setVisibility(isAddFunction(FUNCTION_TEXT_TITLE) ? VISIBLE : GONE);

        // 添加leftTextView
        mLeftTextView.setVisibility(isAddFunction(FUNCTION_TEXT_LEFT) ? VISIBLE : GONE);

        // 添加left2NdTextView
        mLeft2NdTextView.setVisibility(isAddFunction(FUNCTION_TEXT_LEFT_SECOND) ? VISIBLE : GONE);

        // 添加rightTextView1
        mRightTextView.setVisibility(isAddFunction(FUNCTION_TEXT_RIGHT) ? VISIBLE : GONE);

        // 添加titleButton
        mTitleButton.setVisibility(isAddFunction(FUNCTION_BUTTON_TITLE) ? VISIBLE : GONE);

        // 添加leftButton
        mLeftButton.setVisibility(isAddFunction(FUNCTION_BUTTON_LEFT) ? VISIBLE : GONE);

        // 添加left2NdButton
        mLeft2NdButton.setVisibility(isAddFunction(FUNCTION_BUTTON_LEFT_SECOND) ? VISIBLE : GONE);

        // 添加RightButton
        mRightButton.setVisibility(isAddFunction(FUNCTION_BUTTON_RIGHT) ? VISIBLE : GONE);

        // 添加顶部进度框
        mTitleProgressBar.setVisibility(isAddFunction(FUNCTION_PROGRESSBAR_TITLE) ? VISIBLE : GONE);
    }

    public void setLeftSrc(Drawable drawable) {
        mLeftButton.setImageDrawable(drawable);
    }

    public void setLeft2NdSrc(Drawable drawable) {
        mLeft2NdButton.setImageDrawable(drawable);
    }

    public void setRightSrc(Drawable drawable) {
        mRightButton.setImageDrawable(drawable);
    }

    public void setTitleSrc(Drawable drawable) {
        mTitleButton.setImageDrawable(drawable);
    }

    /**
     * 设置标题栏背景
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setBackground(Drawable background) {
        super.setBackgroundDrawable(background);
    }

    /**
     * 设置标题栏文本
     */
    public void setTitleText(CharSequence titleString) {
        mTitleTextView.setText(titleString);
    }

    /**
     * 设置标题栏文本
     */
    public void setTitleRightDrawable(Drawable drawable) {
        drawable.setBounds(0, 1, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() + 1);
        mTitleTextView.setCompoundDrawables(null, null, drawable, null);
        mTitleTextView.setCompoundDrawablePadding(BaseActivity.dipToPx(App.getInstance(), 5));
    }

    /**
     * 设置标题栏文本
     *
     * @param resId 资源ID
     */
    public void setTitleText(int resId) {
        setTitleText(getResources().getString(resId));
    }

    /**
     * 设置左文本
     */
    public void setLeftText(String titleString) {
        mLeftTextView.setText(titleString);
    }

    /**
     * 设置左文本
     *
     * @param resId 资源ID
     */
    public void setLeftText(int resId) {
        setLeftText(getResources().getString(resId));
    }

    /**
     * 设置右文本
     */
    public void setRightText(String titleString) {
        mRightTextView.setText(titleString);
    }

    /**
     * 设置左边第二文本
     */
    public void setLeft2NdText(String titleString) {
        mLeft2NdTextView.setText(titleString);
    }

    /**
     * 设置左边第二文本
     *
     * @param resId 资源id
     */
    public void setLeft2NdText(int resId) {
        setLeft2NdText(getResources().getString(resId));
    }

    /**
     * 设置右文本图标
     */
    public void setRightTextIcon(int resId) {
        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        mRightTextView.setCompoundDrawablePadding(CommonUtils.dipToPx(getContext(), 3.0f));
    }

    /**
     * 设置右文本
     *
     * @param resId 资源ID
     */
    public void setRightText(int resId) {
        setRightText(getResources().getString(resId));
    }

    public View getLeftButton() {
        return mLeftButton;
    }

    public View getRightButton() {
        return mRightButton;
    }

    public void setTitleColor(int titleColor) {
        mTitleTextView.setTextColor(titleColor);
    }

    public void setRightTextColor(@ColorInt int color) {
        ((ActionBarTextView) mRightTextView).setDefaultColor(color);
        mRightTextView.setTextColor(color);
    }

    /**
     * ActionBar控件点击监听器
     *
     * @author zengdexing
     */
    public interface OnActionBarClickListener {
        /**
         * 按钮被点击的回调，当返回true时，表示需要处理点击事件，比如左边按钮点击执行finish()、点击title执行listView的滑动到顶部等。
         *
         * @param function 按钮点击事件编号:
         *                 </br> {@link HActionBar#FUNCTION_TEXT_LEFT}：左文本被点击</br>
         *                 {@link HActionBar#FUNCTION_TEXT_RIGHT}：右文本被点击</br>
         *                 {@link HActionBar#FUNCTION_TEXT_TITLE}：中间文本被点击：title</br>
         *                 {@link HActionBar#FUNCTION_BUTTON_LEFT}：左按钮被点击</br>
         *                 {@link HActionBar#FUNCTION_BUTTON_RIGHT}：右按钮被点击</br>
         *                 {@link HActionBar#FUNCTION_BUTTON_TITLE}：中间按钮被点击
         * @return ActionBar控件是否需要接着处理点击事件: <b>true</b>:需要事件; <b>false</b>：不需要再处理事件</br>
         */
        boolean onActionBarClickListener(int function);
    }
}
