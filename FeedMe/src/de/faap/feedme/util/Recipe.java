package de.faap.feedme.util;

public class Recipe {
    // TODO mit autschi abstimmen, array, arraylist, was auch immer
    private String name;
    private String[] ingredients;
    private double[] quantities;
    private String preperation;
    private String[] types;
    private String effort;
    private String cuisine;
    private int portion;

    public Recipe(String name, String[] ingredients, double[] quantities,
	    String preperation, String[] types, String effort, String cuisine,
	    int portion) {
	this.name = name;
	this.ingredients = ingredients;
	this.quantities = quantities;
	this.preperation = preperation;
	this.types = types;
	this.effort = effort;
	this.cuisine = cuisine;
	this.portion = portion;
    }

}
