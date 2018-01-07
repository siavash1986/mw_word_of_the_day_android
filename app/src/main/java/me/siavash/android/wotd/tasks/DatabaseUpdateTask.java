package me.siavash.android.wotd.tasks;

import android.os.AsyncTask;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.TaskCompletionListener;

public class DatabaseUpdateTask extends AsyncTask<Command<Word>, Void, Void> {

  private Set<Word> wordSet;
  private TaskCompletionListener mListener;
  private Command command;

  public void setListener(TaskCompletionListener mListener) {
    this.mListener = mListener;
  }

  public DatabaseUpdateTask(Set<Word> set) {
    this.wordSet = new ConcurrentSkipListSet<>(set);
  }

  @Override
  protected Void doInBackground(Command<Word>[] commands) {
    this.command = commands[0];
    commands[0].execute(wordSet);
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if (command.getClass() == AddFavoriteCommand.class) {
      mListener.addCompleted();
    } else {
      mListener.removeCompleted();
    }

  }
}
