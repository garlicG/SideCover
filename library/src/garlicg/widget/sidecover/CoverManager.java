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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class CoverManager {
	private final Activity mActivity;
	private final SideCover mSideCover;
	private final ObserveContainer mObserveContainer;

	public CoverManager(Activity activity){
		this(activity, SideCover.POSITION_LEFT, SideCover.TOUCH_MODE_CONTENT);
	}
	
	public CoverManager(Activity activity,int gravity , int touchMode){
		mActivity = activity;
		mSideCover = (gravity == SideCover.POSITION_LEFT) ? 
				new LeftCover(activity) : new RightCover(activity);
		mSideCover.setTouchMode(touchMode);
		mObserveContainer = new ObserveContainer(activity , mSideCover);
	}

	// OK
    public void setContentViewWithContentCover(int layoutResId){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_CONTENT);
    	
    	ViewGroup content = (ViewGroup)mActivity.findViewById(android.R.id.content);
    	content.removeAllViews();
    	content.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	inflater.inflate(layoutResId, mObserveContainer, true);
    	
    	mObserveContainer.addView(mSideCover);
    }
    
    // OK
    public void setContentViewWithContentCover(View view){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_CONTENT);
		ViewGroup content = (ViewGroup)mActivity.findViewById(android.R.id.content);
		content.removeAllViews();
		content.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
		mObserveContainer.addView(view);
        mObserveContainer.addView(mSideCover);
    }
    
    
    // OK
    public void setContentViewWithWindowCover(int layoutResId){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_WINDOW);
    	ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
    	ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
    	
    	decorView.removeAllViews();
    	decorView.addView(mObserveContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	mObserveContainer.addView(decorChild, LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
    	
    	mActivity.setContentView(layoutResId);
    	mObserveContainer.addView(mSideCover);
    }
    
    // OK
    public void setContentViewWithWindowCover(View view){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_WINDOW);
		ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
		
		decorView.removeAllViews();
		decorView.addView(mObserveContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mObserveContainer.addView(decorChild, LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
		decorChild.addView(view);
		mObserveContainer.addView(mSideCover);
    }
    
    // OK
    public void bindContentCover(ViewGroup targetContent){
    	int count = targetContent.getChildCount();
    	if(count > 0){
    		View tartgetChild = targetContent.getChildAt(0);
    		targetContent.removeAllViews();
    		targetContent.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
    		mObserveContainer.addView(tartgetChild);
    		mObserveContainer.addView(mSideCover);
    	}
    	else{
    		targetContent.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
    		mObserveContainer.addView(mSideCover);
    	}
    }
    
    public void setCoverView(View view){
    	mSideCover.setCoverView(view);
    }
    
//    public void replaceCoverFragment(FragmentTransaction ft , Fragment fragment){
//    	mSideCover.replaceCoverFragment(ft, fragment);
//    }

    public void toggleCover() {
        mSideCover.toggleCover();
    }

    public void openCover() {
        mSideCover.openCover();
    }

    public void closeCover() {
        mSideCover.closeCover();
    }
    
    public boolean isVisible(){
    	return mSideCover.isVisible();
    }
	
}


