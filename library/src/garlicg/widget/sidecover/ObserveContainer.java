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

import java.util.logging.Logger;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class ObserveContainer extends FrameLayout{
	private SideCover mSideCover;
	private int mTouchSlop;
	private int mMaxVelocity;
	private VelocityTracker mVelocityTracker;
	private float mLastMotionX = -1;
	private float mLastMotionY = -1;
	private float mInitialMotionX = -1;
	
	public ObserveContainer(Context context , SideCover cover) {
		super(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
		mSideCover = cover;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEvent.ACTION_MASK;
		
		if (action == MotionEvent.ACTION_DOWN && mSideCover.mIsCoverVisible && mSideCover.isCloseEnough()) {
			mSideCover.setOffsetPixels(0);
			mSideCover.stopAnimation();
			mSideCover.setCoverState(SideCover.STATE_CLOSED);
		}
		
		if (mSideCover.mIsCoverVisible && mSideCover.isAirTouch(ev)){
			return true;
		}
		
		if (mSideCover.mTouchMode == SideCover.TOUCH_MODE_NONE) {
			return false;
		}
		
		if (action != MotionEvent.ACTION_DOWN) {
			if (mSideCover.mIsDragging){
				return true;
			}
		}
	        
		
		if(action == MotionEvent.ACTION_DOWN){
			 mLastMotionX = mInitialMotionX = ev.getX();
             mLastMotionY = ev.getY();
             final boolean allowDrag = mSideCover.onDownAllowDrag(ev , mInitialMotionX);

             if (allowDrag) {
                 mSideCover.setCoverState(mSideCover.isVisible() ? SideCover.STATE_OPEN : SideCover.STATE_CLOSED);
                 mSideCover.stopAnimation();
                 mSideCover.mIsDragging = false;
             }
		}
		
		else if(action == MotionEvent.ACTION_MOVE){
			final float x = ev.getX();
            final float dx = x - mLastMotionX;
            final float xDiff = Math.abs(dx);
            final float y = ev.getY();
            final float yDiff = Math.abs(y - mLastMotionY);

            if (xDiff > mTouchSlop && xDiff > yDiff) {
                final boolean allowDrag = mSideCover.onMoveAllowDrag(ev, dx , mInitialMotionX);
                if (allowDrag) {
                    mSideCover.setCoverState(SideCover.STATE_DRAGGING);
                    mSideCover.mIsDragging = true;
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
            }
		}
		else{
			final int offsetPixels = mSideCover.mCurrentOffset;
			mSideCover.animateOffsetTo(offsetPixels > mSideCover.mCoverWidth /2 ? mSideCover.mCoverWidth : 0 , 0);
		}
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
			android.util.Log.i("observeContainer", "onInterceptTouchEvent:obtain");
		}
		mVelocityTracker.addMovement(ev);
		return mSideCover.mIsDragging;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mSideCover.mIsCoverVisible && (mSideCover.mTouchMode == SideCover.TOUCH_MODE_NONE) && !mSideCover.mIsDragging) {
            return false;
        }
        final int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (mVelocityTracker == null){
        	android.util.Log.i("observeContainer", "onTouchEvent:obtain");
        	mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = event.getX();
                mLastMotionY = event.getY();
                final boolean allowDrag = mSideCover.onDownAllowDrag(event , mInitialMotionX);

                if (allowDrag) {
                    mSideCover.stopAnimation();
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!mSideCover.mIsDragging) {
                    final float x = event.getX();
                    final float dx = x - mLastMotionX;
                    final float xDiff = Math.abs(dx);
                    final float y = event.getY();
                    final float yDiff = Math.abs(y - mLastMotionY);

                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        final boolean allowDrag = mSideCover.onMoveAllowDrag(event, dx, mInitialMotionX);

                        if (allowDrag) {
                            mSideCover.setCoverState(SideCover.STATE_DRAGGING);
                            mSideCover.mIsDragging = true;
                            mLastMotionX = x - mInitialMotionX > 0 ?
                                    mInitialMotionX + mTouchSlop : mInitialMotionX - mTouchSlop;
                        }
                    }
                }

                if (mSideCover.mIsDragging) {
                    final float x = event.getX();
                    final float dx = x - mLastMotionX;

                    mLastMotionX = x;
                    mSideCover.onMoveEvent(dx);
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            	// unknown error
            	try {
            		mVelocityTracker.recycle();
				} catch (IllegalStateException e) {
					e.toString();
				}
            case MotionEvent.ACTION_UP: {
            	mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                mSideCover.onUpEvent(event , mVelocityTracker);
                // unknown error
                try {
                	mVelocityTracker.recycle();
                } catch (IllegalStateException e) {
                	e.toString();
                }
                break;
            }
        }
		return true;
	}
}
