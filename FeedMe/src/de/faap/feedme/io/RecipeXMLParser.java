package de.faap.feedme.io;

import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;
import android.content.*;
import android.content.res.*;
import android.util.*;
import de.faap.feedme.*;
import de.faap.feedme.provider.*;
import de.faap.feedme.util.*;
import de.faap.feedme.util.Recipe.Effort;

public class RecipeXMLParser {
    private static String LOG_TAG = "faap.feedme.xmlparse";

    private Resources resourceManager;

    HashSet<String> recipeNames = new HashSet<String>();
    ArrayList<Recipe> recipes = new ArrayList<Recipe>();
    private ParseStates state = ParseStates.BEGIN;

    private enum ParseStates {
        BEGIN,
        IN_RECIPES,
        IR_BEGIN_NAME_NEXT,
        IR_TYPES,
        IR_CUISINES,
        IR_PREPARATION,
        IR_INGREDIENTS,
        II_NAME,
        II_AMOUNT,
        II_END,
        IN_INNERMOST,
        END,
        IR_PORTIONS
    }

    enum ValidTags {
        recipes,
        recipe,
        name,
        type,
        preparation,
        portions,
        cuisine,
        ingredient,
        amount

    }

    boolean openingTagAllowed = true; // to check if elements are illegally
                                      // nested

    boolean hadType = false; // additional flag to check if at least one type
                             // has been given

    enum ValidAttributes {
        effort,
        unit
    }

    private HashMap<String, ContentValues> dbIngredientsTable =
            new HashMap<String, ContentValues>();
    private HashMap<String, ContentValues> dbCategoriesTable =
            new HashMap<String, ContentValues>();
    private HashMap<String, ContentValues> dbCuisinesTable =
            new HashMap<String, ContentValues>();
    private HashMap<String, ContentValues> dbEffortsTable =
            new HashMap<String, ContentValues>();
    private HashMap<String, ContentValues> dbUnitsTable =
            new HashMap<String, ContentValues>();

    private ArrayList<ContentValues> dbRecipesTable =
            new ArrayList<ContentValues>();
    private ArrayList<ContentValues> dbIngredientsRecipeTable =
            new ArrayList<ContentValues>();
    private ArrayList<ContentValues> dbCategoriesRecipeTable =
            new ArrayList<ContentValues>();

    public RecipeXMLParser(Resources resMan) {
        this.resourceManager = resMan;
    }

    public boolean reparseRecipeDatabase(InputStream recipeStream) {
        boolean parseSuccess = parseRecipeStream(recipeStream);
        if (!parseSuccess)
            return false;

        // now create data sets

        int referenceKey;
        Recipe recipe;
        for (int i = 0; i < recipes.size(); i++) {
            recipe = recipes.get(i);
            ContentValues recipesTableEntry = new ContentValues(5);
            // set cuisine
            referenceKey = pushCuisine(recipe.getCuisine(), dbCuisinesTable);
            recipesTableEntry.put(ValidTags.cuisine.toString(), referenceKey);

            // set effort
            referenceKey = pushEffort(recipe.getEffort(), dbEffortsTable);
            recipesTableEntry.put(ValidAttributes.effort.toString(),
                                  referenceKey);

            // set portions
            recipesTableEntry.put(ValidTags.portions.toString(),
                                  recipe.getPortions());

            // set preparation
            recipesTableEntry.put(ValidTags.preparation.toString(),
                                  recipe.getPreparation());

            // set categories
            ContentValues categoriesRecipeTableEntry = new ContentValues();
            for (String category : recipe.getCategories()) {
                referenceKey = pushCategory(category, dbCategoriesTable);
                categoriesRecipeTableEntry.put(ValidTags.recipe.toString(), i);
                categoriesRecipeTableEntry.put(ValidTags.type.toString(),
                                               referenceKey);
                dbCategoriesRecipeTable.add(categoriesRecipeTableEntry);
            }

            // set ingredients
            ContentValues ingredientsRecipeTableEntry = new ContentValues();
            for (Ingredient ingredient : recipe.getIngredients()) {
                referenceKey =
                        pushIngredient(ingredient, dbIngredientsTable,
                                       dbUnitsTable);
                ingredientsRecipeTableEntry.put(ValidTags.recipe.toString(), i);
                ingredientsRecipeTableEntry
                        .put(ValidTags.ingredient.toString(), referenceKey);
                ingredientsRecipeTableEntry.put(ValidTags.amount.toString(),
                                                ingredient.quantity);
                dbIngredientsRecipeTable.add(ingredientsRecipeTableEntry);
            }

            dbRecipesTable.add(recipesTableEntry);

        }

        return true;
    }

    private int pushIngredient(Ingredient ingredient,
            Map<String, ContentValues> ingredientsTable,
            Map<String, ContentValues> unitsTable)
            throws IllegalArgumentException {
        if (ingredientsTable.containsKey(ingredient.name)) {
            // check if the units match
            ContentValues storedIngredient =
                    ingredientsTable.get(ingredient.name);
            int storedUnitKey =
                    storedIngredient.getAsInteger(ValidAttributes.unit
                            .toString());
            Ingredient.Unit storedUnit =
                    getUnitForKey(storedUnitKey, unitsTable);
            if (!storedUnit.equals(ingredient.unit)) {
                throw new IllegalArgumentException(
                        "Ingredients with the same name, must have the same unit-type. "
                                + ingredient.name + " has "
                                + ingredient.unit.toString() + " as well as "
                                + storedUnit.toString());
            }
            return ingredientsTable.get(ingredient.name).getAsInteger("key");
        }
        ContentValues entry = new ContentValues(3);
        int unitKey = putUnit(ingredient.unit, unitsTable);
        entry.put("key", ingredientsTable.size());
        entry.put(ValidTags.name.toString(), ingredient.name.toString());
        entry.put(ValidAttributes.unit.toString(), unitKey);
        ingredientsTable.put(ingredient.name, entry);
        return 0;
    }

    private int putUnit(Ingredient.Unit unit,
            Map<String, ContentValues> unitsTable) {
        if (unitsTable.containsKey(unit.toString())) {
            return unitsTable.get(unit.toString()).getAsInteger("key");
        }

        ContentValues entry = new ContentValues(2);
        entry.put("key", unitsTable.size());
        entry.put(ValidTags.name.toString(), unit.toString());
        unitsTable.put(unit.toString(), entry);
        return unitsTable.size() - 1;
    }

    private Ingredient.Unit getUnitForKey(int key,
            Map<String, ContentValues> unitsTable) {
        for (ContentValues unitRecord : unitsTable.values()) {
            if (unitRecord.getAsInteger("key") == key) {
                return Ingredient.Unit.valueOf(unitRecord
                        .getAsString(ValidTags.name.toString()));
            }
        }
        return null;
    }

    private int pushCategory(String category,
            Map<String, ContentValues> categoriesTable) {
        if (categoriesTable.containsKey(category)) {
            return categoriesTable.get(category).getAsInteger("key");
        }

        ContentValues entry = new ContentValues(2);
        entry.put("key", categoriesTable.size());
        entry.put(ValidTags.name.toString(), category);
        categoriesTable.put(category, entry);
        return categoriesTable.size() - 1;
    }

    private int pushEffort(Effort effort,
            Map<String, ContentValues> effortsTable) {
        if (effortsTable.containsKey(effort.toString())) {
            return effortsTable.get(effort.toString()).getAsInteger("key");
        }

        ContentValues entry = new ContentValues(2);
        entry.put("key", effortsTable.size());
        entry.put(ValidTags.name.toString(), effort.toString());
        effortsTable.put(effort.toString(), entry);
        return effortsTable.size() - 1;
    }

    private int pushCuisine(String cuisine,
            Map<String, ContentValues> cuisinesTable) {
        if (cuisinesTable.containsKey(cuisine)) {
            return cuisinesTable.get(cuisine).getAsInteger("key");
        }

        ContentValues entry = new ContentValues(2);
        entry.put("key", cuisinesTable.size());
        entry.put(ValidTags.name.toString(), cuisine);
        cuisinesTable.put(cuisine, entry);
        return cuisinesTable.size() - 1;
    }

    public ContentValues[] getValuesForTable(RecipeDatabaseHelper.Tables table) {
        switch (table) {
        case Recipes:
            return dbRecipesTable.toArray(new ContentValues[dbRecipesTable
                    .size()]);
        case Categories:
            return dbCategoriesRecipeTable
                    .toArray(new ContentValues[dbCategoriesRecipeTable.size()]);
        case Cuisine:
            return dbCuisinesTable.values()
                    .toArray(new ContentValues[dbCuisinesTable.size()]);
        case Effort:
            return dbEffortsTable.values()
                    .toArray(new ContentValues[dbEffortsTable.size()]);
        case Ingredients:
            return dbIngredientsTable.values()
                    .toArray(new ContentValues[dbIngredientsTable.size()]);
        case One_takes:
            return dbIngredientsRecipeTable
                    .toArray(new ContentValues[dbIngredientsRecipeTable.size()]);
        case Type:
            return dbCategoriesTable.values()
                    .toArray(new ContentValues[dbCategoriesTable.size()]);
        default:
            assert false;
            return null;
        }

    }

    private boolean parseRecipeStream(InputStream input) {

        try {
            InputStream schemaStream;
            schemaStream = resourceManager.openRawResource(R.raw.recipe_schema);
            XmlPullParser pullParser =
                    XmlValidatingParserFactory
                            .newValidatingParser(schemaStream);
            pullParser.setInput(input, "UTF-8");

            int eventType = pullParser.next(); // skip start document

            while (eventType != XmlPullParser.END_DOCUMENT) {
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
                        addRecipeToData(parsedRecipe);
                        break;
                    default:
                        throw getStandardParseException(pullParser);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    System.out.println("End: " + pullParser.getName());
                    break;
                }
                eventType = pullParser.next();
            }
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG,
                  "Illegal XML Tag in recipe: " + e.getLocalizedMessage());
            return false;
        } catch (XmlPullParserException e1) {
            Log.e(LOG_TAG, "Could not parse the recipe file. See stack trace.");
            e1.printStackTrace();
            return false;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void addRecipeToData(Recipe recipe) {
        recipes.add(recipe);
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
    @SuppressWarnings("incomplete-switch")
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
            eventType = pullParser.next();
            state = ParseStates.IR_BEGIN_NAME_NEXT;

            ValidTags currentTag;
            Ingredient currentIngredient;
            while (!(eventType == XmlPullParser.END_TAG && pullParser.getName()
                    .equals(ValidTags.recipe.toString()))) {
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
                        assert pullParser.getName().equals(ValidTags.ingredient
                                                                   .toString())
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
                        break;
                    case portions:
                        state = ParseStates.IR_PORTIONS;
                        break;
                    default:
                        throw new IllegalStateException("Illegal parse state!");
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    switch (state) {
                    case IR_BEGIN_NAME_NEXT:
                        assert newRecipe.getName() == null; // xml schema
                        // validation guarantuees uniqueneness (of name per
                        // recipe)
                        // now check global uniqueness of recipe names
                        newRecipe.setName(pullParser.getText());
                        if (recipeNames.contains(newRecipe.getName())) {
                            throw new IllegalArgumentException(
                                    "Duplicate recipe name "
                                            + newRecipe.getName() + " (Line: "
                                            + pullParser.getLineNumber() + ").");
                        }
                        recipeNames.add(newRecipe.getName());
                        break;
                    case IR_CUISINES:
                        assert newRecipe.getCuisine() == null; // validation
                        // invariant
                        newRecipe.setCuisine(pullParser.getText());
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
                    case IR_PORTIONS:
                        assert newRecipe.getPortions() == -1;
                        newRecipe.setPortions(Integer.valueOf(pullParser
                                .getText()));
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
                eventType = pullParser.next();
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
        Log.d(LOG_TAG, "Recipe: " + newRecipe);

        return newRecipe;
    }

    private Ingredient parseIngredient(XmlPullParser pullParser)
            throws XmlPullParserException, IOException {
        int eventType = pullParser.getEventType();
        assert pullParser.getName().equals(ValidTags.ingredient.toString());
        Ingredient ingredient = new Ingredient();
        state = ParseStates.II_NAME;
        eventType = pullParser.next();
        assert eventType == XmlPullParser.START_TAG
                && pullParser.getName().equals(ValidTags.name.toString());
        ingredient.name = pullParser.nextText();
        eventType = pullParser.next();
        state = ParseStates.II_AMOUNT;
        assert eventType == XmlPullParser.START_TAG
                && pullParser.getName().equals(ValidTags.amount.toString());
        ingredient.unit =
                Ingredient.Unit.valueOf(pullParser.getAttributeValue(0));
        ingredient.quantity = Double.valueOf(pullParser.nextText());
        eventType = pullParser.next();
        state = ParseStates.II_END;
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
