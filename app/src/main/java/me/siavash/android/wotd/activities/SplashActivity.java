package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.siavash.android.wotd.GlobalInfo;
import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.TestListener;
import me.siavash.android.wotd.listeners.WordDownloaderListener;
import me.siavash.android.wotd.tasks.DatabaseOfflineInitTask;
import me.siavash.android.wotd.tasks.DownloadWordTask;
import me.siavash.android.wotd.util.Utils;

public class SplashActivity extends AppCompatActivity implements TestListener, WordDownloaderListener {

  private SharedPreferences mPreferences;
  private static String mNextActivity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    if (dataBaseExists()) {
      startMainActivity();
    } else {
      mNextActivity = "IntroActivity.class";
      initiateDB();
    }

  }

  private boolean dataBaseExists() {
    Future<Integer> localWords = Executors.newSingleThreadExecutor().submit(() ->
        WordDatabase.get(getApplicationContext()).wordDAO().getNumberOfRecoreds());
    int dbRecords = Utils.extractFuture(localWords);
    return dbRecords > GlobalInfo.EXPECTED_RECORDS_DB;
  }

  private void initiateDB() {
    DatabaseOfflineInitTask initTask = new DatabaseOfflineInitTask(new WeakReference<>(getApplicationContext()));
    initTask.setListener(this);
    initTask.execute();
  }

  private void updateLatestWordSharePref() {
    String latestWordDate = Utils.getLatestWordDate(this);
    SharedPreferences.Editor edit = mPreferences.edit();
    edit.putString(GlobalInfo.KEY_LATEST_WORD_DATE, latestWordDate);
    edit.apply();
  }

  private void startMainActivity() {
    mNextActivity = "MainActivity.class";
    syncDb();

  }

  private void syncDb() {
    if (DataBaseUpdateNeeded() && Utils.connectedToInternet(this)) {
      LocalDate missingDate = LocalDate.parse(Utils.getLatestWordDate(this)).plusDays(1);
      DownloadWordTask downloadWordTask = new DownloadWordTask();
      downloadWordTask.setDownloaderListener(this);
      downloadWordTask.execute(missingDate.toString());
    } else {
      fireNextActivity();
    }
  }

  private boolean DataBaseUpdateNeeded() {
    String latestDbWord = mPreferences.getString(GlobalInfo.KEY_LATEST_WORD_DATE, "0");
    if (!latestDbWord.equals("0")) {
      LocalDate localDbDate = LocalDate.parse(latestDbWord);
      return (new LocalDate(DateTimeZone.forID("US/Pacific")).isAfter(localDbDate));
    }
    return false;
  }

  private void startIntroActivity() {
    Intent intent = new Intent(this, IntroActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void complete() {
    boolean firstRun = mPreferences.getBoolean(GlobalInfo.KEY_FIRSTRUN, true);
    if (firstRun) {
      updateLatestWordSharePref();
      setFirstRunFalse();
      syncDb();

    } else {
      updateLatestWordSharePref();
      fireNextActivity();
    }

  }

  private void setFirstRunFalse() {
    SharedPreferences.Editor edit = mPreferences.edit();
    edit.putBoolean(GlobalInfo.KEY_FIRSTRUN, false);
    edit.apply();
  }

  @Override
  public void wordDownloadingComplete(final Map<String, Word> result) {
    if (result.size() > 0) {
      Future<?> submit = Executors.newSingleThreadExecutor().submit(() -> {
        ArrayList<Word> words = new ArrayList<>(result.values());
        WordDatabase.get(getApplicationContext()).wordDAO().insertWords(words);
      });

      Utils.extractFuture(submit);
      Toast.makeText(this, result.size() + " new words added", Toast.LENGTH_SHORT).show();
      updateLatestWordSharePref();

    }

    fireNextActivity();
  }

  private void fireNextActivity() {
    switch (mNextActivity) {
      case "MainActivity.class":
        startActivity(new Intent(this, MainActivity.class));
        finish();
        break;

      case "IntroActivity.class":
        startActivity(new Intent(this, IntroActivity.class));
        finish();
        break;

      default:
        finish();
    }
  }

  @Override
  public void progressUpdate(String currentWord, int progress, int total) {

  }
}
