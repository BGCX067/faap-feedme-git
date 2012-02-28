package de.faap.feedme.provider;

import android.content.*;
import android.database.sqlite.*;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
    public enum Tables {
        Recipes,
        Ingredients,
        Type,
        Effort,
        Cuisine,
        One_takes,
        Categories
    }

    private static final String DATABASE_NAME = "Recipe_database";
    private static final int DATABASE_VERSION = 15;

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.Effort.toString() + " ("
                + "_id INTEGER PRIMARY KEY," + "name TEXT" + ");");

        db.execSQL("CREATE TABLE " + Tables.Cuisine.toString() + " ("
                + "_id INTEGER PRIMARY KEY," + "name TEXT" + ");");

        db.execSQL("CREATE TABLE " + Tables.Recipes.toString() + " ("
                + "_id INTEGER PRIMARY KEY," + "name TEXT,"
                + "preparation TEXT," + "effort INTEGER," + "cuisine INTEGER,"
                + "portions INTEGER," + "FOREIGN KEY (effort) REFERENCES "
                + Tables.Effort.toString() + "(_id),"
                + "FOREIGN KEY (cuisine) REFERENCES "
                + Tables.Cuisine.toString() + "(_id)" + ");");

        db.execSQL("CREATE TABLE " + Tables.Ingredients.toString() + " ("
                + "_id INTEGER PRIMARY KEY," + "name TEXT," + "unit INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + Tables.Type.toString() + " ("
                + "_id INTEGER PRIMARY KEY," + "name TEXT" + ");");

        db.execSQL("CREATE TABLE " + Tables.One_takes.toString() + " ("
                + "recipe INTEGER," + "ingredient INTEGER," + "amount FLOAT,"
                + "FOREIGN KEY (recipe) REFERENCES "
                + Tables.Recipes.toString() + "(_id),"
                + "FOREIGN KEY (ingredient) REFERENCES "
                + Tables.Ingredients.toString() + "(_id),"
                + "PRIMARY KEY (recipe, ingredient)" + ");");

        db.execSQL("CREATE TABLE " + Tables.Categories.toString() + " ("
                + "recipe INTEGER," + "type INTEGER,"
                + "FOREIGN KEY (recipe) REFERENCES "
                + Tables.Recipes.toString() + "(_id),"
                + "FOREIGN KEY (type) REFERENCES " + Tables.Type.toString()
                + "(_id)," + "PRIMARY KEY (recipe, type)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Recipes.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Ingredients.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Type.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Effort.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Cuisine.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.One_takes.toString());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Categories.toString());
        onCreate(db);
    }

}
