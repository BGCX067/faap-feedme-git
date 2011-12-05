package de.faap.feedme.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import de.faap.feedme.R;
import de.faap.feedme.util.Preferences;

public class RecipesActivity extends ActionBarActivity {

    private static final int NUM_ITEMS = 3;

    private static Context mContext;
    private static Preferences preferences;

    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.recipes);

	mContext = getApplicationContext();
	mFPAdapter = new mFPAdapter(getSupportFragmentManager());
	mViewPager = (ViewPager) findViewById(R.id.recipes_viewpager);
	mViewPager.setAdapter(mFPAdapter);
	mTPIndicator = (TitlePageIndicator) findViewById(R.id.recipes_indicator);
	mTPIndicator.setViewPager(mViewPager, 1);
	preferences = new Preferences(this);
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

    private static class mFPAdapter extends FragmentPagerAdapter implements
	    TitleProvider {

	public mFPAdapter(FragmentManager fm) {
	    super(fm);
	}

	@Override
	public Fragment getItem(int position) {
	    if (0 <= position && position <= 2) {
		return CategoryFragment.newInstance(position);
	    }
	    return null;
	}

	@Override
	public int getCount() {
	    return NUM_ITEMS;
	}

	@Override
	public String getTitle(int position) {
	    if (position == 0) {
		return mContext.getResources().getString(R.string.ind_effort);
	    } else if (position == 1) {
		return mContext.getResources().getString(R.string.ind_type);
	    } else if (position == 2) {
		return mContext.getResources().getString(R.string.ind_cuisine);
	    } else {
		return null;
	    }
	}
    }

    private static class CategoryFragment extends Fragment {
	private int pos;

	static CategoryFragment newInstance(int position) {
	    CategoryFragment mCF = new CategoryFragment();

	    // needed to load right array
	    Bundle args = new Bundle();
	    args.putInt("num", position);
	    mCF.setArguments(args);

	    return mCF;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    pos = getArguments().getInt("num", -1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    ListView v = (ListView) inflater.inflate(R.layout.listview,
		    container, false);

	    // TODO mock the entries :) delete this
	    String[] mock = { "0" };
	    preferences.saveEffort(mock);
	    mock[0] = "1";
	    preferences.saveType(mock);
	    mock[0] = "2";
	    preferences.saveCuisine(mock);

	    String[] category = { "scheiße" };
	    // load the right list
	    if (pos == 0) {
		category = preferences.getEffort();
	    } else if (pos == 1) {
		category = preferences.getType();
	    } else if (pos == 2) {
		category = preferences.getCuisine();
	    }

	    v.setAdapter(new ArrayAdapter<String>(mContext, R.layout.listitem,
		    category));

	    v.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
			long id) {
		    Intent mIntent = new Intent(mContext,
			    PreperationActivity.class);
		    mIntent.putExtra(DashboardActivity.ACTIONBAR_TITLE,
			    ((TextView) v).getText());
		    mIntent.putExtra(DashboardActivity.ACTIONBAR_ICON,
			    DashboardActivity.iconRecipes);
		    startActivity(mIntent);
		}

	    });

	    return v;
	}
    }
}
