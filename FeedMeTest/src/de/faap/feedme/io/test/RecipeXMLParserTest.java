package de.faap.feedme.io.test;

import java.io.*;
import android.content.*;
import android.test.*;
import de.faap.feedme.io.*;

public class RecipeXMLParserTest extends InstrumentationTestCase {
    RecipeXMLParser parser;
    Context appContext;
    Context testContext;

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

    public void testParseFailsNoIngredientName() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noIngredientName.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsIllegalAmountUnit() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("illegalAmountUnit.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsIllegalAmountData() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("illegalAmountData.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsMultipleOrNoAmount() throws IOException {
        InputStream recipeStream = testContext.getAssets().open("noAmount.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        resetParser();
        recipeStream = testContext.getAssets().open("multipleAmounts.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsNoIngredient() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noIngredient.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsMultipleOrNoCuisines() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("multipleCuisines.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream = testContext.getAssets().open("noCuisine.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsNoCategory() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("noCategory.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsNoName() throws IOException {
        InputStream recipeStream = testContext.getAssets().open("noName.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsDuplicateOrWrongEntries() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("sameCaseDuplicate.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream =
                testContext.getAssets().open("caseInsensitiveDuplicate.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
        recipeStream =
                testContext.getAssets()
                        .open("whitespaceContainingDuplicate.xml");
        resetParser();
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsIllegalXML() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("recipesXMLIncorrect.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    public void testParseFailsDuplicateIngredientInRecipe() throws IOException {
        InputStream recipeStream =
                testContext.getAssets().open("duplicateIngredient.xml");
        assertFalse(parser.reparseRecipeDatabase(recipeStream));
    }

    private void resetParser() {
        parser = new RecipeXMLParser(appContext.getResources());
    }

}
