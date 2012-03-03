package de.faap.feedme.ui;

import java.util.*;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
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

public class RecipesActivity extends ActionBarActivity {
    protected static final int NUM_ITEMS = 3;

    protected static Context mContext;
    protected static Preferences preferences;

    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar
                    .setIcon(getResources().getDrawable(R.drawable.ic_recipes));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getResources().getString(R.string.title_recipes));

        mContext = getApplicationContext();
        preferences = new Preferences(this);

        mFPAdapter = new mFPAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.recipes_viewpager);
        mViewPager.setAdapter(mFPAdapter);
        mTPIndicator =
                (TitlePageIndicator) findViewById(R.id.recipes_indicator);
        mTPIndicator.setViewPager(mViewPager, 1);
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

    /**
     * Creates fragments according to the category and loads the according
     * recipe-lists
     * 
     * @author joe
     * 
     */
    protected static class CategoryFragment extends Fragment {

        private int pos;

        static CategoryFragment newInstance(int position) {
            CategoryFragment mCF = new CategoryFragment();

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
            ExpandableListView v =
                    (ExpandableListView) inflater
                            .inflate(R.layout.recipe_categories, container,
                                     false);

            RecipeDatabaseHelper openHelper =
                    new RecipeDatabaseHelper(mContext);
            SQLiteDatabase db = openHelper.getReadableDatabase();

            Cursor cursor;
            switch (pos) {
            case 0:
                cursor = this.queryEffort(db);
                break;

            case 1:
                cursor = this.queryType(db);
                break;

            case 2:
                cursor = this.queryCuisine(db);
                break;

            default:
                cursor = null;
                break;
            }

            ArrayList<String> categories = new ArrayList<String>();
            ArrayList<ArrayList<String>> recipes =
                    new ArrayList<ArrayList<String>>();
            if (cursor != null) {
                String category = "";
                ArrayList<String> recipesTmp = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    String curCategory = cursor.getString(0);
                    if (!category.equals(curCategory)) {
                        // new category
                        category = curCategory;
                        categories.add(category);
                        recipesTmp = new ArrayList<String>();
                        recipes.add(recipesTmp);
                    }
                    recipesTmp.add(cursor.getString(1));
                }
                cursor.close();
            }

            db.close();
            openHelper.close();
            v.setAdapter(new CategoryListAdapter(categories, recipes));

            return v;
        }

        private Cursor queryEffort(SQLiteDatabase db) {
            return db.rawQuery("SELECT "
                                       + RecipeDatabaseHelper.Tables.Effort
                                               .toString()
                                       + ".name, "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + ".name "
                                       + "FROM "
                                       + RecipeDatabaseHelper.Tables.Effort
                                               .toString()
                                       + " INNER JOIN "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + " ON "
                                       + RecipeDatabaseHelper.Tables.Effort
                                               .toString()
                                       + "._id = "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + ".effort "
                                       + "ORDER BY "
                                       + RecipeDatabaseHelper.Tables.Effort
                                               .toString() + ".name ASC", null);
        }

        private Cursor queryCuisine(SQLiteDatabase db) {
            return db.rawQuery("SELECT "
                                       + RecipeDatabaseHelper.Tables.Cuisine
                                               .toString()
                                       + ".name, "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + ".name "
                                       + "FROM "
                                       + RecipeDatabaseHelper.Tables.Cuisine
                                               .toString()
                                       + " INNER JOIN "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + " ON "
                                       + RecipeDatabaseHelper.Tables.Cuisine
                                               .toString()
                                       + "._id = "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + ".effort "
                                       + "ORDER BY "
                                       + RecipeDatabaseHelper.Tables.Cuisine
                                               .toString() + ".name ASC", null);
        }

        private Cursor queryType(SQLiteDatabase db) {
            return db.rawQuery("SELECT TypeRecipes.name, "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + ".name "
                                       + "FROM (SELECT recipe, name "
                                       + "FROM "
                                       + RecipeDatabaseHelper.Tables.Type
                                               .toString()
                                       + " INNER JOIN "
                                       + RecipeDatabaseHelper.Tables.Categories
                                               .toString()
                                       + " ON "
                                       + RecipeDatabaseHelper.Tables.Type
                                               .toString()
                                       + "._id = "
                                       + RecipeDatabaseHelper.Tables.Categories
                                               .toString()
                                       + ".type) AS TypeRecipes "
                                       + "INNER JOIN "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString()
                                       + " ON TypeRecipes.recipe = "
                                       + RecipeDatabaseHelper.Tables.Recipes
                                               .toString() + "._id "
                                       + "ORDER BY TypeRecipes.name ASC", null);
        }

        private class CategoryListAdapter extends BaseExpandableListAdapter {

            private ArrayList<String> categories;
            private ArrayList<ArrayList<String>> recipes;

            public CategoryListAdapter(ArrayList<String> categories,
                    ArrayList<ArrayList<String>> recipes) {
                this.categories = categories;
                this.recipes = recipes;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return recipes.get(groupPosition).get(childPosition);
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                TextView textView = getGenericView();
                textView.setText(recipes.get(groupPosition).get(childPosition));
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent =
                                new Intent(mContext, PreperationActivity.class);
                        mIntent.putExtra(DashboardActivity.ACTIONBAR_TITLE,
                                         ((TextView) v).getText());
                        mIntent.putExtra(DashboardActivity.ACTIONBAR_ICON,
                                         DashboardActivity.iconRecipes);
                        startActivity(mIntent);
                    }
                });
                return textView;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return recipes.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return categories.get(groupPosition);
            }

            @Override
            public int getGroupCount() {
                return categories.size();
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                TextView textView = getGenericView();
                textView.setText(categories.get(groupPosition));
                return textView;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                return true;
            }

            /**
             * Returns a new TextView which can be used as a Child- or
             * GroudView.
             */
            private TextView getGenericView() {
                AbsListView.LayoutParams lp =
                        new AbsListView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 60);

                TextView textView = new TextView(mContext);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                textView.setPadding(60, 0, 0, 0);
                return textView;
            }

        }
    }
}
