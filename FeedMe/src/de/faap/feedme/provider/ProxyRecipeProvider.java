package de.faap.feedme.provider;

import java.util.*;
import android.content.*;
import de.faap.feedme.util.*;

public class ProxyRecipeProvider implements IRecipeProvider {

    private static final ProxyRecipeProvider instance =
            new ProxyRecipeProvider();

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
        }
        // TODO collapse
        IRecipeProvider provider = new RecipeProvider(mContext);
        Recipe recipe = provider.getRecipe(name);
        return recipe;
    }

    @Override
    public String[] getNewWeek(int[] prefs) {
        return new RecipeProvider(mContext).getNewWeek(prefs);
    }
}
