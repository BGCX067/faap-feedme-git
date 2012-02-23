package de.faap.feedme.io;

import java.io.*;
import javax.xml.*;
import javax.xml.validation.*;
import org.xml.sax.*;
import org.xmlpull.v1.*;
import android.util.*;

public class XmlValidatingParserFactory {
    private static final boolean tryBuiltInValidatingParser = false; // no real
								     // automatic
								     // switching,
								     // as this
								     // would
								     // have to
								     // be
								     // tested

    public static XmlPullParser newValidatingParser(String schemaLocation) {
	XmlPullParser newParser = null;
	XmlPullParserFactory factory = null;
	try {
	    factory = XmlPullParserFactory.newInstance();
	    factory.setValidating(true);
	    SchemaFactory schFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
	    schFactory.newSchema(new File(schemaLocation));
	    newParser = factory.newPullParser();
	    if (!tryBuiltInValidatingParser) {
		// also throw exception to enter the "no validation available"
		// branch
		throw new XmlPullParserException(
			"Manual switch to self-built Validating Parser.");
	    }
	} catch (XmlPullParserException e) {
	    try {
		factory = XmlPullParserFactory.newInstance();
		newParser = factory.newPullParser();
	    } catch (XmlPullParserException e2) {
		Log.d("faap.feedme.xmlparse", "Fatal: Could not create parser!");
	    }
	    newParser = new RecipeValidatingXmlPullParser(newParser); // return
								      // own
	    // creation
	} catch (IllegalArgumentException e) {
	    try {
		factory = XmlPullParserFactory.newInstance();
		newParser = factory.newPullParser();
	    } catch (XmlPullParserException e2) {
		Log.d("faap.feedme.xmlparse", "Fatal: Could not create parser!");
	    }
	    newParser = new RecipeValidatingXmlPullParser(newParser); // return
								      // own
	    // creation
	} catch (SAXException e) {
	    Log.e("faap.feedme.xmlparse", "Unexpected SAXException!");
	    e.printStackTrace();
	}
	return newParser;
    }
}
