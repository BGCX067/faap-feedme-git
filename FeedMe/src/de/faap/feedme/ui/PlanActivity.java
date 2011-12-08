package de.faap.feedme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import de.faap.feedme.R;
import de.faap.feedme.provider.MockModel;
import de.faap.feedme.util.Preferences;

public class PlanActivity extends ActionBarActivity {

    static final int NUM_ITEMS = 2;

    Context mContext;
    Preferences preferences;

    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.plan);

	mContext = getApplicationContext();
	preferences = new Preferences(this);

	mFPAdapter = new mFPAdapter(getSupportFragmentManager());
	mViewPager = (ViewPager) findViewById(R.id.plan_viewpager);
	mViewPager.setAdapter(mFPAdapter);
	mTPIndicator = (TitlePageIndicator) findViewById(R.id.plan_indicator);
	mTPIndicator.setViewPager(mViewPager, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater menuInflater = getMenuInflater();
	menuInflater.inflate(R.menu.plan, menu);
	// Calling super after populating the menu is necessary here to ensure
	// that the action bar helpers have a chance to handle this event.
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    break;

	case R.id.menu_refresh:
	    getActionBarHelper().setRefreshActionItemState(true);

	    // TODO refresh week
	    // check buttons
	    // query recipes
	    // compute result
	    // save recipe-names
	    String[] week = new String[7];
	    MockModel model = new MockModel();
	    week[0] = model.IWillTakeMyTimeToCreateSomethingSpecial();
	    week[1] = week[0];
	    week[2] = model.iWantFoodFast();
	    week[3] = model.iWantFoodFast();
	    week[4] = model.iWantFoodFast();
	    week[5] = "---";
	    week[6] = "---";

	    preferences.saveWeek(week);
	    mFPAdapter.notifyDataSetChanged();
	    getActionBarHelper().setRefreshActionItemState(false);
	    break;
	}
	return super.onOptionsItemSelected(item);
    }

    /**
     * This class creates planner and week fragments and provides their titles
     * 
     * @author joe
     * 
     */
    private class mFPAdapter extends FragmentPagerAdapter implements
	    TitleProvider {

	public mFPAdapter(FragmentManager fm) {
	    super(fm);
	}

	@Override
	public Fragment getItem(int position) {
	    if (position == 0) {
		return new PlannerFragment();
	    } else if (position == 1) {
		return new WeekFragment();
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
		return mContext.getResources().getString(R.string.ind_planner);
	    } else if (position == 1) {
		return mContext.getResources().getString(R.string.ind_week);
	    } else {
		return null;
	    }
	}

	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
    }

    /**
     * This class handles the week overview
     * 
     * @author joe
     * 
     */
    private class WeekFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.week, container, false);

	    String[] week = preferences.getWeek();

	    // TODO make Textviews clickable
	    ((TextView) v.findViewById(R.id.rcp_sat)).setText(week[0]);
	    ((TextView) v.findViewById(R.id.rcp_sun)).setText(week[1]);
	    ((TextView) v.findViewById(R.id.rcp_mon)).setText(week[2]);
	    ((TextView) v.findViewById(R.id.rcp_tue)).setText(week[3]);
	    ((TextView) v.findViewById(R.id.rcp_wed)).setText(week[4]);
	    ((TextView) v.findViewById(R.id.rcp_thu)).setText(week[5]);
	    ((TextView) v.findViewById(R.id.rcp_fri)).setText(week[6]);

	    // TODO implements button listeners
	    // query, preferences, etctect

	    return v;
	}
    }

    /**
     * This class handles user preferences for computing the diet plan
     * 
     * @author joe
     * 
     */
    private class PlannerFragment extends Fragment {
	// radio groups handles needed for loading and saving preferences
	private RadioGroup sat;
	private RadioGroup sun;
	private RadioGroup mon;
	private RadioGroup tue;
	private RadioGroup wed;
	private RadioGroup thu;
	private RadioGroup fri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.planner, container, false);

	    sat = (RadioGroup) v.findViewById(R.id.radioGroup0);
	    sun = (RadioGroup) v.findViewById(R.id.radioGroup1);
	    mon = (RadioGroup) v.findViewById(R.id.radioGroup2);
	    tue = (RadioGroup) v.findViewById(R.id.radioGroup3);
	    wed = (RadioGroup) v.findViewById(R.id.radioGroup4);
	    thu = (RadioGroup) v.findViewById(R.id.radioGroup5);
	    fri = (RadioGroup) v.findViewById(R.id.radioGroup6);

	    int[] checkedButtons = preferences.getCheckedButtons();

	    sat.check(checkedButtons[0]);
	    sun.check(checkedButtons[1]);
	    mon.check(checkedButtons[2]);
	    tue.check(checkedButtons[3]);
	    wed.check(checkedButtons[4]);
	    thu.check(checkedButtons[5]);
	    fri.check(checkedButtons[6]);

	    return v;
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();

	    int[] checkedButtons = new int[7];

	    checkedButtons[0] = sat.getCheckedRadioButtonId();
	    checkedButtons[1] = sun.getCheckedRadioButtonId();
	    checkedButtons[2] = mon.getCheckedRadioButtonId();
	    checkedButtons[3] = tue.getCheckedRadioButtonId();
	    checkedButtons[4] = wed.getCheckedRadioButtonId();
	    checkedButtons[5] = thu.getCheckedRadioButtonId();
	    checkedButtons[6] = fri.getCheckedRadioButtonId();

	    preferences.saveCheckedButtons(checkedButtons);
	}
    }
}
