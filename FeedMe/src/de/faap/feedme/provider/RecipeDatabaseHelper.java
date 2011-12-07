package de.faap.feedme.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_RECIPES = "Recipes";
    public static final String TABLE_INGREDIENTS = "Ingredients";
    public static final String TABLE_TYPE = "Type";
    public static final String TABLE_EFFORT = "Effort";
    public static final String TABLE_CUISINE = "Cuisine";
    public static final String TABLE_ONETAKES = "One_takes";
    public static final String TABLE_CATEGORIES = "Categories";

    private static final String DATABASE_NAME = "Recipe_database";
    private static final int DATABASE_VERSION = 1;

    public RecipeDatabaseHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL("CREATE TABLE " + TABLE_EFFORT + " ("
		+ "_id INTEGER PRIMARY KEY," 
		+ "name TEXT" 
		+ ");");
	
	db.execSQL("CREATE TABLE " + TABLE_CUISINE + " ("
		+ "_id INTEGER PRIMARY KEY," 
		+ "name TEXT" 
		+ ");");
	
	db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
		+ "_id INTEGER PRIMARY KEY," 
		+ "name TEXT,"
		+ "preperation TEXT," 
		+ "effort INTEGER,"
		+ "cuisine INTEGER," 
		+ "portion INTEGER,"
		+ "FOREIGN KEY (effort) REFERENCES " + TABLE_EFFORT + "(_id),"
		+ "FOREIGN KEY (cuisine) REFERENCES " + TABLE_CUISINE + "(_id)"
		+ ");");
	
	db.execSQL("CREATE TABLE " + TABLE_INGREDIENTS + " ("
		+ "_id INTEGER PRIMARY KEY," 
		+ "name TEXT,"
		+ "unit TEXT" 
		+ ");");
	
	db.execSQL("CREATE TABLE " + TABLE_TYPE + " ("
		+ "_id INTEGER PRIMARY KEY," 
		+ "name TEXT" 
		+ ");");

	db.execSQL("CREATE TABLE " + TABLE_ONETAKES + " ("
		+ "name INTEGER,"
		+ "ingredient INTEGER,"
		+ "quantitiy INTEGER," 
		+ "FOREIGN KEY (name) REFERENCES " + TABLE_RECIPES + "(_id),"
		+ "FOREIGN KEY (ingredient) REFERENCES " + TABLE_INGREDIENTS + "(_id),"
		+ "PRIMARY KEY (name, ingredient)"
		+ ");");
	
	db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " ("
		+ "name INTEGER," 
		+ "type INTEGER,"
		+ "FOREIGN KEY (name) REFERENCES " + TABLE_RECIPES + "(_id),"
		+ "FOREIGN KEY (type) REFERENCES " + TABLE_TYPE + "(_id),"
		+ "PRIMARY KEY (name, type)"
		+ ");");
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
