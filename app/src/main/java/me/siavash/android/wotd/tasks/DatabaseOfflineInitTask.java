package me.siavash.android.wotd.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import me.siavash.android.wotd.R;
import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.TestListener;

public class DatabaseOfflineInitTask extends AsyncTask<String, Void, Void> {

  private final WeakReference<Context> app;
  private TestListener listener;

  public DatabaseOfflineInitTask(WeakReference<Context> app) {
    this.app = app;
  }

  public void setListener(TestListener listener) {
    this.listener = listener;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    this.listener.complete();
  }

  @Override
  protected Void doInBackground(String... strings) {

    Gson gson = new Gson();
    List<Word> wordList;
    InputStream is = app.get().getResources().openRawResource(R.raw.dump);
    //TODO: if for some reason raw file is not accessible, Plan B: parse manually and insert into DB
    if (is == null) {
      return null;
    }
    String str;
    StringBuilder json = new StringBuilder();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(is));
      while ((str = reader.readLine()) != null) {
        json.append(str);
      }
      wordList = gson.fromJson(json.toString(), new TypeToken<List<Word>>() {
      }.getType());
      WordDatabase.get(app.get()).wordDAO().insertWords(wordList);
    } catch (Exception e) {
      FirebaseCrash.report(e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        if (is != null) {
          is.close();
        }
      } catch (Throwable ignore) {
        FirebaseCrash.report(ignore);
      }
    }
    return null;
  }
}
