package de.faap.feedme.io;

import java.io.*;
import android.content.*;
import android.content.res.*;
import android.util.*;
import de.faap.feedme.provider.*;

public class DatabaseUpdater implements IUpdateDatabase {
    private static String PATH_TO_DEF_RECIPE = "recipes.xml";
    RecipeXMLParser xmlParser;
    AssetManager assetManager;
    Context context;

    public DatabaseUpdater(Context context) {
        xmlParser = new RecipeXMLParser(context.getResources());
        this.assetManager = context.getAssets();
        this.context = context;
    }

    @Override
    public boolean isUpToDate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean update() {
        IRecipeReceiver receiver =
                new RecipeReceiver(new RecipeDatabaseHelper(context));

        InputStream newRecipesStream = getNewRecipesStream();

        if (newRecipesStream == null)
            return false;

        boolean state = xmlParser.reparseRecipeDatabase(newRecipesStream);

        receiver.open();
        for (RecipeDatabaseHelper.Tables tab : RecipeDatabaseHelper.Tables
                .values()) {
            receiver.addTable(tab.toString(), xmlParser.getValuesForTable(tab));
            Log.d("faap.feedmee.dbupdate", "Table " + tab.toString() + " added");
        }
        receiver.close();

        return state;
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
