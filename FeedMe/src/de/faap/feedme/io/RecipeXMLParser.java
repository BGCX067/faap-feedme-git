package de.faap.feedme.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.util.Log;
import de.faap.feedme.util.Ingredient;
import de.faap.feedme.util.Recipe;
import de.faap.feedme.util.Recipe.Effort;

public class RecipeXMLParser {
    private static String FILE_PATH = "/mnt/sdcard/recipes.xml";
    private static String SCHEMA_PATH = "/mnt/sdcard/recipe.xsd";

    private static String LOG_TAG = "faap.feedme.xmlparse";

    private ParseStates state = ParseStates.BEGIN;

    private enum ParseStates {
	BEGIN, IN_RECIPES, END
    }

    enum ValidTags {
	recipes, recipe, name, type, preparation, cuisine, ingredient

    }

    enum ValidAttributes {
	effort, unit
    }

    public boolean reparseRecipeDatabase() {
	return parseXMLFile(FILE_PATH);
    }

    public ContentValues getValuesForTable(String table) {
	return null;
    }

    private boolean parseXMLFile(String name) {

	InputStream in;
	File inFile;
	try {
	    inFile = new File(name);
	    in = new FileInputStream(inFile);
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}

	return parseStream(in);
    }

    private boolean parseStream(InputStream input) {

	XmlPullParserFactory factory;
	try {
	    factory = XmlPullParserFactory.newInstance();
	    factory.setValidating(true);
	    XmlPullParser pullParser = factory.newPullParser();
	    pullParser.setInput(input, "UTF-8");
	    int eventType = pullParser.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    System.out.println("Start: " + pullParser.getName());
		    ValidTags tag = ValidTags.valueOf(pullParser.getName());
		    switch (tag) {
		    case recipes:
			if (!state.equals(ParseStates.BEGIN)) {
			    throw getStandardParseException(pullParser);
			}
		    case recipe:
			if (!state.equals(ParseStates.IN_RECIPES)) {
			    throw getStandardParseException(pullParser);
			}
			parseRecipe(pullParser);
			break;
		    default:
			throw getStandardParseException(pullParser);
		    }
		    break;
		case XmlPullParser.END_TAG:
		    System.out.println("End: " + pullParser.getName());
		    break;
		}
		pullParser.next();
	    }
	} catch (IllegalArgumentException e) {
	    Log.d(LOG_TAG,
		    "Illegal XML Tag in recipe: " + e.getLocalizedMessage());
	} catch (XmlPullParserException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return true;
    }

    private IllegalArgumentException getStandardParseException(
	    XmlPullParser pullParser) {
	return new IllegalArgumentException(
		"You need to keep to the xml-schema-file. The tag "
			+ pullParser.getName() + "is illegal here (Line: "
			+ pullParser.getLineNumber() + ").");
    }

    /**
     * Parses a single recipe from the xml file.
     * 
     * @param pullParser
     *            The pull parser used for xml parsing. Must be pointing at
     *            opening recipe-tag.
     * @throws IllegalArgumentException
     *             If the recipe does not have the correct recipe format.
     */
    private Recipe parseRecipe(XmlPullParser pullParser)
	    throws IllegalArgumentException {
	Recipe newRecipe = null;
	try {
	    int eventType = pullParser.getEventType();
	    assert eventType == XmlPullParser.START_TAG
		    && pullParser.getName().equals(ValidTags.recipe);
	    if (pullParser.getAttributeCount() != 1
		    || !pullParser.getAttributeName(0).equals(
			    ValidAttributes.effort)) {
		throw new IllegalArgumentException(
			"Each recipe needs to have an effort! Types are: "
				+ Arrays.toString(Effort.values()));
	    }

	    newRecipe = new Recipe(pullParser.getName());
	    newRecipe
		    .setEffort(Effort.valueOf(pullParser.getAttributeValue(0)));
	    pullParser.next();

	    ValidTags currentTag;
	    Ingredient currentIngredient;
	    while (!(eventType == XmlPullParser.END_TAG && pullParser.getName()
		    .equals(ValidTags.recipe))) {
		currentTag = ValidTags.valueOf(pullParser.getName());
		switch (currentTag) {
		case name:
		    if (newRecipe.getName() != null) {
			throw getStandardParseException(pullParser);
		    }
		    newRecipe.setName(pullParser.getText());
		    break;
		case ingredient:
		    currentIngredient = parseIngredient(pullParser);
		    if (!newRecipe.addIngredient(currentIngredient)) {
			throw new IllegalArgumentException(
				"Duplicate ingredient "
					+ currentIngredient.name + " (Line: "
					+ pullParser.getLineNumber() + ").");
		    }
		}
		// FIXME: complete parsing
	    }

	    // reached end tag, check if everything necessary was there
	    if (!recipeIsComplete(newRecipe)) {
		throw new IllegalArgumentException(
			"A recipe needs all elements as specified in the .xsd file.");
	    }
	} catch (XmlPullParserException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return newRecipe;
    }

    private Ingredient parseIngredient(XmlPullParser pullParser) {
	// FIXME: implement
	return null;

    }

    private boolean recipeIsComplete(Recipe newRecipe) {
	return (newRecipe.getIngredients().length >= 1
		&& newRecipe.getName() != null
		&& newRecipe.getName().length() >= 1
		&& newRecipe.getPortions() != -1 && newRecipe.getPreperation() != null);
    }
}
