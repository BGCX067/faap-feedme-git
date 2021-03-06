package de.faap.feedme.ui;

import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import com.example.android.actionbarcompat.*;
import de.faap.feedme.*;
import de.faap.feedme.io.*;

public class DashboardActivity extends ActionBarActivity {

    public static final String ACTIONBAR_TITLE = "title";
    public static final String ACTIONBAR_ICON = "icon";
    public static final int iconPlan = R.drawable.ic_action_planer;
    public static final int iconRecipes = R.drawable.ic_action_recipes;

    protected Context context;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        }
        setContentView(R.layout.dashboard);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setProgressBarIndeterminateVisibility(false);
        }
        this.context = this;

        findViewById(R.id.dashboard_btn_plan)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent =
                                new Intent(getApplicationContext(),
                                        PlanActivity.class);
                        mIntent.putExtra(ACTIONBAR_TITLE, getResources()
                                .getString(R.string.title_plan));
                        mIntent.putExtra(ACTIONBAR_ICON, iconPlan);
                        startActivity(mIntent);
                    }
                });

        findViewById(R.id.dashboard_btn_recipes)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent =
                                new Intent(getApplicationContext(),
                                        RecipesActivity.class);
                        mIntent.putExtra(ACTIONBAR_TITLE, getResources()
                                .getString(R.string.title_recipes));
                        mIntent.putExtra(ACTIONBAR_ICON, iconRecipes);
                        startActivity(mIntent);
                    }
                });

        findViewById(R.id.dashboard_btn_update)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IUpdateDatabase updater = new DatabaseUpdater(context);
                        if (!updater.isUpToDate()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                setProgressBarIndeterminateVisibility(true);
                            } else {
                                getActionBarHelper().setProgressBarState(true);
                            }
                            updater.update();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                setProgressBarIndeterminateVisibility(false);
                            } else {
                                getActionBarHelper().setProgressBarState(false);
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);
        // Calling super after populating the menu is necessary here to ensure
        // that the action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }
}
