package cn.zrc.dailylife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by yangzhizhong
 */

public class ActionBarTextView extends android.support.v7.widget.AppCompatTextView {

    private int defaultColor = HActionBar.defaultTextColor;
    private int pressedColor = HActionBar.prsssTextColor;

    public ActionBarTextView(Context context) {
        super(context);
    }

    public ActionBarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionBarTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDefaultColor(int color) {
        this.defaultColor = color;
    }

    public void setPressedColor(int color) {
        this.pressedColor = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setTextColor(defaultColor);
                break;
            case MotionEvent.ACTION_DOWN:
                setTextColor(pressedColor);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
