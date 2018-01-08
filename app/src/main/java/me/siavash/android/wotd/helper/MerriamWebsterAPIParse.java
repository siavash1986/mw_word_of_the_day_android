package me.siavash.android.wotd.helper;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class MerriamWebsterAPIParse {

  private static final String ns = null;

  public static String parse(String s) {
    String attributeValue = null;
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(new StringReader(s));
      int index = 0;
      while (parser.next() != -1) {
        String name = parser.getName();
        index++;
        if (name != null && name.equals("wav")) {
          attributeValue = parser.nextText();
          break;
        }
      }
    } catch (IOException | XmlPullParserException e) {
      e.printStackTrace();
    }

    String sb = "https://media.merriam-webster.com/soundc11/" +
        attributeValue.charAt(0) +
        "/" +
        attributeValue;

    return sb;
  }

  private static void readEntry(XmlPullParser parser) {
    try {
      parser.require(XmlPullParser.START_TAG, ns, "wav");
    } catch (XmlPullParserException | IOException e) {
      e.printStackTrace();

    }

  }

  private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
    String result = "";
    if (parser.next() == XmlPullParser.TEXT) {
      result = parser.getText();
      parser.nextTag();
    }
    return result;

  }
}
