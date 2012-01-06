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

    HashSet<String> ingredientNames = new HashSet<String>();

    HashSet<String> categories = new HashSet<String>();

    HashSet<String> cuisines = new HashSet<String>();

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

	try {

	    // factory.setValidating(true); //currently not supported
	    XmlPullParser pullParser = XmlValidatingParserFactory
		    .newValidatingParser(SCHEMA_PATH);
	    pullParser.setInput(input, "UTF-8");
	    pullParser.next(); // skip start document
	    int eventType = pullParser.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		checkXMLSchemaConform(pullParser);
		eventType = pullParser.getEventType(); // here, as the
						       // check-routine might
						       // change the eventType
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    System.out.println("Start: " + pullParser.getName());
		    ValidTags tag = ValidTags.valueOf(pullParser.getName());
		    switch (tag) {
		    case recipes:
			state = ParseStates.IN_RECIPES;
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
	    return false;
	} catch (XmlPullParserException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	    return false;

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}

	return true;
    }

    private void addRecipe(Recipe parsedRecipe) {

    }

    /**
     * This method checks if the current tag would be allowed if the xml were
     * evaluated against the recipe.xsd schema file. It also checks if the data
     * type is valid. This method hopefully becomes obsolete soon, when a
     * validating parser becomes available for android. It might also be
     * obsolete when the validation is guaranteed elsewhere.
     * 
     * 
     * @param parser
     * @throws IllegalArgumentException
     * @throws XmlPullParserException
     */
    private void checkXMLSchemaConform(XmlPullParser pullParser)
	    throws IllegalArgumentException, XmlPullParserException {
	if (pullParser.getEventType() == XmlPullParser.START_TAG) {
	    ValidTags tag = ValidTags.valueOf(pullParser.getName());
	    if (!openingTagAllowed) {
		throw getStandardParseException(pullParser);
	    }
	    switch (tag) {
	    case recipes:
		if (!state.equals(ParseStates.BEGIN)) {
		    throw getStandardParseException(pullParser);
		}
		break;
	    case recipe:
		if (!state.equals(ParseStates.IN_RECIPES)) {
		    throw getStandardParseException(pullParser);
		}
		if (pullParser.getAttributeCount() != 1
			|| !pullParser.getAttributeName(0).equals(
				ValidAttributes.effort.toString())) {

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
		break;
	    case name:
		openingTagAllowed = false;
		if (!(state.equals(ParseStates.IR_BEGIN_NAME_NEXT) || state
			.equals(ParseStates.II_NAME))) {
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
		break;
	    case preparation:
		openingTagAllowed = false;
		if (!state.equals(ParseStates.IR_PREPARATION)) {
		    throw getStandardParseException(pullParser);
		}
		break;
	    case ingredient:
		openingTagAllowed = true;
		if (!state.equals(ParseStates.IR_INGREDIENTS)) {
		    throw getStandardParseException(pullParser);
		}
		break;
	    case amount:
		openingTagAllowed = false;
		if (!state.equals(ParseStates.II_AMOUNT)) {
		    throw getStandardParseException(pullParser);
		}
		if (pullParser.getAttributeCount() != 1
			|| !pullParser.getAttributeName(0).equals(
				ValidAttributes.unit.toString())) {
		    assert false; // standard value given, this should not be
				  // possible
		}
		try {
		    Ingredient.Unit.valueOf(pullParser.getAttributeValue(0));
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException(
			    "Illegal unit type! Types are: "
				    + Arrays.toString(Ingredient.Unit.values())
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		}
		break;
	    }
	} else if (pullParser.getEventType() == XmlPullParser.TEXT) {
	    if (openingTagAllowed) {
		// no real text should come now, whitespace allowed and skipped
		if (!pullParser.isWhitespace()) {
		    throw new IllegalArgumentException(
			    "Only whitespace is allowed between these tags, but was: '"
				    + pullParser.getText() + "' (Line: "
				    + pullParser.getLineNumber() + ").");

		}
		try {
		    pullParser.next();
		} catch (IOException e) {
		    Log.d(LOG_TAG,
			    "Unexpected excpetion while skipping whitespace!");
		    e.printStackTrace();
		} // skip whitespace
	    }
	    // check if text has right format
	    switch (state) {
	    case II_AMOUNT:
		try {
		    Double.valueOf(pullParser.getText());
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException(
			    "The amount value needs to be a decimal value"
				    + "(Line: " + pullParser.getLineNumber()
				    + ").");
		}
		break;
	    }
	} else if (pullParser.getEventType() == XmlPullParser.END_TAG) {
	    ValidTags tag = ValidTags.valueOf(pullParser.getName());
	    if (!tag.equals(ValidTags.amount))
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
	hadType = false;
	try {
	    int eventType = pullParser.getEventType();
	    assert eventType == XmlPullParser.START_TAG
		    && pullParser.getName().equals(ValidTags.recipe.toString());

	    newRecipe = new Recipe(pullParser.getName());
	    newRecipe
		    .setEffort(Effort.valueOf(pullParser.getAttributeValue(0)));
	    pullParser.next();
	    eventType = pullParser.getEventType();
	    state = ParseStates.IR_BEGIN_NAME_NEXT;

	    ValidTags currentTag;
	    Ingredient currentIngredient;
	    while (!(eventType == XmlPullParser.END_TAG && pullParser.getName()
		    .equals(ValidTags.recipe.toString()))) {
		checkXMLSchemaConform(pullParser);
		eventType = pullParser.getEventType(); // set here, as the check
						       // might change the type
		if (eventType == XmlPullParser.START_TAG) {
		    currentTag = ValidTags.valueOf(pullParser.getName());
		    switch (currentTag) {
		    case name:
			state = ParseStates.IR_BEGIN_NAME_NEXT;
			break;
		    case ingredient:
			state = ParseStates.IR_INGREDIENTS;
			currentIngredient = parseIngredient(pullParser);
			if (!newRecipe.addIngredient(currentIngredient)) {
			    throw new IllegalArgumentException(
				    "Duplicate ingredient "
					    + currentIngredient.name
					    + " (Line: "
					    + pullParser.getLineNumber() + ").");
			}
			assert pullParser.getName().equals(
				ValidTags.ingredient.toString())
				&& pullParser.getEventType() == XmlPullParser.END_TAG;
			break;
		    case cuisine:
			state = ParseStates.IR_CUISINES;
			break;
		    case type:
			state = ParseStates.IR_TYPES;
			break;
		    case preparation:
			state = ParseStates.IR_PREPARATION;
			assert newRecipe.getPreparation() == null; // "xml schema validation"
								   // guarantees
								   // uniqueness
			newRecipe.setPreparation(pullParser.getText());
			break;
		    default:
			throw new IllegalStateException("Illegal parse state!");
		    }
		} else if (eventType == XmlPullParser.TEXT) {
		    switch (state) {
		    case IR_BEGIN_NAME_NEXT:
			assert newRecipe.getName() == null; // xml schema
			// validation guarantuees uniqueneness
			newRecipe.setName(pullParser.getText());
			break;
		    case IR_CUISINES:
			if (!newRecipe.addCuisine(pullParser.getText())) {
			    throw new IllegalArgumentException(
				    "Duplicate cuisine " + pullParser.getText()
					    + " (Line: "
					    + pullParser.getLineNumber() + ").");
			}
			break;
		    case IR_TYPES:
			if (!newRecipe.addType(pullParser.getText())) {
			    throw new IllegalArgumentException(
				    "Duplicate type " + pullParser.getText()
					    + " (Line: "
					    + pullParser.getLineNumber() + ").");
			}
			break;
		    case IR_PREPARATION:
			assert newRecipe.getPreparation() == null; // "xml schema validation"
								   // guarantees
								   // uniqueness
			newRecipe.setPreparation(pullParser.getText());
			break;
		    default:
			throw new IllegalStateException("Illegal parse state.");
		    }
		} else if (eventType == XmlPullParser.END_TAG) {
		    currentTag = ValidTags.valueOf(pullParser.getName());
		    switch (currentTag) {
		    case name:
			state = ParseStates.IR_TYPES;
			break;
		    case preparation:
			state = ParseStates.IR_INGREDIENTS;
		    }
		}
		pullParser.next();
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
	Log.d(LOG_TAG, "Recipe: name=" + newRecipe.getName() + ", types="
		+ Arrays.toString(newRecipe.getCategories()));
	Log.d(LOG_TAG, "cuisines=" + Arrays.toString(newRecipe.getCuisines())
		+ ", preparation:");

	return newRecipe;
    }

    private Ingredient parseIngredient(XmlPullParser pullParser)
	    throws XmlPullParserException, IOException {
	int eventType = pullParser.getEventType();
	assert pullParser.getName().equals(ValidTags.ingredient.toString());
	Ingredient ingredient = new Ingredient();
	checkXMLSchemaConform(pullParser);
	state = ParseStates.II_NAME;
	pullParser.next();
	checkXMLSchemaConform(pullParser);
	eventType = pullParser.getEventType();
	assert eventType == XmlPullParser.START_TAG
		&& pullParser.getName().equals(ValidTags.name.toString());
	ingredient.name = pullParser.nextText();
	pullParser.next();
	checkXMLSchemaConform(pullParser);
	state = ParseStates.II_AMOUNT;
	eventType = pullParser.getEventType();
	assert eventType == XmlPullParser.START_TAG
		&& pullParser.getName().equals(ValidTags.amount.toString());
	ingredient.unit = Ingredient.Unit.valueOf(pullParser
		.getAttributeValue(0));
	ingredient.quantity = Double.valueOf(pullParser.nextText());
	pullParser.next();
	checkXMLSchemaConform(pullParser);
	state = ParseStates.II_END;
	eventType = pullParser.getEventType();
	assert eventType == XmlPullParser.END_TAG
		&& pullParser.getName().equals(ValidTags.ingredient.toString());

	assert ingredient.name != null && ingredient.quantity != Double.NaN
		&& ingredient.unit != null : "Ingredient incomplete!";
	Log.d(LOG_TAG, "Ingredient: " + ingredient);
	return ingredient;
    }

    private boolean recipeIsComplete(Recipe newRecipe) {
	return (newRecipe.getIngredients().length >= 1
		&& newRecipe.getName() != null
		&& newRecipe.getName().length() >= 1
		&& newRecipe.getPortions() != -1 && newRecipe.getPreparation() != null);
    }
}
