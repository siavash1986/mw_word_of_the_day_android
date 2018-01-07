package me.siavash.android.wotd.listeners;

import java.util.Map;

import me.siavash.android.wotd.entities.Word;

public interface WordDownloaderListener {

  void wordDownloadingComplete(Map<String, Word> result);

  void progressUpdate(String currentWord, int progress, int total);
}
