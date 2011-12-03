package de.faap.feedme.provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class RecipeReceiver implements IRecipeReceiver {

    private RecipeDatabase openHelper;
    private SQLiteDatabase db;

    public RecipeReceiver(RecipeDatabase rd) {
	openHelper = rd;
    }

    @Override
    public void open() {
	db = openHelper.getWritableDatabase();
    }

    @Override
    public void addTable(String tableName, ContentValues values) {
	if (db.isOpen()) {
	    db.insert(tableName, null, values);
	}
    }

    @Override
    public void close() {
	db.close();
    }

}
