package de.faap.feedme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import de.faap.feedme.R;

public class PreperationActivity extends ActionBarActivity {
    private static final int NUM_ITEMS = 2;
    private static Context mContext;

    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.preperation);

	mContext = getApplicationContext();
	mFPAdapter = new mFPAdapter(getSupportFragmentManager());
	mViewPager = (ViewPager) findViewById(R.id.prep_viewpager);
	mViewPager.setAdapter(mFPAdapter);
	mTPIndicator = (TitlePageIndicator) findViewById(R.id.prep_indicator);
	mTPIndicator.setViewPager(mViewPager, 0);
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
	    if (position == 0) {
		return IngredientsFragment.newInstance(position);
	    } else if (position == 1) {
		return PreperationFragment.newInstance(position);
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
		return mContext.getResources().getString(
			R.string.ind_ingredients);
	    } else if (position == 1) {
		return mContext.getResources().getString(
			R.string.ind_preperation);
	    } else {
		return null;
	    }
	}
    }

    private static class IngredientsFragment extends Fragment {

	static IngredientsFragment newInstance(int position) {
	    IngredientsFragment mIF = new IngredientsFragment();
	    return mIF;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.ingredients, container, false);
	    // TODO vernünftig machen
	    String[] ingredients = { "Handy", "Guthaben" };

	    ListView mListView = (ListView) v
		    .findViewById(R.id.ingredients_listview);

	    mListView.setAdapter(new ArrayAdapter<String>(mContext,
		    R.layout.listitem, ingredients));

	    return v;
	}
    }

    private static class PreperationFragment extends Fragment {
	static PreperationFragment newInstance(int position) {
	    PreperationFragment mPF = new PreperationFragment();
	    return mPF;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.prepdetails, container, false);
	    // TODO vernünftig machen

	    TextView mTextView = (TextView) v.findViewById(R.id.prep_textview);
	    mTextView.setText("PizzaTaxi anrufen");
	    return v;
	}
    }
}
