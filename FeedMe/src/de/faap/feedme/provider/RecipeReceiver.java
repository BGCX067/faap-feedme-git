package de.faap.feedme.provider;

import java.util.Collection;

import android.database.sqlite.SQLiteDatabase;

public class RecipeReceiver implements IRecipeReceiver {

    private RecipeDatabase openHelper;
    private SQLiteDatabase recipeDatabase;

    public RecipeReceiver(RecipeDatabase rd) {
	openHelper = rd;
    }

    @Override
    public void addTable(String tableName, Collection<String[]> tableContents) {
	// TODO add tables
    }

    @Override
    public void open() {
	recipeDatabase = openHelper.getWritableDatabase();
    }

    @Override
    public void close() {
	recipeDatabase.close();
    }

}
