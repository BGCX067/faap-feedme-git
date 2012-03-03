package de.faap.feedme.provider;

import de.faap.feedme.util.*;

public interface IRecipeProvider {
    /**
     * Returns a Recipe object of the given name
     * 
     */
    public Recipe getRecipe(String name);

    /**
     * Returns an array of random recipe-names with respect to the given effort
     * preferences
     * 
     */
    public String[] proposeRecipes(int[] prefs);
}
