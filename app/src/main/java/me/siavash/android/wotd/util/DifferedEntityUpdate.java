package me.siavash.android.wotd.util;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.TaskCompletionListener;
import me.siavash.android.wotd.tasks.AddFavoriteCommand;
import me.siavash.android.wotd.tasks.DatabaseUpdateTask;
import me.siavash.android.wotd.tasks.RemoveFavoriteCommand;

public class DifferedEntityUpdate implements TaskCompletionListener {

  private static volatile DifferedEntityUpdate INSTANCE = null;

  private Set<Word> mAddToFav;
  private Set<Word> mRemoveFromFav;

  private static WeakReference<Context> mContext;


  private DifferedEntityUpdate() {
    this.mAddToFav = new HashSet<>();
    this.mRemoveFromFav = new HashSet<>();
  }

  public synchronized static DifferedEntityUpdate getInstance(Context context) {
    if (INSTANCE == null) {
      mContext = new WeakReference<>(context);
      INSTANCE = new DifferedEntityUpdate();
    }

    return INSTANCE;
  }

  public boolean isPendingFavorite(Word word) {
    return mAddToFav.contains(word);
  }

  public boolean isPendingRemove(Word word) {
    return mRemoveFromFav.contains(word);
  }


  public void addToFavorites(Word word) {
    if (isPendingRemove(word)) {
      removeFromRemoveList(word);
    } else {
      mAddToFav.add(word);
    }
  }

  public void removeFromFavorites(Word word) {
    if (isPendingFavorite(word)) {
      removeFromAddList(word);
    } else {
      mRemoveFromFav.add(word);
    }


  }

  private void removeFromAddList(Word word) {
    if (mAddToFav.contains(word))
      mAddToFav.remove(word);
  }

  private void removeFromRemoveList(Word word) {
    if (mRemoveFromFav.contains(word))
      mRemoveFromFav.remove(word);
  }

  public void updateEntities() {
    if (mAddToFav.size() > 0) {
      DatabaseUpdateTask updateTask = new DatabaseUpdateTask(mAddToFav);
      updateTask.setListener(this);
      updateTask.execute(new AddFavoriteCommand(mContext.get()));
    }
    if (mRemoveFromFav.size() > 0) {
      DatabaseUpdateTask updateTask = new DatabaseUpdateTask(mRemoveFromFav);
      updateTask.setListener(this);
      updateTask.execute(new RemoveFavoriteCommand(mContext.get()));
    }
  }


  @Override
  public void addCompleted() {
    mAddToFav.clear();
    //Toast.makeText(mContext.get(), "addCompleted() called", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void removeCompleted() {
    mRemoveFromFav.clear();
//        Toast.makeText(mContext.get(), "removeCompleted() called", Toast.LENGTH_SHORT).show();
  }
}
