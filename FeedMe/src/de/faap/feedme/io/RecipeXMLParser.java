package de.faap.feedme.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

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

    HashSet<String> recipeNames = new HashSet<String>();

    private ParseStates state = ParseStates.BEGIN;

    private enum ParseStates {
	BEGIN, IN_RECIPES, IR_BEGIN_NAME_NEXT, IR_TYPES, IR_CUISINES, IR_PREPARATION, IR_INGREDIENTS, II_NAME, II_AMOUNT, II_END, IN_INNERMOST, END
    }

    enum ValidTags {
	recipes, recipe, name, type, preparation, cuisine, ingredient, amount

    }

    boolean openingTagAllowed = true; // to check if elements are illegally
				      // nested

    boolean hadType = false; // additional flag to check if at least one type
			     // has been given

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
	    // factory.setValidating(true); //currently not supported
	    XmlPullParser pullParser = factory.newPullParser();
	    pullParser.setInput(input, "UTF-8");
	    int eventType = pullParser.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		checkXMLSchemaConform(pullParser);
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    System.out.println("Start: " + pullParser.getName());
		    ValidTags tag = ValidTags.valueOf(pullParser.getName());
		    switch (tag) {
		    case recipes:
			break;
		    case recipe:
			Recipe parsedRecipe = parseRecipe(pullParser);
			addRecipe(parsedRecipe);
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

    private void addRecipe(Recipe parsedRecipe) {
	// TODO Auto-generated method stub

    }

    /**
     * This method checks if the current tag would be allowed if the xml were
     * evaluated against the recipe.xsd schema file. It also checks if the data
     * type is valid. This method hopefully becomes obsolete soon, when a
     * validating parser becomes available for android. It might also be
     * obsolete when the validation is guaranteed elsewhere.
     * 
     * @param parser
     * @throws IllegalArgumentException
     * @throws XmlPullParserException
     */
    private void checkXMLSchemaConform(XmlPullParser pullParser)
	    throws IllegalArgumentException, XmlPullParserException {
	ValidTags tag = ValidTags.valueOf(pullParser.getName());
	if (pullParser.getEventType() == XmlPullParser.START_TAG) {
	    if (!openingTagAllowed) {
		throw getStandardParseException(pullParser);
	    }
	    switch (tag) {
	    case recipes:
		if (!state.equals(ParseStates.BEGIN)) {
		    throw getStandardParseException(pullParser);
		}
		state = ParseStates.IN_RECIPES;
		break;
	    case recipe:
		if (!state.equals(ParseStates.IN_RECIPES)) {
		    throw getStandardParseException(pullParser);
		}
		if (pullParser.getAttributeCount() != 1
			|| !pullParser.getAttributeName(0).equals(
				ValidAttributes.effort)) {

		    throw new IllegalArgumentException(
			    "Each recipe needs to have an effort! Types are: "
				    + Arrays.toString(Effort.values())
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		}
		try {
		    Recipe.Effort.valueOf(pullParser.getAttributeValue(0));
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException(
			    "Illegal effort type! Types are: "
				    + Arrays.toString(Effort.values())
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		}
		state = ParseStates.IR_BEGIN_NAME_NEXT;
		break;
	    case name:
		openingTagAllowed = false;
		if (state.equals(ParseStates.IR_BEGIN_NAME_NEXT)) {
		    state = ParseStates.IR_TYPES;
		} else if (state.equals(ParseStates.II_NAME)) {
		    state = ParseStates.II_AMOUNT;
		} else {
		    throw getStandardParseException(pullParser);
		}
		break;
	    case type:
		openingTagAllowed = false;
		hadType = true;
		if (!state.equals(ParseStates.IR_TYPES)) {
		    throw getStandardParseException(pullParser);
		}
		// state stays the same, as there may be more than one types
		break;
	    case cuisine:
		openingTagAllowed = false;
		if (!(state.equals(ParseStates.IR_TYPES) || state
			.equals(ParseStates.IR_CUISINES)) || !hadType) {
		    throw getStandardParseException(pullParser);
		}
		state = ParseStates.IR_CUISINES;// wait for more cuisines
		break;
	    case preparation:
		openingTagAllowed = false;
		if (!state.equals(ParseStates.IR_PREPARATION)) {
		    throw getStandardParseException(pullParser);
		}
		state = ParseStates.IR_INGREDIENTS;
		break;
	    case ingredient:
		openingTagAllowed = true;
		if (!state.equals(ParseStates.IR_INGREDIENTS)) {
		    throw getStandardParseException(pullParser);
		}
		state = ParseStates.II_NAME;
		break;
	    case amount:
		openingTagAllowed = false;
		if (!state.equals(ParseStates.II_AMOUNT)) {
		    throw getStandardParseException(pullParser);
		}
		if (pullParser.getAttributeCount() != 1
			|| !pullParser.getAttributeName(0).equals(
				ValidAttributes.unit)) {
		    assert false; // standard value given, this should not be
				  // possible
		}
		try {
		    Ingredient.Unit.valueOf(pullParser.getAttributeValue(0));
		    Double.valueOf(pullParser.getText());
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException(
			    "The amount value needs to be a decimal value"
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException(
			    "Illegal unit type! Types are: "
				    + Arrays.toString(Ingredient.Unit.values())
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		}
		state = ParseStates.II_END;
		break;
	    }
	} else if (pullParser.getEventType() == XmlPullParser.END_TAG) {
	    openingTagAllowed = true;
	}

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

	    newRecipe = new Recipe(pullParser.getName());
	    newRecipe
		    .setEffort(Effort.valueOf(pullParser.getAttributeValue(0)));
	    pullParser.next();

	    ValidTags currentTag;
	    Ingredient currentIngredient;
	    while (!(eventType == XmlPullParser.END_TAG && pullParser.getName()
		    .equals(ValidTags.recipe))) {
		checkXMLSchemaConform(pullParser);
		currentTag = ValidTags.valueOf(pullParser.getName());
		switch (currentTag) {
		case name:
		    assert newRecipe.getName() == null; // xml schema validation
							// guarantuees this
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
		    break;
		case cuisine:
		    if (!newRecipe.addCuisine(pullParser.getText())) {
			throw new IllegalArgumentException("Duplicate cuisine "
				+ pullParser.getText() + " (Line: "
				+ pullParser.getLineNumber() + ").");
		    }
		    break;
		case type:
		    if (!newRecipe.addType(pullParser.getText())) {
			throw new IllegalArgumentException("Duplicate type "
				+ pullParser.getText() + " (Line: "
				+ pullParser.getLineNumber() + ").");
		    }
		    break;
		case preparation:
		    assert newRecipe.getPreparation() == null; // "xml schema validation"
							       // guarantees
							       // this
		    newRecipe.setPreparation(pullParser.getText());
		    break;
		}

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

    private Ingredient parseIngredient(XmlPullParser pullParser)
	    throws XmlPullParserException {
	int eventType = pullParser.getEventType();
	assert pullParser.getName().equals(ValidTags.ingredient);
	String name;
	Ingredient.Unit unit;
	double amount;

	while (!(eventType == XmlPullParser.END_TAG && pullParser.getName()
		.equals(ValidTags.recipe))) {
	    checkXMLSchemaConform(pullParser);
	    if (eventType == XmlPullParser.START_TAG) {
		ValidTags tag = ValidTags.valueOf(pullParser.getName());
		switch (tag) {
		case name:
		    // FIXME: continue here
		}
	    }

	}
	return null; // FIXME: rem
    }

    private boolean recipeIsComplete(Recipe newRecipe) {
	return (newRecipe.getIngredients().length >= 1
		&& newRecipe.getName() != null
		&& newRecipe.getName().length() >= 1
		&& newRecipe.getPortions() != -1 && newRecipe.getPreparation() != null);
    }
}
