package garlicg.widget.sidecover.sample;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	Class<? extends Activity>[] c = new Class[]{
			SimpleCover.class ,
			WindowCover.class,
			StyledCover.class,
			};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<Class<? extends Activity>> adapter = new ArrayAdapter<Class<? extends Activity>>(this,android.R.layout.simple_list_item_1,c);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Class<? extends Activity> a = (Class<? extends Activity>)l.getItemAtPosition(position);
    	startActivity(new Intent(this ,a));
    	super.onListItemClick(l, v, position, id);
    }

}
