package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.ichi2.anki.api.AddContentApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.siavash.android.wotd.R;
import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.helper.AnkiDroidConfig;
import me.siavash.android.wotd.helper.AnkiDroidHelper;
import me.siavash.android.wotd.listeners.DatabaseTaskListener;
import me.siavash.android.wotd.listeners.MediaPlayerEvents;
import me.siavash.android.wotd.tasks.MusicPlayer;
import me.siavash.android.wotd.tasks.RetrieveWordTask;
import me.siavash.android.wotd.util.DifferedEntityUpdate;
import me.siavash.android.wotd.util.Utils;

public class WordActivity extends AppCompatActivity
    implements DatabaseTaskListener {

  @BindView(R.id.textViewDateLable)
  TextView date;
  @BindView(R.id.textViewTitle)
  TextView title;
  @BindView(R.id.textViewAttribute)
  TextView attribute;
  @BindView(R.id.textViewSyllable)
  TextView syllable;
  @BindView(R.id.textViewDefinition)
  TextView definition;
  @BindView(R.id.textViewExamples)
  TextView example;
  @BindView(R.id.textViewDidYouKnow)
  TextView didYouKnow;
  @BindView(R.id.likeImageViewFlashCard)
  ImageView likeImageView;
  @BindView(R.id.shareImageViewFlashCard)
  ImageView shareImageView;
  @BindView(R.id.playOrPausePodcastImageView)
  ImageView playOrPauseImageView;
  @BindView(R.id.stopPodcastImageView)
  ImageView stopImageView;
  @BindView(R.id.pronunciationButton)
  ImageView pronunciationImageView;
  @BindView(R.id.progressBarPronunciation)
  ProgressBar progressBarPronunciation;


  private static final int AD_PERM_REQUEST = 0;
  private final String TAG = this.getClass().getSimpleName();
  private Word word;
  private MaterialDialog.Builder mProgressBarBuilder;
  private MaterialDialog mProgressBarDialog;
  private boolean mShouldDisplayProgressDialog = true;
  private AnkiDroidHelper mAnkiDroid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAnkiDroid = new AnkiDroidHelper(this);
    String param = getIntent().getStringExtra("EXTRA_KEY");
    new RetrieveWordTask(this, this).execute(param);

  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    finish();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void complete(Word word) {
    setContentView(R.layout.activity_word);
    if (word != null) {
      initView(word);
      visit(word);
    }

  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(MediaPlayerEvents event) {
    switch (event) {
      case ON_PREPARED:
        dismissProgressDialog();
        break;
      case STOPPED:
        setToPlayState();
        mShouldDisplayProgressDialog = true;
        break;
      case PAUSED:
        setToPlayState();
        mShouldDisplayProgressDialog = false;
        break;
      case RESUMED:
        setToPauseState();
        mShouldDisplayProgressDialog = false;
      default:
        break;
    }

  }

  public void share(View v) {
    String message = "Merriam Webster Word Of The Day For " +
        Utils.dateFormatter(this.word.getDate()) + " is: " + "\"" + this.word.getTitle() + "\"" +
        System.getProperty("line.separator") + System.getProperty("line.separator") +
        "https://www.merriam-webster.com/word-of-the-day/" + this.word.getDate();

    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
    shareIntent.setType("text/plain");
    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
  }

  public void likeClick(View v) {
    boolean isFavorite = (word.isFav() || DifferedEntityUpdate.getInstance(this).isPendingFavorite(word)) &&
        (!DifferedEntityUpdate.getInstance(this).isPendingRemove(word));
    if (isFavorite) {
      this.likeImageView.setImageResource(R.drawable.ic_favorite_border_black_48dp);
      word.setFav(false);
      new Thread(() -> WordDatabase.get(getApplicationContext()).wordDAO().updateWord(word)).start();
      Utils.makeToast(this, "\"" + word.getTitle() + "\"" + " removed from favorite list");
    } else {
      this.likeImageView.setImageResource(R.drawable.ic_favorite_black_48dp);
      word.setFav(true);
      new Thread(() -> WordDatabase.get(this).wordDAO().updateWord(word)).start();
      Utils.makeToast(this, "\"" + word.getTitle() + "\"" + " added to favorite list");
    }
  }

  public void pronounceWord(View v) {
    if (!Utils.connectedToInternet(this)) {
      Utils.makeToast(this, getString(R.string.NO_NETWORK_ACCESS_MESSAGE));
    } else {

      pronunciationImageView.setVisibility(View.GONE);
      progressBarPronunciation.setVisibility(View.VISIBLE);

      MediaPlayer mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setOnPreparedListener(MediaPlayer::start);
      mediaPlayer.setOnCompletionListener(mp -> {
        pronunciationImageView.setVisibility(View.VISIBLE);
        progressBarPronunciation.setVisibility(View.GONE);
      });

      try {
        mediaPlayer.setDataSource(word.getPronounceUrl());
        mediaPlayer.prepareAsync();
      } catch (Exception e) {
        e.printStackTrace();
        FirebaseCrash.report(e);
        Utils.makeToast(this, getString(R.string.XML_PARSE_ERROR));
      }

/*      new Thread(() -> {

        pronunciationImageView.setVisibility(View.GONE);
        progressBarPronunciation.setVisibility(View.VISIBLE);

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnCompletionListener(mp -> {
          pronunciationImageView.setVisibility(View.VISIBLE);
          progressBarPronunciation.setVisibility(View.GONE);
        });

        try {
          mediaPlayer.setDataSource(word.getPronounceUrl());
          mediaPlayer.prepareAsync();
        } catch (IOException e) {
          e.printStackTrace();
          FirebaseCrash.report(e);
          Utils.makeToast(this, getString(R.string.XML_PARSE_ERROR));
        }
      }).start();*/
    }
  }

  public void playOrPause(View view) {
    if (!Utils.connectedToInternet(this)) {
      Utils.makeToast(this, getString(R.string.NO_NETWORK_ACCESS_MESSAGE));
    } else {
      if (playOrPauseImageView.getTag().equals("play") && mShouldDisplayProgressDialog) {
        displayProgressDialog();
        mShouldDisplayProgressDialog = false;
      }
      flipState();
      LocalBroadcastManager.getInstance(this)
          .sendBroadcast(MusicPlayer.makeIntent("play/pause", Utils.getPodcastUri(word)));
    }
  }

  public void stop(View view) {
    stop();
  }

  private void stop() {
    LocalBroadcastManager.getInstance(this)
        .sendBroadcast(MusicPlayer.makeIntent("stop", null));
    setToPlayState();
    mShouldDisplayProgressDialog = true;
  }

  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode == AD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      addCardsToAnkiDroid(word);
    } else {
      Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
    }
  }

  public void sendToAnki(View view) {
    boolean isAnkiInstalled;
    PackageManager manager = getApplicationContext().getPackageManager();
    try {
      manager.getApplicationInfo(AddContentApi.getAnkiDroidPackageName(this), 0);
      isAnkiInstalled = true;
    } catch (PackageManager.NameNotFoundException e) {
      isAnkiInstalled = false;
    }

    if (!isAnkiInstalled) {
      new MaterialDialog.Builder(this)
          .title("Anki not found")
          .content("Do you want to install Anki Flash Card?")
          .positiveText("Yes")
          .negativeText("No")
          .onPositive((d, w) -> installAnki())
          .onNegative((d, w) -> d.dismiss())
          .show();
    } else if (mAnkiDroid.shouldRequestPermission())
      mAnkiDroid.requestPermission(this, AD_PERM_REQUEST);
    else {
      if (Utils.hasImportedToAnki(word, this)) {
        new MaterialDialog.Builder(this)
            .title(R.string.ImportAnkiDialogTitle)
            .content(R.string.ImportAnkiDialogContent)
            .positiveText(R.string.ImportAnkiDialogYes)
            .negativeText(R.string.ImportAnkiDialogNo)
            .onPositive((d, w) -> addCardsToAnkiDroid(word))
            .onNegative((d, w) -> d.dismiss())
            .show();
      } else {
        addCardsToAnkiDroid(word);
      }
    }
  }

  private void addCardsToAnkiDroid(Word word) {
    long deckId = 0;
    long modelId = 0;
    try {
      deckId = getDeckId();
      modelId = getModelId();
    } catch (Exception e) {
      new MaterialDialog.Builder(this)
          .title("Anki's permissions are missing!")
          .content("Please run Anki Flash Card and grant required permissions and try again.")
          .positiveText("OK")
          .show();
      return;
    }

    String[] fieldNames = serializeWord(word, mAnkiDroid.getApi().getFieldList(modelId));
    Set<String> tags = new HashSet<>();


    Long res = mAnkiDroid.getApi().addNote(modelId, deckId, fieldNames, tags);
    if (res != null) {
      Utils.makeToast(this, String.format("\"%s\" added to Anki!", word.getTitle()));
      word.setImportedToAnki(true);
      Utils.updateWord(word, this);
    } else {
      Utils.makeToast(this, "Operation failed, make sure Anki is installed and works properly");
    }
  }

  private String[] serializeWord(Word word, String[] modelId) {
    String[] fields = new String[modelId.length];
    fields[0] = word.getTitle();
    fields[1] = word.getAttribute();
    fields[2] = word.getSyllables();
    fields[3] = addHTMLLineBreak(word.getDefinition());
    fields[4] = addHTMLLineBreak(word.getExamples());
    fields[5] = word.getDidYouKnow();
    fields[6] = word.getPodcastUrl();
    return fields;
  }

  private void visit(final Word word) {
    Executors.newSingleThreadExecutor().submit(() -> {
      word.setLastVisitTimeStamp(System.currentTimeMillis() / 1000);
      WordDatabase.get(getApplicationContext()).wordDAO().updateWord(word);
    });
  }

  private String addHTMLLineBreak(String arg) {
    return arg.replace("\n", "<br><br>");
  }

  private void initView(Word word) {
    ButterKnife.bind(this);

    if (word != null) {
      this.word = word;
      StringBuilder sb = new StringBuilder();

      for (String definition : word.getDefinition().split("\n"))
        sb.append(definition).append('\n').append('\n');
      this.definition.setText(sb.toString().trim());

      StringBuilder sb2 = new StringBuilder();
      for (String example : word.getExamples().split("\n"))
        sb2.append(example).append('\n').append('\n');
      this.example.setText(sb2.toString().trim());

      this.date.setText(word.getDate());
      this.title.setText(word.getTitle());
      this.attribute.setText(word.getAttribute());
      this.syllable.setText(word.getSyllables());

      this.didYouKnow.setText(word.getDidYouKnow());
      this.likeImageView.setImageResource(word.isFav() ?
          R.drawable.ic_favorite_black_48dp :
          R.drawable.ic_favorite_border_black_48dp);

      if (word.getPronounceUrl().equals("")) {
        this.pronunciationImageView.setVisibility(View.INVISIBLE);
      }

      if (MusicPlayer.getMusicPlayingNow().equals(Utils.getPodcastUri(word).toString())) {
        setToPauseState();
        mShouldDisplayProgressDialog = false;
      }
    }
  }

  private long getDeckId() {
    Long did = mAnkiDroid.findDeckIdByName(AnkiDroidConfig.DECK_NAME);
    if (did == null) {
      did = mAnkiDroid.getApi().addNewDeck(AnkiDroidConfig.DECK_NAME);
      mAnkiDroid.storeDeckReference(AnkiDroidConfig.DECK_NAME, did);
    }
    return did;
  }

  private long getModelId() {
    Long mid = mAnkiDroid.findModelIdByName(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.length);
    if (mid == null) {
      mid = mAnkiDroid.getApi().addNewCustomModel(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS,
          AnkiDroidConfig.CARD_NAMES, AnkiDroidConfig.QFMT, AnkiDroidConfig.AFMT, AnkiDroidConfig.CSS, getDeckId(), null);
      mAnkiDroid.storeModelReference(AnkiDroidConfig.MODEL_NAME, mid);
    }
    return mid;
  }

  private void installAnki() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://details?id=com.ichi2.anki"));
    startActivity(intent);
  }

  private void flipState() {
    if (playOrPauseImageView.getTag().equals("play")) {
      playOrPauseImageView.setImageResource(R.drawable.ic_pause_black_48dp);
      playOrPauseImageView.setTag("pause");
    } else {
      playOrPauseImageView.setImageResource(R.drawable.ic_play_arrow_black_48dp);
      playOrPauseImageView.setTag("play");
    }
  }

  private void setToPlayState() {
    playOrPauseImageView.setImageResource(R.drawable.ic_play_arrow_black_48dp);
    playOrPauseImageView.setTag("play");
  }

  private void setToPauseState() {
    playOrPauseImageView.setImageResource(R.drawable.ic_pause_black_48dp);
    playOrPauseImageView.setTag("pause");
  }

  private void displayProgressDialog() {
    if (mProgressBarBuilder == null) {
      mProgressBarBuilder = new MaterialDialog.Builder(this)
          .title(R.string.progress_dialog)
          .content(R.string.please_wait)
          .canceledOnTouchOutside(false)
          .cancelListener((d) -> {
            Utils.makeToast(this, "Operation Cancelled!");
            stop();
          })
          .progressIndeterminateStyle(true)
          .progress(true, 0);
    }
    mProgressBarDialog = mProgressBarBuilder.show();
  }

  private void dismissProgressDialog() {
    if (mProgressBarDialog != null) {
      mProgressBarDialog.dismiss();
    }

  }

}
