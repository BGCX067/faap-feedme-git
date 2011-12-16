package de.faap.feedme.provider;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import de.faap.feedme.util.Recipe;

/**
 * Singleton pattern.
 * 
 * @author joe
 * 
 */
public class ProxyRecipeProvider implements IRecipeProvider {

    private static final ProxyRecipeProvider instance = new ProxyRecipeProvider();

    private static Context mContext;

    private Map<String, Recipe> map;

    private ProxyRecipeProvider() {
	map = new HashMap<String, Recipe>();
	// TODO mock sachen entfernen
	int portions = 2;
	double[] quantities = { 1, 2, 500 };
	String[] units = { "", "l", "g" };
	String[] ingredients = { "Ei", "Wasser", "Mehl" };
	String preperation = "Zusammenmischen, 10min in Pfanne, namnamnam";

	Recipe r0 = new Recipe("0", portions, quantities, units, ingredients,
		preperation);
	Recipe r1 = new Recipe("1", portions, quantities, units, ingredients,
		preperation);
	Recipe r2 = new Recipe("2", portions, quantities, units, ingredients,
		preperation);

	map.put("0", r0);
	map.put("1", r1);
	map.put("2", r2);
    }

    public static ProxyRecipeProvider getInstance(Context context) {
	mContext = context;
	return instance;
    }

    @Override
    public Recipe getRecipe(String name) {
	if (map.containsKey(name)) {
	    return map.get(name);
	} else {
	    return new RecipeProvider(mContext).getRecipe(name);
	}
    }

    @Override
    public String[] getNewWeek(int[] prefs) {
	return new RecipeProvider(mContext).getNewWeek(prefs);
    }
}
