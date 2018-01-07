package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.siavash.android.wotd.R;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.WordDownloaderListener;
import me.siavash.android.wotd.tasks.DownloadWordTask;
import me.siavash.android.wotd.util.Utils;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener, WordDownloaderListener {

  @BindView(R.id.textViewDateLabel)
  TextView mTextViewDate;
  @BindView(R.id.textViewTitle)
  TextView mTextViewTitle;
  @BindView(R.id.textViewAttribute)
  TextView mTextViewAttribute;
  @BindView(R.id.textViewDefinition)
  TextView mTextViewDefinition;
  @BindView(R.id.imageButton_main_next)
  ImageButton mNextButton;
  @BindView(R.id.imageButton_main_previous)
  ImageButton mPreviousButton;

  private static Word mWord;
  private final static String TAG = MainActivity.class.getSimpleName();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initView();
    setTitle("");
    if (mWord == null)
      mWord = Utils.getLatestWord(this);
    initWord(mWord);
    setupNavigationBottoms();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (needUpdate() && Utils.connectedToInternet(this)) {
      LocalDate missingDate = LocalDate.parse(Utils.getLatestWordDate(this)).plusDays(1);
      DownloadWordTask downloadWordTask = new DownloadWordTask();
      downloadWordTask.setDownloaderListener(this);
      downloadWordTask.execute(missingDate.toString());
    }
  }

  private boolean needUpdate() {
    if (mWord != null) {
      LocalDate currentDate = LocalDate.parse(mWord.getDate());
      return (new LocalDate(DateTimeZone.forID("US/Pacific")).isAfter(currentDate));
    }

    return false;
  }

  public void onCardClick(View view) {
    Intent intent = new Intent(this, WordActivity.class);
    intent.putExtra("EXTRA_KEY", mWord.getDate());
    startActivity(intent);
  }

//    public void onArchiveClick(View view){
//        startActivity(new Intent(this, Main2Activity.class));
//    }

  public void onNavigationClick(View view) {
    Class<?> nextActivity = null;
    switch (view.getId()) {
      case R.id.imageButton_main_Archive:
        nextActivity = Main2Activity.class;
        break;
      case R.id.imageButton_main_Favorites:
        nextActivity = FavoritesActivity.class;
        break;
      case R.id.imageButton_main_History:
        nextActivity = HistoryActivity.class;
        break;
      default:
        Log.wtf(TAG, "How did you get here???");
    }

    if (nextActivity != null)
      startActivity(new Intent(this, nextActivity));
  }

  public void onPreviousWord(View view) {
    LocalDate previousDay = LocalDate.parse(mWord.getDate()).minusDays(1);
    mWord = Utils.getWord(this, previousDay.toString());
    initWord(mWord);
    setupNavigationBottoms();
  }

  public void onNextWord(View view) {
    LocalDate nextDay = LocalDate.parse(mWord.getDate()).plusDays(1);
    mWord = Utils.getWord(this, nextDay.toString());
    initWord(mWord);
    setupNavigationBottoms();
  }

  private void initWord(Word latestWord) {
    mTextViewDate.setText(latestWord.getDate());
    mTextViewTitle.setText(latestWord.getTitle());
    mTextViewAttribute.setText(latestWord.getAttribute());
    mTextViewDefinition.setText(latestWord.getDefinition());
  }

  private void setupNavigationBottoms() {
    if (mWord.getDate().equals(Utils.getLatestWordDate(this))) {
      mNextButton.setEnabled(false);
      mNextButton.setAlpha(0.4f);
      mPreviousButton.setEnabled(true);
      mPreviousButton.setAlpha(1.0f);
    } else if (mWord.getDate().equals("2006-10-24")) {
      mPreviousButton.setEnabled(false);
      mPreviousButton.setAlpha(0.4f);

    } else {
      mNextButton.setEnabled(true);
      mNextButton.setAlpha(1.0f);
      mPreviousButton.setEnabled(true);
      mPreviousButton.setAlpha(1.0f);
    }


  }

  @Override
  public void wordDownloadingComplete(Map<String, Word> result) {
    if (result.size() > 0) {
      Future<?> future = Executors.newSingleThreadExecutor().submit(() -> {
        ArrayList<Word> newWords = new ArrayList<>(result.values());
      });
      Utils.extractFuture(future); // to make sure the thread is executed before running next line;
      mWord = Utils.getLatestWord(this);
      initWord(mWord);
    }
  }

  @Override
  public void progressUpdate(String currentWord, int progress, int total) {

  }
}
