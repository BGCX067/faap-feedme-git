package de.faap.feedme.util;

public class Recipe {
    private String name;
    private int portions;
    private double[] quantities;
    private String[] units;
    private String[] ingredients;
    private String preperation;

    public Recipe(String name, int portions, double[] quantities,
	    String[] units, String[] ingredients, String preperation) {
	this.name = name;
	this.portions = portions;
	this.quantities = quantities;
	this.units = units;
	this.ingredients = ingredients;
	this.preperation = preperation;
    }

    public String getName() {
	return name;
    }

    public int getPortions() {
	return portions;
    }

    public double[] getQuantities() {
	return quantities;
    }

    public String[] getUnits() {
	return units;
    }

    public String[] getIngredients() {
	return ingredients;
    }

    public String getPreperation() {
	return preperation;
    }

    public void changePortions(int newPortions) {
	double quotient = newPortions / portions;
	portions = newPortions;
	for (double d : quantities) {
	    d = d * quotient;
	}
    }

}
