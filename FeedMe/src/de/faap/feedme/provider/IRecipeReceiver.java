package de.faap.feedme.provider;

import android.content.*;

public interface IRecipeReceiver {

    public void open();

    public void close();

    void addTable(String tableName, ContentValues[] values);
}
