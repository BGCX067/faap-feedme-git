package de.faap.feedme.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Recipe_database";
    private static final int DATABASE_VERSION = 1;
    // table names
    private static final String TABLE_RECIPES = "Recipes";
    private static final String TABLE_INGREDIENTS = "Ingredients";
    private static final String TABLE_TYPE = "Type";
    private static final String TABLE_EFFORT = "Effort";
    private static final String TABLE_CUISINE = "Cuisine";
    private static final String TABLE_ONETAKES = "One_takes";
    private static final String TABLE_CATEGORIES = "Categories";

    public RecipeDatabase(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
		+ "_id INTEGER PRIMARY KEY," + "name TEXT NOT NULL,"
		+ "preperation TEXT NOT NULL," + "effort INTEGER NOT NULL,"
		+ "cuisine INTEGER NOT NULL," + "portion INTEGER NOT NULL"
		+ ";");
	db.execSQL("CREATE TABLE " + TABLE_INGREDIENTS + " ("
		+ "_id INTEGER PRIMARY KEY," + "name TEXT NOT NULL,"
		+ "unit TEXT NOT NULL" + ";");
	db.execSQL("CREATE TABLE " + TABLE_TYPE + " ("
		+ "_id INTEGER PRIMARY KEY," + "name TEXT NOT NULL" + ";");
	db.execSQL("CREATE TABLE " + TABLE_EFFORT + " ("
		+ "_id INTEGER PRIMARY KEY," + "name TEXT NOT NULL" + ";");
	db.execSQL("CREATE TABLE " + TABLE_CUISINE + " ("
		+ "_id INTEGER PRIMARY KEY," + "name TEXT NOT NULL" + ";");
	db.execSQL("CREATE TABLE " + TABLE_ONETAKES + " ("
		+ "name INTEGER PRIMARY KEY,"
		+ "ingredient INTEGER PRIMARY KEY,"
		+ "quantitiy INTEGER NOT NULL" + ";");
	db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " ("
		+ "name INTEGER PRIMARY KEY," + "type INTEGER PRIMARY KEY"
		+ ";");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_RECIPES);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_INGREDIENTS);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_TYPE);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_EFFORT);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_CUISINE);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_ONETAKES);
	db.execSQL("DROP TABLE IF EXISTS" + TABLE_CATEGORIES);
	onCreate(db);
    }

}
