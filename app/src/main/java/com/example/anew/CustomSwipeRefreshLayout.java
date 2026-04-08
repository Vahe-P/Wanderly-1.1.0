package com.example.anew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Custom {@link SwipeRefreshLayout} that only triggers refresh when the user performs
 * a vertical swipe from the top of the associated scroll view. Prevents false-positive
 * refresh triggers during horizontal scrolling or mid-content vertical scrolling.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private float startX;
    private float startY;
    private final int touchSlop;
    private View scrollView;
    private boolean isDragging;
    private float initialMotionY;
    private boolean isRefreshing;

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setAssociatedScrollView(View scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isRefreshing) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                initialMotionY = event.getY();
                isDragging = false;
                return super.onInterceptTouchEvent(event);

            case MotionEvent.ACTION_MOVE:
                if (!isDragging) {
                    float endX = event.getX();
                    float endY = event.getY();
                    float diffX = Math.abs(endX - startX);
                    float diffY = Math.abs(endY - startY);

                    // Only start dragging if vertical movement exceeds touch slop
                    // and is greater than horizontal movement
                    if (diffY > touchSlop && diffY > diffX) {
                        isDragging = true;
                        // Only allow refresh if we're at the top of the scrollable view
                        if (!canChildScrollUp()) {
                            return super.onInterceptTouchEvent(event);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }
        return false;
    }

    @Override
    public boolean canChildScrollUp() {
        if (scrollView != null) {
            // Check if the scrollable view can scroll up
            return scrollView.canScrollVertically(-1);
        }
        return super.canChildScrollUp();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        isRefreshing = refreshing;
    }
}