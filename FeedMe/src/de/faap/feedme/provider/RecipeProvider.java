package de.faap.feedme.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.faap.feedme.util.Recipe;

public class RecipeProvider implements IRecipeProvider {

    private Context mContext;

    public RecipeProvider(Context context) {
	mContext = context;
    }

    @Override
    public Recipe getRecipe(String name) {
	int portions;
	double[] quantities;
	String[] units;
	String[] ingredients;
	String preperation;
	
	int _id;

	SQLiteDatabase db = new RecipeDatabaseHelper(mContext)
		.getReadableDatabase();
	
	// query to get _id, preperation and portions
	Cursor simpleRecipeData = db.rawQuery(
		"SELECT _id,preperation,portion " + 
		"FROM " + RecipeDatabaseHelper.TABLE_RECIPES + " " +
		"WHERE name = ? ",
		new String[] {name});
	
	_id = simpleRecipeData.getInt(0);
	preperation = simpleRecipeData.getString(1);
	portions = simpleRecipeData.getInt(2);
	simpleRecipeData.close();
	
	// query to get quantities, units and ingredients
	Cursor complexRecipeData = db.rawQuery(
		"SELECT temp.quantity,Ingredients.name,Ingredients.unit " + 
		"FROM (SELECT quantitiy,ingredient " +
			"FROM One_takes " + 
			"WHERE name = " + _id + ") as temp " +
			"INNER JOIN Ingredients on temp.ingredient = Ingredients._id "
			, null);
	
	int length = complexRecipeData.getCount();
	quantities = new double[length];
	units = new String[length];
	ingredients = new String[length];
	
	for (int i = 0; complexRecipeData.moveToNext(); i++) {
	    quantities[i] = complexRecipeData.getDouble(0);
	    units[i] = complexRecipeData.getString(1);
	    ingredients[i] = complexRecipeData.getString(2);
	}

	db.close();
	return new Recipe(name, portions, quantities, units, ingredients, preperation);
    }
}
