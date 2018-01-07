package me.siavash.android.wotd.application;

import android.app.Application;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.crash.FirebaseCrash;

import me.siavash.android.wotd.tasks.MusicPlayer;


public class WordApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseCrash.log("Application Started");
    registerBroadcasts();
//    setupLeakCanary();
  }

//  private void setupLeakCanary() {
//    if (LeakCanary.isInAnalyzerProcess(this)) {
//      // This process is dedicated to LeakCanary for heap analysis.
//      // You should not init your app in this process.
//    }
//    LeakCanary.install(this);
//  }


  private void registerBroadcasts() {
    LocalBroadcastManager.getInstance(this).registerReceiver(new MusicPlayer(),
        new IntentFilter("me.siavash.android.wotd.podcastReceiver"));
  }

}
