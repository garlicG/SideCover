package garlicg.widget.sidecover.sample;

import garlicg.widget.sidecover.CoverManager;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StyledCover extends Activity implements OnItemClickListener{
	// aa
	CoverManager mCoverManager;
	TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Default type cover
		mCoverManager = new CoverManager(this);
		
		// Do not call setContentView here, CoverManager set contentView with layout resource
		mCoverManager.setContentViewWithContentCover(R.layout.activity_simple_cover);
		
		// set original view to the cover
        ListView listView = new ListView(this);
        ArrayAdapter<Items> adapter = new ArrayAdapter<Items>(this, android.R.layout.simple_list_item_1 ,Items.values());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setDivider(null);
        mCoverManager.setCoverView(listView);
        
		mTextView = (TextView)findViewById(R.id.textView1);
		mTextView.setText("pull the cover from left side");
	}
	
	enum Items{
		BLUE(Color.BLUE) , RED(Color.RED) , YELLOW(Color.YELLOW) , GREEN(Color.GREEN) , GRAY(Color.GRAY) , WHITE(Color.WHITE);
		public int eColor;
		private Items(int color) {
			eColor = color;
		}
	}
	
	@Override
    public void onBackPressed() {
    	if(mCoverManager.isVisible()){
    		mCoverManager.closeCover();
    		return;
    	}
    	super.onBackPressed();
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Items item = (Items)arg0.getItemAtPosition(arg2);
		mTextView.setBackgroundColor(item.eColor);
		mCoverManager.closeCover();
	};
}
