package de.faap.feedme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;

import de.faap.feedme.R;
import de.faap.feedme.provider.MockModel;

public class RecipesActivity extends ActionBarActivity {
    private ListView mListView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.recipes);

	mListView = (ListView) findViewById(R.id.recipes_listview);

	// TODO vernünftig machen
	MockModel model = new MockModel();
	String[] daFood = model.getTheCoolFood();

	mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.listitem,
		daFood));
	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> a, View v, int position,
		    long id) {
		Intent mIntent = new Intent(getApplicationContext(),
			PreperationActivity.class);
		mIntent.putExtra(DashboardActivity.ACTIONBAR_TITLE,
			((TextView) v).getText());
		mIntent.putExtra(DashboardActivity.ACTIONBAR_ICON,
			DashboardActivity.iconRecipes);
		startActivity(mIntent);
	    }

	});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    break;
	}
	return super.onOptionsItemSelected(item);
    }
}
