package cn.zrc.dailylife.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 特点：根据DragLayout的状态来判断是否将事件传递给子View
 * <p>
 * Created by yangzhizhong
 */

public class InterceptTouchEventLinearLayout extends LinearLayout {

    public InterceptTouchEventLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptTouchEventLinearLayout(Context context) {
        super(context);
    }

    private DragLayout dragLayout;

    public void setDragLayout(DragLayout draglayout) {
        this.dragLayout = draglayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 根据DragLayout的状态判断是否返回true
        if (dragLayout != null && dragLayout.mCurrentState == DragLayout.DragState.OPEN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (dragLayout != null && dragLayout.mCurrentState == DragLayout.DragState.OPEN
                && action == MotionEvent.ACTION_UP) {
            dragLayout.close();
        }
        return true;
    }

}
