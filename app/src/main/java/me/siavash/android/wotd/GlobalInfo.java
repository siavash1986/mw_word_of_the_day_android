package me.siavash.android.wotd;

public class GlobalInfo {

  private GlobalInfo() {
  }

  public static final String DB_NAME = "words.db";
  public static final int LIKE_IMAGE_KEY = 0;
  public static final int LIKE_IMAGE_DATE_KEY = 1;
  public static final String KEY_FIRSTRUN = "K_FIRSTRUN";
  public static final int EXPECTED_RECORDS_DB = 4000;
  public static final long FIREST_PODCAST_DATE = 1157022060000L; //1157035061000L;

  public static final String KEY_LATEST_WORD_DATE = "LATEST_WORD_DATE";
  public static final String REST_SERVER_ADDRESS = "138.68.0.73:8080";
  public static final String RESET_SERVER_BATCH_ENDPOINT = "//batch/flatwords";
  public static final String RESET_SERVER_ENDPOINT = "//word//flat";
  public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";
  public static final String AMAZON_S3 = "https://s3.us-east-2.amazonaws.com/mwwottd/words/wd";

  public static final String KEY_LAST_SYNC = "LAST_SYNC";

}
