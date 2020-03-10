package com.example.my.floatview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.my.floatview.R;
import com.example.my.floatview.util.ScreenUtil;

/**
 * Created by zhhang on 2020/3/5.
 */
public class FloatingMagnetView extends FrameLayout {

    private Context context;
    int screenWidth;
    int screenHeight;
    int viewWidth;
    int viewHeight;
    int statusBarHeight;

    public FloatingMagnetView(@NonNull Context context) {
        this(context, null);
    }

    public FloatingMagnetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMagnetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.float_view_layout, this, true);
        screenWidth = ScreenUtil.getScreenWidth(context);
        screenHeight = ScreenUtil.getScreenHeight(context);
        statusBarHeight = ScreenUtil.getStatusBarHeight(context);
        setLayoutParams(getParams(context));
    }

    int paddingLeft = 30;

    private FrameLayout.LayoutParams getParams(Context context) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START | Gravity.TOP | Gravity.BOTTOM;
        params.setMargins(paddingLeft, 0, 0, 0);
        return params;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
        }
        return true;
    }

    private float startX;
    private float startY;
    private float startRawX;
    private float startRawY;

    private void touchStart(MotionEvent event) {
        startX = getX();
        startY = getY();
        startRawX = event.getRawX();
        startRawY = event.getRawY();
    }

    private void touchMove(MotionEvent event) {
        float moveX = startX + event.getRawX() - startRawX;
        if ((moveX + viewWidth) <= screenWidth && moveX > 0) {
            setX(moveX);
        }
        float moveY = startY + event.getRawY() - startRawY;
        if ((moveY + viewHeight) <= (screenHeight - statusBarHeight) && moveY > 0) {
            setY(moveY);
        }
    }

    private void touchUp(MotionEvent event) {
        float moveX = startX + event.getRawX() - startRawX;
        if (moveX > screenWidth / 2) {
            scrollToEdge(moveX, false);
        } else {
            scrollToEdge(moveX, true);
        }

    }

    /**
     * distance 距离左边距离
     */
    ValueAnimator animator;

    private void scrollToEdge(final float distance, final boolean isLeft) {
        animator = ObjectAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if (isLeft) {
                    setX(distance * (100f - progress) / 100f + paddingLeft);
                } else {
                    setX(distance + (screenWidth - distance - viewWidth) * progress / 100f - paddingLeft);
                }
            }
        });

        animator.setStartDelay(0);
        try {
            if (isLeft) {
                animator.setDuration((long) (distance / ((float) screenWidth) * 700f));
            } else {
                animator.setDuration((long) ((screenWidth - distance - viewWidth) / ((float) screenWidth) * 700f));
            }
        } catch (Exception e) {
            animator.setDuration(0);
        }
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }
}
