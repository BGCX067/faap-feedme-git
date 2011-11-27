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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import de.faap.feedme.R;
import de.faap.feedme.provider.MockModel;

public class PlanActivity extends ActionBarActivity {

    private static final int NUM_ITEMS = 2;
    private static Context mContext;

    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.plan);

	mContext = getApplicationContext();
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
	    Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT)
		    .show();
	    getActionBarHelper().setRefreshActionItemState(true);
	    getWindow().getDecorView().postDelayed(new Runnable() {
		@Override
		public void run() {
		    getActionBarHelper().setRefreshActionItemState(false);
		}
	    }, 1000);
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
		return PlannerFragment.newInstance(position);
	    } else if (position == 1) {
		return WeekFragment.newInstance(position);
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
    }

    private static class WeekFragment extends Fragment {
	public TextView sat;
	public TextView sun;
	public TextView mon;
	public TextView tue;
	public TextView wed;
	public TextView thu;
	public TextView fri;

	static WeekFragment newInstance(int position) {
	    WeekFragment mWF = new WeekFragment();
	    // TODO irgendwann entfernen, beispiel code für extras
	    // Bundle args = new Bundle();
	    // args.putInt("num", position);
	    // mWF.setArguments(args);

	    return mWF;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.week, container, false);

	    // TODO vernünftig machen =)
	    sat = (TextView) v.findViewById(R.id.rcp_sat);
	    sun = (TextView) v.findViewById(R.id.rcp_sun);
	    mon = (TextView) v.findViewById(R.id.rcp_mon);
	    tue = (TextView) v.findViewById(R.id.rcp_tue);
	    wed = (TextView) v.findViewById(R.id.rcp_wed);
	    thu = (TextView) v.findViewById(R.id.rcp_thu);
	    fri = (TextView) v.findViewById(R.id.rcp_fri);

	    MockModel model = new MockModel();
	    sat.setText(model.IWillTakeMyTimeToCreateSomethingSpecial());
	    sun.setText(sat.getText());
	    mon.setText(model.iWantFoodFast());
	    tue.setText(model.iWantFoodFast());
	    wed.setText(model.iWantFoodFast());
	    thu.setText("---");
	    fri.setText("---");

	    return v;
	}
    }

    private static class PlannerFragment extends Fragment {
	static PlannerFragment newInstance(int position) {
	    PlannerFragment mPF = new PlannerFragment();
	    return mPF;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.planner, container, false);
	    return v;
	}
    }
}
