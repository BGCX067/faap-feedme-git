package de.faap.feedme.util;

import android.content.*;
import android.content.SharedPreferences.Editor;

public class Preferences implements IPreferences {

    private static final String PREFS = "de.faap.FeedMe_preferences";

    private SharedPreferences preferences;
    private Editor editor;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences(PREFS, 0);
        editor = preferences.edit();
    }

    @Override
    public int[] getCheckedButtons() {
        int[] checkedButtons = new int[7];
        for (int i = 0; i < checkedButtons.length; i++) {
            checkedButtons[i] = preferences.getInt("radioGroup" + i, -1);
        }
        return checkedButtons;
    }

    @Override
    public void saveCheckedButtons(int[] checkedButtons) {
        for (int i = 0; i < checkedButtons.length; i++) {
            editor.putInt("radioGroup" + i, checkedButtons[i]);
        }
        editor.commit();
    }

    @Override
    public String[] getWeek() {
        String[] week = new String[7];
        for (int i = 0; i < week.length; i++) {
            week[i] = preferences.getString("week" + i, "");
        }
        return week;
    }

    @Override
    public void saveWeek(String[] week) {
        for (int i = 0; i < week.length; i++) {
            editor.putString("week" + i, week[i]);
        }
        editor.commit();
    }

    @Override
    public int[] getLastUpdate() {
        int[] lastUpdate = new int[3];
        lastUpdate[0] = preferences.getInt("upd_year", 1971);
        lastUpdate[1] = preferences.getInt("upd_month", 1);
        lastUpdate[2] = preferences.getInt("upd_day", 1);
        return lastUpdate;
    }

    @Override
    public void saveLastUpdate(int year, int month, int day) {
        editor.putInt("upd_year", year);
        editor.putInt("upd_month", month);
        editor.putInt("upd_day", day);
        editor.commit();
    }

}
