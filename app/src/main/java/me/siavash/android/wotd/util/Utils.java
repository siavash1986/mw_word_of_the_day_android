package me.siavash.android.wotd.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;

public class Utils {

  private Utils() {
  }

  @NonNull
  public static LocalDate getDate() {
    return new DateTime(DateTimeZone.forID("US/Pacific")).toLocalDate();
  }

  public static long getCurrentEpochPacificTime() {
    return new DateTime(DateTimeZone.forID("US/Pacific")).getMillis();
  }

  public static String dateFormatter(CharSequence text) {
    String string = text.toString();
    String yyyy = string.substring(0, 4);
    String dd = string.substring(8, 10);
    String[] months = {"January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"};
    String mm = months[Integer.parseInt(string.substring(5, 7)) - 1];
    if (dd.charAt(0) == '0') {
      dd = dd.substring(1);
    }
    return mm + " " + dd + ", " + yyyy;
  }

  public static void addWordToFavorite(Word word, Context context) {
    word.setFav(true);
    updateWord(word, context);
  }

  public static void removeWordFromFavorite(Word word, Context context) {
    word.setFav(false);
    updateWord(word, context);
  }

  public static void updateWord(final Word word, final Context context) {
    Executors.newSingleThreadExecutor().submit(() ->
        WordDatabase.get(context).wordDAO().updateWord(word));

  }

  public static boolean hasImportedToAnki(Word w, Context context){
    Future<Boolean> res = Executors.newSingleThreadExecutor().submit(() ->
        WordDatabase.get(context).wordDAO().importedToAnki(w.getDate()));
    return extractFuture(res);
  }

  public static <T> T extractFuture(Future<T> future) {
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      FirebaseCrash.report(e);
      return null;
    }
  }

  public static Word getLatestWord(final Context context) {
    Future<Word> latestWord = Executors.newSingleThreadExecutor()
        .submit(() ->
            WordDatabase.get(context).wordDAO().getLatestWord()
        );
    Word word = extractFuture(latestWord);
    if (word == null) {
      //TODO
    }
    return word;
  }

  public static String getLatestWordDate(final Context context) {
    Future<String> latestWordDate = Executors
        .newSingleThreadExecutor()
        .submit(() -> WordDatabase.get(context).wordDAO().getLatestWord().getDate());
    return extractFuture(latestWordDate);
  }

  public static LocalDate getLatestWordLocalDate(final Context context) {
    return LocalDate.parse(getLatestWordDate(context));
  }

  public static Word getWord(Context context, String date) {
    Future<Word> word = Executors
        .newSingleThreadExecutor()
        .submit(() -> WordDatabase.get(context).wordDAO().getWordAtDate(date));
    return extractFuture(word);
  }

  public static boolean connectedToInternet(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
      return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    return false;
  }

  public static Uri[] getPodcastUri(Word w) {
    Uri uris[] = new Uri[2];
    uris[0] = Uri.parse("http://condor.eb.com/word/podcast/wd" + w.getDate().replace("-", "") + ".mp3");
    uris[1] = Uri.parse("https://s3.us-east-2.amazonaws.com/mwwottd/words/wd" + w.getDate().replace("-", "") + ".mp3");
    return uris;
  }

  public static void makeToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
