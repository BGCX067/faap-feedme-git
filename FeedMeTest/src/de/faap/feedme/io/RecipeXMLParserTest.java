package de.faap.feedme.io;

import java.io.*;
import java.util.Map.Entry;
import android.content.*;
import android.test.*;
import android.util.*;
import de.faap.feedme.io.RecipeXMLParser.ValidAttributes;
import de.faap.feedme.io.RecipeXMLParser.ValidTags;
import de.faap.feedme.provider.RecipeDatabaseHelper.Tables;
import de.faap.feedme.util.*;

// FIXME: Write test and implement that only one "case-version" is stored for a
// qualifier
public class RecipeXMLParserTest extends InstrumentationTestCase {
    RecipeXMLParser parser;
    Context appContext;
    Context testContext;
    final String id = RecipeXMLParser.ID_HEADER;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        appContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        resetParser();
    }

    public void testParseSucceeds() throws IOException {
        InputStream recipeStream =
                getInstrumentation().getContext().getAssets()
                        .open("recipesTestValid.xml");
        assertTrue(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseHasCorrectOutput() throws IOException {
        InputStream recipeStream =
                getInstrumentation().getContext().getAssets()
                        .open("recipeForOutputTest.xml");
        assertTrue(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
        for (Tables tab : Tables.values()) {
            ContentValues[] vals = parser.getValuesForTable(tab);
            assertTrue("At table " + tab.toString() + ".",
                       hasNoDuplicates(vals));
        }
        for (Recipe recipe : parser.recipes) {
            int recipeId =
                    assertContains(recipe.getName(), ValidTags.name.toString(),
                                   Tables.Recipes);
            assertContains(recipe.getPreparation(),
                           ValidTags.preparation.toString(), Tables.Recipes);
            assertContains(recipe.getPortions(), ValidTags.portions.toString(),
                           Tables.Recipes);
            assertContainsIngredients(recipe, recipeId);
            assertContainsCategories(recipe, recipeId);
            assertContainsCuisine(recipe);
        }
    }

    private void assertContainsIngredients(Recipe recipe, int recipeId) {
        for (Ingredient ingr : recipe.getIngredients()) {
            ContentValues ingrRow =
                    getRow(ingr.name, ValidTags.name.toString(),
                           Tables.Ingredients);
            assertTrue("Units do not match in data Record "
                               + ingrRow.toString() + ". Ingredient unit is: "
                               + ingr.unit + ".",
                       ingrRow.containsKey(ValidAttributes.unit.toString())
                               && ingrRow.getAsInteger(ValidAttributes.unit
                                       .toString()) == ingr.unit.ordinal());
            ContentValues linkRow = new ContentValues(2);
            linkRow.put(ValidTags.ingredient.toString(),
                        ingrRow.getAsInteger(id));
            linkRow.put(ValidTags.recipe.toString(), recipeId);
            linkRow.put(ValidTags.amount.toString(), ingr.quantity);
            assertContainsLink(Tables.One_takes, linkRow);
        }

    }

    private void assertContainsLink(Tables table, ContentValues row) {
        for (ContentValues c : parser.getValuesForTable(table)) {
            boolean same = true;
            for (Entry<String, Object> rowCol : row.valueSet()) {
                if (!c.get(rowCol.getKey()).equals(rowCol.getValue())) {
                    same = false;
                    break;
                }
            }
            if (same) {
                // length might still be not equal
                // but this is allowed, the link must just be there
                return;
            }
        }
        assertFalse("Table " + table.toString() + " does not contain row "
                + row + ".", true);
    }

    private ContentValues getRow(Object identifier, String column, Tables table) {
        assertContains(identifier, column, table);
        for (ContentValues c : parser.getValuesForTable(table)) {
            if (identifier.equals(c.get(column))) {
                return c;
            }
        }
        return null;
    }

    private void assertContainsCategories(Recipe recipe, int recipeID) {
        for (String cat : recipe.getCategories()) {
            ContentValues catRow =
                    getRow(cat, ValidTags.name.toString(), Tables.Type);
            ContentValues linkRow = new ContentValues(2);
            linkRow.put(ValidTags.type.toString(), catRow.getAsInteger(id));
            linkRow.put(ValidTags.recipe.toString(), recipeID);
            assertContainsLink(Tables.Categories, linkRow);
        }
    }

    private void assertContainsCuisine(Recipe recipe) {
        String cuisine = recipe.getCuisine();
        ContentValues cuisineRow =
                getRow(cuisine, ValidTags.name.toString(), Tables.Cuisine);
        ContentValues linkRow = new ContentValues(2);
        linkRow.put(ValidTags.cuisine.toString(), cuisineRow.getAsInteger(id));
        linkRow.put(ValidTags.name.toString(), recipe.getName());
        assertContainsLink(Tables.Recipes, linkRow);
    }

    int assertContains(String content, String column, Tables table) {
        ContentValues[] rows = parser.getValuesForTable(table);
        for (ContentValues row : rows) {
            if (!row.containsKey(column))
                continue;
            if (row.getAsString(column).equalsIgnoreCase(content))
                return row.getAsInteger(id);
        }
        assertFalse("The expected content '" + content
                + "' was not found in column '" + column + "' of table "
                + table.toString() + ".", true);
        return -1;
    }

    private int assertContains(Object content, String column, Tables table) {
        ContentValues[] rows = parser.getValuesForTable(table);
        for (ContentValues row : rows) {
            if (!row.containsKey(column))
                continue;
            if (row.get(column).equals(content))
                return row.getAsInteger(id);
        }
        assertFalse("The expected content '" + content
                + "' was not found in column '" + column + "' of table "
                + table.toString() + ".", true);
        return -1;
    }

    private boolean hasNoDuplicates(ContentValues[] vals) {
        // this method tests if there are any two completely identical rows
        // this is forbidden. Contextual difference is assured/tested elsewhere.
        ContentValues currentSingleton;
        ContentValues currentComparison;
        for (int i = 0; i < vals.length; i++) {
            currentSingleton = vals[i];
            for (int j = i + 1; j < vals.length; j++) {
                currentComparison = vals[j];
                if (currentSingleton.equals(currentComparison)) {
                    Log.d("RecipeXMLParserTest",
                          "Found two equivalent rows.\nContent: ");
                    Log.d("RecipeXMLParserTest", currentSingleton.toString());
                    return false;
                }
            }
        }
        return true;
    }

    public void testParseFailsNoIngredientName() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noIngredientName.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsIllegalAmountUnit() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("illegalAmountUnit.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsIllegalAmountData() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("illegalAmountData.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsMultipleOrNoAmount() throws IOException {
        InputStream recipeStream = testContext.getAssets().open("noAmount.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        resetParser();
        recipeStream = testContext.getAssets().open("multipleAmounts.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsNoIngredient() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noIngredient.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsMultipleOrNoCuisines() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("multipleCuisines.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
        recipeStream = testContext.getAssets().open("noCuisine.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsNoCategory() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noCategory.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsNoName() throws IOException {
        InputStream recipeStream = testContext.getAssets().open("noName.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsDuplicateOrWrongEntries() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("sameCaseDuplicate.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
        recipeStream =
                testContext.getAssets().open("caseInsensitiveDuplicate.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
        recipeStream =
                testContext.getAssets()
                        .open("whitespaceContainingDuplicate.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsIllegalXML() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("recipesXMLIncorrect.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    public void testParseFailsDuplicateIngredientInRecipe() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("duplicateIngredient.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream.close();
    }

    private void resetParser() {
        parser = new RecipeXMLParser(appContext.getResources());
    }

}
