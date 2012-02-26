package de.faap.feedme.io;

import java.io.*;
import android.content.res.*;
import android.util.*;

public class DatabaseUpdater implements IUpdateDatabase {
    private static String PATH_TO_DEF_RECIPE = "recipes.xml";
    RecipeXMLParser xmlParser;
    AssetManager assetManager;

    public DatabaseUpdater(Resources resMan, AssetManager assetMan) {
        xmlParser = new RecipeXMLParser(resMan);
        this.assetManager = assetMan;
    }

    @Override
    public boolean isUpToDate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean update() {
        InputStream newRecipesStream = getNewRecipesStream();
        if (newRecipesStream == null)
            return false;

        return xmlParser.reparseRecipeDatabase(newRecipesStream);
    }

    private InputStream getNewRecipesStream() {
        // TODO: this method is a candidate to be moved to the class that is, in
        // the future, responsible for recipeXML-version-handling and retrieving
        // from the internet/file system.
        try {
            return assetManager.open(PATH_TO_DEF_RECIPE);
        } catch (IOException e) {
            Log.e("faap.feedme", "Could not open any recipe resource at all!");
            e.printStackTrace();
            return null;
        }

    }

}
