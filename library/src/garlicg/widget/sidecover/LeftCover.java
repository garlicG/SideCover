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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class LeftCover extends SideCover{
	public LeftCover(Context context) {
        super(context);
    }

    public LeftCover(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public Drawable getDropShadowColor(int color) {
        final int endColor = color & 0x00FFFFFF;
        return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
                color,
                endColor,
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        final int width = r - l;
        final int height = b - t;
        final int offsetPixels = mCurrentOffset;

        mCoverContainer.layout(0, 0, mCoverWidth, height);
        offsetCover(offsetPixels);

    }

    /**
     * Offsets the menu relative to its original position based on the position of the content.
     *
     * @param offsetPixels The number of pixels the content if offset.
     */
    private void offsetCover(int offsetPixels) {
        if (mCoverWidth != 0) {
            final int menuWidth = mCoverWidth;
            final float openRatio = 1.f-(menuWidth - (float) offsetPixels) / menuWidth;
            final int oldMenuLeft = mCoverContainer.getLeft();
            final int offset = (int) ((-(1.f-openRatio) * menuWidth)) - oldMenuLeft;
            mCoverContainer.offsetLeftAndRight(offset);
            if(mIsTranslateAnimation)
            	mCoverContainer.getBackground().setAlpha((int) ((0xFF-(0xFF-ON_EDGE_COVER_ALPHA) * (1.f - openRatio))));
        }
    }

    @Override
    protected void drawDropShadow(Canvas canvas, int offsetPixels) {
        final int height = getHeight();
        final int menuWidth = mCoverWidth;
        final float openRatio = 1.f- ((menuWidth - (float) offsetPixels) / menuWidth);

        mDropShadowDrawable.setBounds(offsetPixels, 0, offsetPixels + mDropShadowWidth, height);
        if(mIsTranslateAnimation)
        	mDropShadowDrawable.setAlpha((int) (0xFF-(0xFF * (1.f - openRatio)))); 
        mDropShadowDrawable.draw(canvas);
    }

    @Override
    protected void onOffsetPixelsChanged(int offsetPixels) {
    	offsetCover(offsetPixels);
        invalidate();
    }

    @Override
    protected boolean isAirTouch(MotionEvent ev) {
        return ev.getX() > mCurrentOffset;
    }

    @Override
    protected boolean onDownAllowDrag(MotionEvent ev , final float initialMotionX) {
        return (!mIsCoverVisible && initialMotionX <= mTouchWidth)
                || (mIsCoverVisible && initialMotionX >= mCurrentOffset);
    }

    @Override
    protected boolean onMoveAllowDrag(MotionEvent ev, float diff, final float initialMotionX) {
        return (!mIsCoverVisible && initialMotionX <= mTouchWidth && (diff > 0))
                || (mIsCoverVisible && initialMotionX >= mCurrentOffset);
    }

    @Override
    protected void onMoveEvent(float dx) {
        setOffsetPixels(Math.min(Math.max(mCurrentOffset + (int) dx, 0), mCoverWidth));
    }

    @Override
    protected void onUpEvent(MotionEvent ev , VelocityTracker tracker) {
        final int offsetPixels = mCurrentOffset;

        if (mIsDragging) {
            final int initialVelocity = (int) tracker.getXVelocity();
            animateOffsetTo(tracker.getXVelocity() > 0 ? mCoverWidth : 0, initialVelocity);

        } else if (mIsCoverVisible && ev.getX() > offsetPixels) {
            closeCover();
        }
    }
}