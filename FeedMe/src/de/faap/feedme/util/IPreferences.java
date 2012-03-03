package de.faap.feedme.util;

public interface IPreferences {
    public int[] getCheckedButtons();

    public void saveCheckedButtons(int[] checkedButtons);

    public String[] getWeek();

    public void saveWeek(String[] week);

    public int[] getLastUpdate();

    public void saveLastUpdate(int year, int month, int day);
}
