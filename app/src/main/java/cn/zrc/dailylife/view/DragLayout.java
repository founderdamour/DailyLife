package cn.zrc.dailylife.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

import cn.zrc.dailylife.util.EvaluateUtil;

/**
 * Created by yangzhizhong
 */

public class DragLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;

    private ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

        //判断child是否能够被移动  true代表child可以被移动  false代表child不能被移动
        //child：被触摸到的子View
        //pointerId:多点触控
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == viewLeft) {
                return true;
            } else if (child == viewMain) {
                return true;
            }
            return true;
        }

        //代表child即将被移动到的水平位置
        //left:系统推荐给你即将需要移动到的水平位置
        //dx:移动之后和现有位置的偏移量
        //此时View没有移动
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == viewMain) {
                left = fixLeft(left);
            }


            return left;
        }

        ;

        //代表child即将被移动到的竖直位置
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        ;

        //获取水平方向上可以移动的范围，这个方法不重要，但是必须得有
        public int getViewHorizontalDragRange(View child) {
            return mDragRange;
        }

        ;


        //移动之后会进行回调
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == viewLeft) {
                //强行的把ViewLeft固定在了原点
                viewLeft.layout(0, 0, widthSize, heightSize);
                //将dx作用到主面板之上
                int currentMainLeft = viewMain.getLeft();
                currentMainLeft = currentMainLeft + dx;
                currentMainLeft = fixLeft(currentMainLeft);
                viewMain.layout(currentMainLeft, 0, currentMainLeft + widthSize, heightSize);
            }
            animateView();
            updateState();
            invalidate();


        }

        ;

        //当view被释放的时候会回调此方法
        //xvel:释放的时候，x轴方向上的速度 向右为正  向左为负
        //yvel:释放的时候，y轴方向上的速度
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (xvel > 100) {
                //打开
                open();
            } else if (xvel < -100) {
                //关闭
                close();
            } else {
                int currentMainLeft = viewMain.getLeft();
                if (currentMainLeft > mDragRange / 2) {
                    //打开
                    open();
                } else {
                    //关闭
                    close();
                }
            }

        }

        ;


    };

    //smoothSlideViewTo的使用步骤
    //1、调用smoothSlideViewTo方法
    //2、调用invalidate
    //3、重写computeScroll方法
    //4、调用mDragHelper.continueSettling，如果此方法返回的是true，则invalidate
    @SuppressLint("NewApi")
    public void open() {
        //viewMain.layout(mDragRange, 0, mDragRange+widthSize, heightSize);
//		int currentMainLeft = viewMain.getLeft();
//		ValueAnimator animator = ValueAnimator.ofInt(currentMainLeft,mDragRange);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				int tempValue = (Integer) animation.getAnimatedValue();
//				viewMain.layout(tempValue, 0, tempValue+widthSize, heightSize);
//			}
//		});
//		animator.setDuration(2000);
//		animator.start();

        mDragHelper.smoothSlideViewTo(viewMain, mDragRange, 0);//只是启动动画的第一帧
        invalidate();

    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @SuppressLint("NewApi")
    public void close() {
        //viewMain.layout(0, 0, widthSize, heightSize);
//		int currentMainLeft = viewMain.getLeft();
//		ValueAnimator animator = ValueAnimator.ofInt(currentMainLeft,0);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				int tempValue = (Integer) animation.getAnimatedValue();
//				viewMain.layout(tempValue, 0, tempValue+widthSize, heightSize);
//				animateView();
//			}
//		});
//		animator.setDuration(2000);
//		animator.start();

        mDragHelper.smoothSlideViewTo(viewMain, 0, 0);
        invalidate();

    }

    private int fixLeft(int left) {
        if (left < 0) {
            left = 0;
        } else if (left > mDragRange) {
            left = mDragRange;
        }
        return left;
    }

    private View viewLeft;

    private View viewMain;

    private int widthSize;

    private int mDragRange;

    private int heightSize;

    //构造方法的串联
    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context) {
        this(context, null);
    }

    private void init() {
        //1、forParent：所需要监控的父控件
        //2、cb：提供信息、接收事件
        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    //1、事件分发
    //2、事件拦截
    //true : 代表DragLayout拦截事件  false：不拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    //3、事件响应
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    //当控件全部加载结束之后会调用此方法
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount < 2) {
            throw new RuntimeException("DragLayout must have at least 2 child Views");
        }
        viewLeft = getChildAt(0);//findViewById(R.id.llleft);
        viewMain = getChildAt(1);//findViewById(R.id.llmain);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getMeasuredWidth();
    }

    //当DragLayout大小发生变化的时候会调用此方法
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        widthSize = getMeasuredWidth();
        heightSize = getMeasuredHeight();

        mDragRange = (int) (widthSize * 0.6f);


    }

    @SuppressLint("NewApi")
    private void animateView() {
        //实现伴随动画
        int currentMainLeft = viewMain.getLeft();
        float percent = currentMainLeft * 1.0f / mDragRange;

        //1.0~0.8
        //viewMain.setScaleX(1.0f+(0.8f-1.0f)*percent);
        //viewMain.setScaleY(1.0f+(0.8f-1.0f)*percent);

        ViewHelper.setScaleX(viewMain, 1.0f + (0.8f - 1.0f) * percent);
        ViewHelper.setScaleY(viewMain, 1.0f + (0.8f - 1.0f) * percent);

        //0.5~1
        viewLeft.setScaleX(0.5f + (1.0f - 0.5f) * percent);
        viewLeft.setScaleY(0.5f + (1.0f - 0.5f) * percent);


        //左面板移动变化范围 -widthSize/2 ~ 0
        viewLeft.setTranslationX(-widthSize / 2 + (0 - (-widthSize / 2)) * percent);
        //左面板的透明度发生了变化  0~1
        viewLeft.setAlpha(0 + (1) * percent);

        viewMain.setAlpha(1 - 0.4f * percent);

        //背景的亮度发生了变化 让background的亮度发生变化
        Drawable background = getBackground();
        //color的变化范围时black-transparent percent
        background.setColorFilter((Integer) EvaluateUtil.evaluateArgb(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    //1、定义观察者
    public interface DragListener {
        public void onOpen();

        public void onClose();

        public void onDraging(float percent);
    }

    //2、保存观察者
    private DragListener listener;

    public void setDragListener(DragListener listener) {
        this.listener = listener;
    }

    //通知观察者
    private void notifyOpen() {
        if (listener != null) {
            listener.onOpen();
        }
    }

    private void notifyClose() {
        if (listener != null) {
            listener.onClose();
        }
    }

    private void notifyDraging(float percent) {
        if (listener != null) {
            listener.onDraging(percent);
        }
    }

    public enum DragState {
        OPEN, CLOSE, DRAGING;
    }

    public DragState mCurrentState;


    public void updateState() {
        int currentMainLeft = viewMain.getLeft();
        float percent = currentMainLeft * 1.0f / mDragRange;
        if (currentMainLeft == 0) {
            //关闭的状态
            mCurrentState = DragState.CLOSE;
            notifyClose();
        } else if (currentMainLeft == mDragRange) {
            //打开的状态
            mCurrentState = DragState.OPEN;
            notifyOpen();
        } else {
            //拖动的状态
            mCurrentState = DragState.DRAGING;
            notifyDraging(percent);
        }

    }
}
