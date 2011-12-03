package de.faap.feedme.provider;

import android.content.ContentValues;

public interface IRecipeReceiver {

    public void open();

    public void addTable(String tableName, ContentValues values);

    public void close();
}
