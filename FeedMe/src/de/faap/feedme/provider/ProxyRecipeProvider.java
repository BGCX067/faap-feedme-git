package de.faap.feedme.provider;

import java.util.HashMap;
import java.util.Map;

import de.faap.feedme.util.Recipe;

/**
 * Singleton pattern.
 * 
 * @author joe
 * 
 */
public class ProxyRecipeProvider implements IRecipeProvider {

    private static final ProxyRecipeProvider instance = new ProxyRecipeProvider();

    private RecipeProvider rp;
    private Map<String, Recipe> map;

    private ProxyRecipeProvider() {
	rp = new RecipeProvider();
	map = new HashMap<String, Recipe>();
    }

    public static ProxyRecipeProvider getInstance() {
	return instance;
    }

    @Override
    public Recipe getRecipe(String name) {
	if (map.containsKey(name)) {
	    return map.get(name);
	} else {
	    return rp.getRecipe(name);
	}
    }
}
