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
	private final ObserveContainer mObserveContainer;
	private SideCover mSideCover;

	public CoverManager(Activity activity){
		this(activity, SideCover.POSITION_LEFT, SideCover.TOUCH_MODE_CONTENT);
	}
	
	public CoverManager(Activity activity,int gravity , int touchMode){
		mActivity = activity;
		if(gravity == SideCover.POSITION_LEFT){
			mSideCover = new LeftCover(activity);
		}
		else if(gravity == SideCover.POSITION_RIGHT){
			mSideCover = new RightCover(activity);
		}
		else{
			throw new IllegalArgumentException("unknow gravity of SideCover");
		}
		mSideCover.setTouchMode(touchMode);
		mObserveContainer = new ObserveContainer(activity , mSideCover);
	}

	/**
	 * setContentViewと一緒にカバーをバインドします。
	 * カバーはcontentにバインドされます。(ActionBarは含まないよ)
	 * Activity側でsetContentViewをよばないでください。
	 * @param layoutResId
	 */
    public void setContentViewWithContentCover(int layoutResId){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_CONTENT);
    	
    	ViewGroup content = (ViewGroup)mActivity.findViewById(android.R.id.content);
    	content.removeAllViews();
    	content.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	inflater.inflate(layoutResId, mObserveContainer, true);
    	
    	mObserveContainer.addView(mSideCover);
    }
    
    /**
     * setContentViewと一緒にカバーをバインドします。
     * カバーはcontentにバインドされます。(ActionBarは含まないよ)
     * Activity側でsetContentViewをよばないでください。
     * @param View view
     */
    public void setContentViewWithContentCover(View view){
    	mSideCover.setDisplayMode(SideCover.DISPLAY_ON_CONTENT);
		ViewGroup content = (ViewGroup)mActivity.findViewById(android.R.id.content);
		content.removeAllViews();
		content.addView(mObserveContainer , LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
		mObserveContainer.addView(view);
        mObserveContainer.addView(mSideCover);
    }
    
    
    /**
     * setContentViewと一緒にカバーをバインドします。
     * カバーはWindow全体にバインドされます。(ActionBarごとずれるよ)
     * Activity側でsetContentViewをよばないでください。
     */
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
    
    /**
     * setContentViewと一緒にカバーをバインドします。
     * カバーはWindow全体にバインドされます。(ActionBarごとずれるよ)
     * Activity側でsetContentViewをよばないでください。
     */
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
    
    /**
     * ViewGroupに対してカバービューをバインドします。
     * @param targetContent 
     */
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
    
    /**
     * カバーのビューを設定します。
     * @param view
     */
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
    
    public void setOnCoverChangeListener(SideCover.OnCoverChangeListener listener){
    	mSideCover.setOnCoverChangeListener(listener);
    }
	
}


