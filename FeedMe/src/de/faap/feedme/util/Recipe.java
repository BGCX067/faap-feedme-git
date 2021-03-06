package de.faap.feedme.util;

import java.util.*;
import de.faap.feedme.util.Ingredient.Unit;

public class Recipe {
    public enum Effort {
        small,
        large,
        instant
    }

    private String name;
    private int portions;
    private String preparation;
    private Effort effort;
    private HashSet<Ingredient> ingredients = new HashSet<Ingredient>();
    private String cuisine = null;
    private HashSet<String> categories = null;

    public Recipe(String name, int portions, double[] quantities, Unit[] units,
            String[] ingredientNames, String preperation) {
        this.name = name;
        this.portions = portions;
        assert quantities.length == units.length
                && quantities.length == ingredientNames.length;
        for (int i = 0; i < quantities.length; i++) {
            Ingredient ingredient =
                    new Ingredient(quantities[i], units[i], ingredientNames[i]);
            this.ingredients.add(ingredient);
        }
        this.preparation = preperation;
    }

    public Recipe(String name) {
        this.name = name;
    }

    public void setEffort(Effort effort) {
        this.effort = effort;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    /**
     * Convenience method.
     * 
     * @see #addIngredient(Ingredient)
     * 
     * @param ingredient
     * @param unit
     */
    public boolean addIngredient(double quantity, Unit unit,
            String ingredientName) {
        Ingredient newIngredient =
                new Ingredient(quantity, unit, ingredientName);
        return addIngredient(newIngredient);
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCuisine() {
        return cuisine;
    }

    public boolean addType(String type) {
        if (categories == null) {
            categories = new HashSet<String>();
        }

        if (categories.contains(type))
            return false;

        categories.add(type);
        return true;
    }

    public String[] getCategories() {
        if (categories == null)
            return null;
        return categories.toArray(new String[categories.size()]);
    }

    public Effort getEffort() {
        return effort;
    }

    public String getName() {
        return name;
    }

    public int getPortions() {
        return portions;
    }

    public String getPreparation() {
        return preparation;
    }

    public void changePortions(int newPortions) {
        double quotient = (double) newPortions / (double) portions;
        portions = newPortions;
        for (Ingredient ingredient : ingredients) {
            ingredient.adaptQuantity(quotient);
        }
    }

    public Ingredient[] getIngredients() {
        return ingredients.toArray(new Ingredient[ingredients.size()]);
    }

    /**
     * Adds a new ingredient to the recipe.
     * 
     * @param newIngredient
     * @return true if the ingredient did not yet exist, false otherwise.
     */
    public boolean addIngredient(Ingredient newIngredient) {
        if (ingredients.contains(newIngredient))
            return false;

        ingredients.add(newIngredient);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder recipeString = new StringBuilder();

        recipeString.append(name + "(" + portions + "P)\n");
        recipeString.append("types: ");
        for (String cat : getCategories()) {
            recipeString.append(cat + ",");
        }
        recipeString.deleteCharAt(recipeString.length() - 1); // remove ','
        recipeString.append("\n");
        recipeString.append("Cuisine: ");
        recipeString.append(cuisine + "\n");

        recipeString.append("Ingredients:\n");
        for (Ingredient ing : getIngredients()) {
            recipeString.append(ing + "\n");
        }
        recipeString.append("Preparation:\n");
        recipeString.append(getPreparation() + "\n");
        return recipeString.toString();
    }
}
