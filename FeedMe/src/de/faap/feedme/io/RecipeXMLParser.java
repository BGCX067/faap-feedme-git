package de.faap.feedme.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import android.content.ContentValues;

public class RecipeXMLParser {
    private static String FILE_PATH = "/mnt/sdcard/recipes.xml";
    private static String SCHEMA_PATH = "/mnt/sdcard/recipe.xsd";

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
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
	    Schema newSchema = schemaFactory.newSchema(new File(SCHEMA_PATH));

	    factory.setSchema(newSchema);
	} catch (SAXException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// XmlPullParserFactory factory;
	// try {
	// factory = XmlPullParserFactory.newInstance();
	// factory.setValidating(true);
	// XmlPullParser pullParser = factory.newPullParser();
	// pullParser.setInput(input, "UTF-8");
	// int eventType = pullParser.getEventType();
	// while (eventType != XmlPullParser.END_DOCUMENT) {
	// switch (eventType) {
	// case XmlPullParser.START_TAG:
	// System.out.println("Start: " + pullParser.getName());
	// break;
	// case XmlPullParser.END_TAG:
	// System.out.println("End: " + pullParser.getName());
	// break;
	// }
	// pullParser.next();
	// }
	// } catch (XmlPullParserException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	return true;
    }
}
