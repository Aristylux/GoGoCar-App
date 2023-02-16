package com.example.app6;

import android.content.Context;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private View view;
    private float startX;
    private float startY;
    private float lastX;

    private static final long UPDATE_INTERVAL_MS = 50;
    private long mLastUpdateTime = SystemClock.uptimeMillis();


    public OnSwipeTouchListener(Context context, View view) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.view = view;
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = startX;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                return true;
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaX = e2.getX() - lastX;
            lastX = e2.getX();
            float translationX = view.getTranslationX() + deltaX;
            view.setTranslationX(translationX);

            // Limit the rate at which the view is updated
            long now = SystemClock.uptimeMillis();
            long elapsed = now - mLastUpdateTime;
            if (elapsed > UPDATE_INTERVAL_MS) {
                mLastUpdateTime = now;
                view.invalidate();
            }

            return true;
        }
    }
}
