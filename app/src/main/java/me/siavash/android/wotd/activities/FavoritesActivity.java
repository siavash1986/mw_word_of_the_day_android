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
import me.siavash.android.wotd.listeners.DatabaseTaskListener;
import me.siavash.android.wotd.tasks.RetrieveFavoriteListTask;

import static me.siavash.android.wotd.util.Utils.extractFuture;

public class FavoritesActivity extends BaseActivity implements DatabaseTaskListener {

  @BindView(R.id.fav_listView)
  ListView mFavoriteListView;

  @BindView(R.id.favorite_progressbar)
  ContentLoadingProgressBar mProgressBar;

  private ArrayAdapter<Word> mListAdapter;
  private List<Word> mWordList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_favoritess);
    ButterKnife.bind(this);
    initView();
    new RetrieveFavoriteListTask(this, this).execute();
  }

  private void initList(List<Word> favoriteList) {
//        Future<List<Word>> futureList = Executors.newSingleThreadExecutor().submit(new Callable<List<Word>>() {
//            @Override
//            public List<Word> call() throws Exception {
//                return WordDatabase.get(getApplicationContext()).wordDAO().getFavoriteWords();
//            }
//        });

    mWordList = favoriteList;
    setTitle(mWordList.size() + " Favorite Words");
    mListAdapter = new FavoritesAdapter(this, R.layout.row_fav, mWordList, R.id.fav_row);
    mFavoriteListView.setAdapter(mListAdapter);
    mFavoriteListView.setOnItemClickListener((parent, view, position, id) -> {
      String wordDate = (String) view.getTag();
      Intent flashCardIntent = new Intent(FavoritesActivity.this, WordActivity.class);
      flashCardIntent.putExtra("EXTRA_KEY", wordDate);
      startActivity(flashCardIntent);
    });
  }

  private void refreshList() {
    Future<List<Word>> futureList = Executors.newSingleThreadExecutor().submit(() ->
        WordDatabase.get(getApplicationContext()).wordDAO().getFavoriteWords());
    List<Word> newWordList = extractFuture(futureList);
    if (newWordList.size() != mWordList.size()) {
      mWordList = newWordList;
      setTitle(mWordList.size() + " Favorite Words");
      mListAdapter.clear();
      mListAdapter.addAll(newWordList);
      mListAdapter.notifyDataSetChanged();
      mFavoriteListView.refreshDrawableState();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    refreshList();

  }

  @Override
  public void complete(List<Word> wordList) {
    initList(wordList);
  }

}
