package de.faap.feedme.ui;

import java.util.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.example.android.actionbarcompat.*;
import com.viewpagerindicator.*;
import de.faap.feedme.R;
import de.faap.feedme.provider.*;
import de.faap.feedme.util.*;

public class PlanActivity extends ActionBarActivity {

    protected static final int NUM_ITEMS = 2;

    protected Context mContext;
    protected Preferences preferences;
    protected mFPAdapter mFPAdapter;

    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setIcon(getResources().getDrawable(R.drawable.ic_planer));
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        setTitle(getResources().getString(R.string.title_plan));

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
            RecipeProvider provider = new RecipeProvider(mContext);
            String[] week =
                    provider.proposeRecipes(preferences.getCheckedButtons());
            saveUpdateTime(new GregorianCalendar());
            preferences.saveWeek(week);
            mFPAdapter.notifyDataSetChanged();
            getActionBarHelper().setRefreshActionItemState(false);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void saveUpdateTime(GregorianCalendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        preferences.saveLastUpdate(year, month, day);
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
    protected class WeekFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.week, container, false);

            // Check if automatic weekly update is needed
            GregorianCalendar now = new GregorianCalendar();
            int[] lastUpdate = preferences.getLastUpdate();
            GregorianCalendar nextUpdate =
                    new GregorianCalendar(lastUpdate[0], lastUpdate[1],
                            lastUpdate[2]);
            nextUpdate.add(Calendar.DAY_OF_YEAR, 7);
            if (now.compareTo(nextUpdate) >= 0) {
                // update needed
                saveUpdateTime(now);
                IRecipeProvider provider = new RecipeProvider(mContext);
                preferences.saveWeek(provider.proposeRecipes(preferences
                        .getCheckedButtons()));
                mFPAdapter.notifyDataSetChanged();
            }

            // Find textviews
            TextView sat = (TextView) v.findViewById(R.id.rcp_sat);
            TextView sun = (TextView) v.findViewById(R.id.rcp_sun);
            TextView mon = (TextView) v.findViewById(R.id.rcp_mon);
            TextView tue = (TextView) v.findViewById(R.id.rcp_tue);
            TextView wed = (TextView) v.findViewById(R.id.rcp_wed);
            TextView thu = (TextView) v.findViewById(R.id.rcp_thu);
            TextView fri = (TextView) v.findViewById(R.id.rcp_fri);

            // Set recipe names
            String[] week = preferences.getWeek();
            sat.setText(week[0]);
            sun.setText(week[1]);
            mon.setText(week[2]);
            tue.setText(week[3]);
            wed.setText(week[4]);
            thu.setText(week[5]);
            fri.setText(week[6]);

            // Set listeners
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String recipe = (String) ((TextView) view).getText();
                    // If the recipe-name is empty, don't start the preparation
                    // activity
                    if (!recipe.equals("")) {
                        startActivity(buildIntent(recipe));
                    }
                }
            };
            sat.setOnClickListener(listener);
            sun.setOnClickListener(listener);
            mon.setOnClickListener(listener);
            tue.setOnClickListener(listener);
            wed.setOnClickListener(listener);
            thu.setOnClickListener(listener);
            fri.setOnClickListener(listener);

            // refresh buttons
            ((ImageButton) v.findViewById(R.id.rcp_btn_sat))
                    .setOnClickListener(getNewRefreshListener(0));
            ((ImageButton) v.findViewById(R.id.rcp_btn_sun))
                    .setOnClickListener(getNewRefreshListener(1));
            ((ImageButton) v.findViewById(R.id.rcp_btn_mon))
                    .setOnClickListener(getNewRefreshListener(2));
            ((ImageButton) v.findViewById(R.id.rcp_btn_tue))
                    .setOnClickListener(getNewRefreshListener(3));
            ((ImageButton) v.findViewById(R.id.rcp_btn_wed))
                    .setOnClickListener(getNewRefreshListener(4));
            ((ImageButton) v.findViewById(R.id.rcp_btn_thu))
                    .setOnClickListener(getNewRefreshListener(5));
            ((ImageButton) v.findViewById(R.id.rcp_btn_fri))
                    .setOnClickListener(getNewRefreshListener(6));

            return v;
        }

        private OnClickListener getNewRefreshListener(final int day) {
            return new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String week[] = preferences.getWeek();
                    // find effort preference for the given day
                    int[] prefs = { preferences.getCheckedButtons()[day] };
                    IRecipeProvider provider = new RecipeProvider(mContext);
                    String recipe = provider.proposeRecipes(prefs)[0];
                    week[day] = recipe;
                    preferences.saveWeek(week);
                    mFPAdapter.notifyDataSetChanged();
                }
            };
        }

        /**
         * Returns an intent to start a preperation activity with the given
         * recipe name
         */
        private Intent buildIntent(String name) {
            Intent mIntent = new Intent(mContext, PreperationActivity.class);
            mIntent.putExtra(DashboardActivity.ACTIONBAR_TITLE, name);
            mIntent.putExtra(DashboardActivity.ACTIONBAR_ICON,
                             DashboardActivity.iconRecipes);
            return mIntent;
        }
    }

    /**
     * This class handles user preferences for computing the diet plan
     * 
     * @author joe
     * 
     */
    protected class PlannerFragment extends Fragment {
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
