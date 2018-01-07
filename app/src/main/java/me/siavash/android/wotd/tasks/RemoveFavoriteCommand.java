package me.siavash.android.wotd.tasks;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;

public class RemoveFavoriteCommand implements Command<Word> {

  private WeakReference<Context> contextWeakReference;

  public RemoveFavoriteCommand(Context context) {
    this.contextWeakReference = new WeakReference<>(context);
  }

  @Override
  public void execute(Set<Word> set) {
    for (Word w : set) {
      w.setFav(false);
    }
    Set<Word> wordSet = new ConcurrentSkipListSet<>(set);
    WordDatabase.get(contextWeakReference.get()).wordDAO().updateWordSet(wordSet);
  }
}
