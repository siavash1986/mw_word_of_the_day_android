package me.siavash.android.wotd.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.DatabaseTaskListener;

public class RetrieveFavoriteListTask extends AsyncTask<Void, Integer, List<Word>> {

  private WeakReference<Context> mWeakContext;
  private DatabaseTaskListener mListener;

  public RetrieveFavoriteListTask(Context context, DatabaseTaskListener listener) {
    mWeakContext = new WeakReference<>(context);
    mListener = listener;
  }

  @Override
  protected List<Word> doInBackground(Void... voids) {
    return WordDatabase.get(mWeakContext.get()).wordDAO().getFavoriteWords();
  }

  @Override
  protected void onPostExecute(List<Word> words) {
    mListener.complete(words);
  }
}
