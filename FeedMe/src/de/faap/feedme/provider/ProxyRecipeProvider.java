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
}
