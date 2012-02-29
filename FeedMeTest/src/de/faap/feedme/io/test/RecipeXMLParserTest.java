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
        parser = new RecipeXMLParser(appContext.getResources());
    }

    public void testParseSucceeds() throws IOException {
        InputStream recipeStream =
                getInstrumentation().getContext().getAssets()
                        .open("recipesTestValid.xml");
        // InputStream recipeStream =
        // getContext().getAssets().open("recipes.xml");
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

    public void testParseFailsDoubledOrWrongEntries() {

    }

}
