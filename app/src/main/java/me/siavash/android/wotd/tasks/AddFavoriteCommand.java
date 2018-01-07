package me.siavash.android.wotd.tasks;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;

public class AddFavoriteCommand implements Command<Word> {

  private WeakReference<Context> contextWeakReference;

  public AddFavoriteCommand(Context context) {
    this.contextWeakReference = new WeakReference<>(context);
  }

  @Override
  public void execute(Set<Word> set) {
    for (Word w : set) {
      w.setFav(true);
    }
    Set<Word> wordSet = new ConcurrentSkipListSet<>(set);
    WordDatabase.get(contextWeakReference.get()).wordDAO().updateWordSet(wordSet);
  }
}
