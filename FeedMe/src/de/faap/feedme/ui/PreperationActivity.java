package de.faap.feedme.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.example.android.actionbarcompat.*;
import com.viewpagerindicator.*;
import de.faap.feedme.R;
import de.faap.feedme.provider.*;
import de.faap.feedme.util.*;

public class PreperationActivity extends ActionBarActivity {
    protected static final int NUM_ITEMS = 2;

    protected static Context mContext;
    protected static Recipe recipe;

    private IRecipeProvider db;
    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preperation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar
                    .setIcon(getResources().getDrawable(R.drawable.ic_recipes));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = getApplicationContext();
        db = ProxyRecipeProvider.getInstance(mContext);
        Bundle bundle = getIntent().getExtras();
        String recipeName = bundle.getString(DashboardActivity.ACTIONBAR_TITLE);
        this.setTitle(recipeName);
        recipe = db.getRecipe(recipeName);

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

    private class mFPAdapter extends FragmentPagerAdapter implements
            TitleProvider {

        public mFPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new IngredientsFragment();
            } else if (position == 1) {
                return new PreperationFragment();
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
                return mContext.getResources()
                        .getString(R.string.ind_ingredients);
            } else if (position == 1) {
                return mContext.getResources()
                        .getString(R.string.ind_preperation);
            } else {
                return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    protected class IngredientsFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.ingredients, container, false);

            // implement button to change portions
            Button ingredientsButton =
                    (Button) v.findViewById(R.id.ingredients_btn);
            ingredientsButton.setText(Integer.toString(recipe.getPortions()));
            ingredientsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft =
                            getFragmentManager().beginTransaction();
                    DialogFragment df = new PortionPickerDialogFragment();
                    df.show(ft, "dialog");
                }
            });

            Ingredient[] ingredients = recipe.getIngredients();
            String[] ingredientStrings = new String[ingredients.length];
            for (int i = 0; i < ingredientStrings.length; i++) {
                ingredientStrings[i] = ingredients[i].toString();
            }

            // fill list
            ListView mListView =
                    (ListView) v.findViewById(R.id.ingredients_listview);
            mListView.setAdapter(new ArrayAdapter<String>(mContext,
                    R.layout.listitem, ingredientStrings));

            return v;
        }

        /**
         * This dialog fragment handles portion changes
         */
        private class PortionPickerDialogFragment extends DialogFragment {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater,
                    ViewGroup container, Bundle savedInstanceState) {
                View v =
                        inflater.inflate(R.layout.portion_dialog, container,
                                         false);

                // set portions
                final EditText text =
                        (EditText) v.findViewById(R.id.port_dia_text);
                text.setText(Integer.toString(recipe.getPortions()));

                // implement button listeners
                ((Button) v.findViewById(R.id.port_dia_dec))
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int portions =
                                        Integer.parseInt(text.getText()
                                                .toString());
                                if (portions > 1) {
                                    text.setText(Integer.toString(portions - 1));
                                }
                            }
                        });

                ((Button) v.findViewById(R.id.port_dia_inc))
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int portions =
                                        Integer.parseInt(text.getText()
                                                .toString());
                                text.setText(Integer.toString(portions + 1));
                            }
                        });

                ((Button) v.findViewById(R.id.port_dia_ok))
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int portions =
                                        Integer.parseInt(text.getText()
                                                .toString());
                                recipe.changePortions(portions);
                                mFPAdapter.notifyDataSetChanged();
                                dismiss();
                            }
                        });

                return v;
            }
        }
    }

    protected class PreperationFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.prepdetails, container, false);
            TextView mTextView = (TextView) v.findViewById(R.id.prep_textview);
            mTextView.setText(recipe.getPreparation());
            return v;
        }
    }

}
