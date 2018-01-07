package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.siavash.android.wotd.R;
import me.siavash.android.wotd.adapters.FavoritesAdapter;
import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;

import static me.siavash.android.wotd.util.Utils.extractFuture;

public class HistoryActivity extends BaseActivity {

  @BindView(R.id.history_listView)
  ListView mHistoryListView;

  @BindView(R.id.history_progressbar)
  ContentLoadingProgressBar mProgressBar;

  private ArrayAdapter<Word> mListAdapter;
  private List<Word> mWordList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    initView();

  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    ButterKnife.bind(this);
    initList();

  }

  private void initList() {
    Future<List<Word>> futureList = Executors.newSingleThreadExecutor().submit(() ->
        WordDatabase.get(getApplicationContext()).wordDAO().getVisitedWordsHistory());

    mWordList = extractFuture(futureList);

    if (mWordList.size() == 0){

    }

    setTitle("Visited " + mWordList.size() + " Words");
    mListAdapter = new FavoritesAdapter(this, R.layout.row_history, mWordList, R.id.history_row);
    mHistoryListView.setAdapter(mListAdapter);
    mHistoryListView.setOnItemClickListener((parent, view, position, id) -> {
      String wordDate = (String) view.getTag();
      Intent flashCardIntent = new Intent(HistoryActivity.this, WordActivity.class);
      flashCardIntent.putExtra("EXTRA_KEY", wordDate);
      startActivity(flashCardIntent);
    });
  }

}
