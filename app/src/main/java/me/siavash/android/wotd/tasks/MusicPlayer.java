package me.siavash.android.wotd.tasks;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;

import me.siavash.android.wotd.R;
import me.siavash.android.wotd.listeners.MediaPlayerEvents;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MusicPlayer extends BroadcastReceiver
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

  public final static String ACTION = "me.siavash.android.wotd.podcastReceiver";

  private static WeakReference<Context> mWeakContext;

  private static boolean mSongPlaying;

  private boolean mInitialized;

  private static Uri mMusicPlayingNow;

  private Uri mMusicUri;

  private static MediaPlayer mPlayer;

  private static State mState = State.NOT_INITIALIZED;

  private static NotificationManager mNotificationManager;

  public static Intent makeIntent(String command, Uri uri) {
    return uri == null ?
        new Intent(ACTION)
            .putExtra("command", command)
        :
        new Intent(ACTION)
            .putExtra("data", uri.toString())
            .putExtra("command", command);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    mWeakContext = new WeakReference<>(context);
    if (mNotificationManager == null)
      mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    String command = intent.getStringExtra("command");
    Uri data = null;
    if (intent.hasExtra("data")) {
      data = Uri.parse(intent.getStringExtra("data"));
      mMusicUri = data;
    }


    switch (command) {
      case "play/pause":
        playPauseResume(data);
        break;
      case "stop":
        stop();
        break;
      default:
        break;
    }
  }

  @Override
  public IBinder peekService(Context myContext, Intent service) {
    return super.peekService(myContext, service);
  }

  public static String getMusicPlayingNow() {
    return mMusicPlayingNow == null ? "" : mMusicPlayingNow.toString();
  }

  private void stop() {
    if (mPlayer != null) {
      mPlayer.stop();
      mPlayer.release();
      mPlayer = null;
      mSongPlaying = false;
      mMusicPlayingNow = null;
      mState = State.NOT_INITIALIZED;
      dismissNotification();
      EventBus.getDefault().post(MediaPlayerEvents.STOPPED);
    }
  }

  private void playPauseResume(Uri uri) {

    if (mState == State.NOT_INITIALIZED || (mState == State.PLAYING && !mMusicPlayingNow.equals(mMusicUri))) {
      initMediaPlayer(uri);
    } else if (mState == State.PLAYING && mMusicPlayingNow.equals(mMusicUri)) {
      pause();
    } else if (mState == State.PAUSED && mMusicPlayingNow.equals(mMusicUri)) {
      resume();
    }
  }

  private void resume() {
    mPlayer.start();
    mState = State.PLAYING;
    EventBus.getDefault().post(MediaPlayerEvents.RESUMED);
  }

  private void pause() {
    if (mPlayer != null) {
      mPlayer.pause();
      mState = State.PAUSED;
      EventBus.getDefault().post(MediaPlayerEvents.PAUSED);
    }
  }

  private void initMediaPlayer(Uri uri) {
    if (mPlayer != null && mPlayer.isPlaying()) {
      stopWithoutCallback();
    }
    mPlayer = new MediaPlayer();
    mPlayer.setOnPreparedListener(this);
    mPlayer.setOnCompletionListener(this);
    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    try {
      mPlayer.setDataSource(uri.toString());
      mPlayer.prepareAsync();
    } catch (IOException | IllegalStateException e) {
      FirebaseCrash.report(e);
    }

    mState = State.PREPARING;
  }

  private void stopWithoutCallback() {
    if (mPlayer != null) {
      mPlayer.stop();
      mPlayer.release();
      mPlayer = null;
      mSongPlaying = false;
      mMusicPlayingNow = null;
      mState = State.NOT_INITIALIZED;
      dismissNotification();
    }
  }


  @Override
  public void onCompletion(MediaPlayer mp) {
    mState = State.NOT_INITIALIZED;
    mSongPlaying = false;
    mMusicPlayingNow = null;
    dismissNotification();
    EventBus.getDefault().post(MediaPlayerEvents.STOPPED);
  }

  @Override
  public void onPrepared(MediaPlayer mp) {

    mState = State.PREPARED;
    mp.setLooping(false);
    mSongPlaying = true;
    mInitialized = true;
    mPlayer.start();
    mState = State.PLAYING;
    mMusicPlayingNow = mMusicUri;
    raiseNotification(mWeakContext.get());
    EventBus.getDefault().post(MediaPlayerEvents.ON_PREPARED);
  }

  private void raiseNotification(Context context) {

    Intent stopIntent = makeIntent("stop", null);
    PendingIntent pendingIntentStop =
        PendingIntent.getBroadcast(context, 1, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);


    Intent pauseIntent = makeIntent("play/pause", mMusicPlayingNow);
    PendingIntent pendingIntentPause =
        PendingIntent.getBroadcast(context, 2, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1337")
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setColorized(true)
        .setSmallIcon(R.drawable.ic_music_note_black_48dp)
        .addAction(R.drawable.ic_pause_play, "Pause", pendingIntentPause)
        .addAction(R.drawable.ic_stop_black_48dp, "Stop", pendingIntentStop)
        .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0).setShowCancelButton(true))
        .setContentTitle("WORD OF THE DAY");

    mNotificationManager.notify(1337, builder.build());


  }


  private void dismissNotification() {
    mNotificationManager.cancel(1337);
  }

  public enum State {
    NOT_INITIALIZED, PREPARING, INITIALIZED, PREPARED, PLAYING, PAUSED, STOP
  }

}
