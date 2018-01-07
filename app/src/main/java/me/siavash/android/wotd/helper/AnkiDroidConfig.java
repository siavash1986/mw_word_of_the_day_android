package me.siavash.android.wotd.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Some fields to store configuration details for AnkiDroid
 **/
public final class AnkiDroidConfig {
  // Name of deck which will be created in AnkiDroid
  public static final String DECK_NAME = "Merriam Webster Word of the Day";
  // Name of model which will be created in AnkiDroid
  public static final String MODEL_NAME = "Word of the Day";
  // List of field names that will be used in AnkiDroid model
  public static final String[] FIELDS = {"Word", "Attribute", "Syllables", "Definitions", "Examples",
      "Did_You_Know", "Podcast"};
  // List of card names that will be used in AnkiDroid (one for each direction of learning)
  public static final String[] CARD_NAMES = {"Word>Definition", "Definition>Word"};
  // CSS to share between all the cards (optional). User will need to install the NotoSans font by themselves
  public static final String CSS = ".card {\n" +
      " font-family: \"Times New Roman\", Times, serif;" +
      " font-style: normal;" +
      " text-align: left;\n" +
      " color: black;\n" +
      " background-color: white;\n" +
      " word-wrap: break-word;\n" +
      "}\n" +
      ".big { font-size: 48px; }\n" +
      ".small { font-size: 20px;}\n";
  // Template for the question of each card
  private static final String QFMT1 = "<div class=big style=\"text-align: center\"> {{Word}}</div>" +
      "<div style=\"text-align: center\"><br>{{Attribute}}<br><br>/{{Syllables}}/ </div>";
  private static final String QFMT2 = "<div class=big style=\"text-align: center\">Definition:</div> " +
      "<br><br>" +
      "<div > {{Definitions}} <br> </div>";
  public static final String[] QFMT = {QFMT1, QFMT2};
  // Template for the answer (use identical for both sides)
  private static final String AFMT1 = "<div class=big style=\"text-align: center\">Definition:</div>" + "<div >{{Definitions}}</div>\n" +
      "<br><br>\n" +
      "<a href=\"#\" onclick=\"document.getElementById('examples').style.display='block';return false;\">Examples</a><br><br>\n" +
      "<div id=\"examples\" style=\"display: none\">{{Examples}}</div>\n" +
      "<br>\n" +
      "<a href=\"#\" onclick=\"document.getElementById('didyouknow').style.display='block';return false;\">Did you know?</a><br><br>\n" +
      "<div id=\"didyouknow\" style=\"display: none\">{{Did_You_Know}}</div>\n" +
      "<br><div class=small>{{Tags}}</div>";

  private static final String AFMT2 = "<div class=big style=\"text-align: center\"> {{Word}}</div>" +
      "<div style=\"text-align: center\"><br>{{Attribute}}<br><br>/{{Syllables}}/ </div>";

  public static final String[] AFMT = {AFMT1, AFMT2};
  // Define two keys which will be used when using legacy ACTION_SEND intent
  public static final String FRONT_SIDE_KEY = FIELDS[0];
  public static final String BACK_SIDE_KEY = FIELDS[3];


}
