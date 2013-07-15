/**
 * Copyright 2013 TAKAHIRO GOTO <goto@gunew.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package garlicg.widget.sidecover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

public abstract class SideCover extends FrameLayout{
	private static final boolean DEBUG = false;
	private static final int DURATION_MAX = 600;
	private static final int CLOSE_ENOUGH = 3;
    protected static final int ANIMATION_DELAY = 1000 / 60;
    protected static final int ON_EDGE_COVER_ALPHA = 0x00;
    public static final int DISPLAY_ON_CONTENT = 0;
    public static final int DISPLAY_ON_WINDOW = 1;
    public static final int DISPLAY_ON_TARGET_CONTENT = 1;
    public static final int POSITION_LEFT = 0;
    public static final int POSITION_RIGHT = 1;
    public static final int TOUCH_MODE_NONE = 0;
    public static final int TOUCH_MODE_CONTENT = 1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_CLOSING = 1;
    public static final int STATE_DRAGGING = 2;
    public static final int STATE_OPENING = 4;
    public static final int STATE_OPEN = 8;
    private Scroller mScroller;
    protected FrameLayout mCoverContainer;
    protected Drawable mDropShadowDrawable;
    protected boolean mIsDragging;
    protected boolean mIsCoverVisible;
    protected boolean mIsAlphaAnimation;
    private int mDisplayMode;
    private int mCoverState = STATE_CLOSED;
    protected int mDropShadowWidth;
    protected int mCoverWidth;
    protected int mCurrentOffset;
    protected int mTouchWidth;
    protected int mTouchMode = TOUCH_MODE_CONTENT;
    protected int mCloseEnough;
    protected long mPeekDelay;
    private int mSystemParHeight;
    
    public interface OnCoverChangeListener{
    	public void onChange(int stateCode);
    }
    private OnCoverChangeListener mListener;
    protected void setOnCoverChangeListener(OnCoverChangeListener listener){
    	mListener = listener;
    }
    
    private class SmoothInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    }
   
    public SideCover(Context context) {
        this(context, null, R.attr.sideCoverStyle);
    }

    @SuppressWarnings("deprecation")
	public SideCover(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        setFocusable(false);

        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.SideCover, defStyle, R.style.Widget_SideCover);

        final Drawable coverBackground = a.getDrawable(R.styleable.SideCover_scCoverBackground);
        mCoverWidth = a.getDimensionPixelSize(R.styleable.SideCover_scCoverWidth, 0);
        final int dropShadowColor = a.getColor(R.styleable.SideCover_scDropShadowColor, 0x66000000);
        mDropShadowDrawable = getDropShadowColor(dropShadowColor);
        mDropShadowWidth = a.getDimensionPixelSize(R.styleable.SideCover_scDropShadowWidth, dpToPx(4));
//        mIsTranslateAnimation = a.getBoolean(R.styleable.SideCover_scTranslateAnimation, false);
        a.recycle();
        
        mCoverContainer = new FrameLayout(context);
        if(coverBackground == null){
        	mCoverContainer.setBackgroundDrawable(coverBackground);
        	mCoverContainer.setBackgroundColor(0x66AACCFF);
        }
        else{
        	mCoverContainer.setBackgroundDrawable(coverBackground);
        }
        addView(mCoverContainer);

        mScroller = new Scroller(context , new SmoothInterpolator());
        mCloseEnough = dpToPx(CLOSE_ENOUGH);
        mSystemParHeight = (int)(25 * context.getResources().getDisplayMetrics().density);
        
    }
    
    private int dpToPx(int dp) {
    	return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    
    public void setCoverView(View view){
    	mCoverContainer.addView(view);
    }
    
//    public void replaceCoverFragment(FragmentTransaction ft , Fragment fragment){
//    	ft.replace(mCoverContainer.getId(), fragment);
//    }
    
    public void setDisplayMode(int displayMode) {
        mDisplayMode = displayMode;
        if(displayMode == DISPLAY_ON_WINDOW){
        	mCoverContainer.setPadding(0, mSystemParHeight, 0, 0);
        }
    }

    public void setTouchMode(int touchMode) {
        if (mTouchMode != touchMode) {
            mTouchMode = touchMode;
            updateTouchAreaWidth();
        }
    }
    
    public void toggleCover() {
        if (mCoverState == STATE_OPEN || mCoverState == STATE_OPENING) {
        	closeCover();
        } else if (mCoverState == STATE_CLOSED || mCoverState == STATE_CLOSING) {
        	openCover();
        }
    }

    public void openCover() {
        animateOffsetTo(mCoverWidth, 0);
    }

    public void closeCover() {
        animateOffsetTo(0, 0);
    }
    
    public boolean isVisible(){
    	return mIsCoverVisible;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mCoverWidth == 0) 
        	mCoverWidth = (int) (width * 0.65f);
        
        if (mCurrentOffset == -1){
        	setOffsetPixels(mCoverWidth);
        }

        final int coverWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, mCoverWidth);
        final int coverHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, height);
        mCoverContainer.measure(coverWidthMeasureSpec, coverHeightMeasureSpec);

        setMeasuredDimension(width, height);
        updateTouchAreaWidth();
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawDropShadow(canvas, mCurrentOffset);
    }
    
    protected void setCoverState(int state){
    	if(mCoverState != state){
    		mCoverState = state;
    		if(mListener != null)
    		mListener.onChange(state);
    		if(DEBUG)
    		android.util.Log.i("CoverState", "state change to : " + state);
    	}
    }
    
    protected void animateOffsetTo(int position, int velocity) {
    	mIsDragging = false;
        

        final int startX = mCurrentOffset;
        final int dx = position - startX;
        if (dx == 0) {
            setOffsetPixels(position);
            setCoverState(position == 0 ? STATE_CLOSED : STATE_OPEN);
            return;
        }

        mCoverState = (dx > 0) ? STATE_OPENING : STATE_CLOSING;
        
        velocity = Math.abs(velocity);
        int duration;
        if (velocity == 0) {
        	duration = (int) (1200.f * Math.abs((float) dx / mCoverWidth));
        } else {
        	duration = 4 * Math.round(1000.f * Math.abs((float) dx / velocity));
        }
        duration = Math.min(duration, DURATION_MAX);

        mScroller.startScroll(startX, 0, dx, 0, duration);
        postAnimationInvalidate();
    }
    
    protected void setOffsetPixels(int offsetPixels) {
        if (offsetPixels != mCurrentOffset) {
            onOffsetPixelsChanged(offsetPixels);
            mCurrentOffset = offsetPixels;
            mIsCoverVisible = offsetPixels != 0;
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (mDisplayMode == DISPLAY_ON_WINDOW) {
        	mCoverContainer.setPadding(0, insets.top, 0, 0);
        }
        return super.fitSystemWindows(insets);
    }

    private void updateTouchAreaWidth() {
        if (mTouchMode == TOUCH_MODE_CONTENT) {
            mTouchWidth = getMeasuredWidth();
        } else {
            mTouchWidth = 0;
        }
    }
 
    protected boolean isCloseEnough() {
        return mCurrentOffset <= mCloseEnough;
    }
    
    protected abstract Drawable getDropShadowColor(int color);
    
    protected abstract void drawDropShadow(Canvas canvas, int offsetPixels);

    protected abstract void onOffsetPixelsChanged(int offsetPixels);
    
    protected abstract boolean isAirTouch(MotionEvent ev);

    protected abstract boolean onDownAllowDrag(MotionEvent ev , float initialMotionX);

    protected abstract boolean onMoveAllowDrag(MotionEvent ev, float dx , float initialMotionX);

    protected abstract void onMoveEvent(float dx);

    protected abstract void onUpEvent(MotionEvent ev , VelocityTracker tracker);

    protected void stopAnimation() {
        removeCallbacks(mDragRunnable);
        mScroller.abortAnimation();
    }
    
    @SuppressLint("NewApi")
	@Override
    public void postOnAnimation(Runnable action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.postOnAnimation(action);
        } else {
            postDelayed(action, ANIMATION_DELAY);
        }
    }
    
    private final Runnable mDragRunnable = new Runnable() {
        public void run() {
            postAnimationInvalidate();
        }
    };
    
    private void postAnimationInvalidate() {
        if (mScroller.computeScrollOffset()) {
            final int oldX = mCurrentOffset;
            final int x = mScroller.getCurrX();
            if (x != oldX){
            	setOffsetPixels(x);
            }
            if (x != mScroller.getFinalX()) {
                postOnAnimation(mDragRunnable);
                return;
            }
        }

        // move contentscomplete scroll Animation
        mScroller.abortAnimation();
        final int finalX = mScroller.getFinalX();
        setOffsetPixels(finalX);
        setCoverState(finalX == 0 ? STATE_CLOSED : STATE_OPEN);
        
    }
}
