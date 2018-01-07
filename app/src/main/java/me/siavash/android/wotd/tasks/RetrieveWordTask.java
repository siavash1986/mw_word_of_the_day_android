package me.siavash.android.wotd.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.DatabaseTaskListener;


public class RetrieveWordTask extends AsyncTask<String, Void, Word> {

  private WeakReference<Context> mWeakContext;
  private DatabaseTaskListener mListener;

  public RetrieveWordTask(Context context, DatabaseTaskListener listener) {
    mWeakContext = new WeakReference<>(context);
    mListener = listener;
  }

  @Override
  protected Word doInBackground(String... params) {
    return WordDatabase.get(mWeakContext.get()).wordDAO().getWordAtDate(params[0]);
  }

  @Override
  protected void onPostExecute(Word word) {
    mListener.complete(word);
  }
}
