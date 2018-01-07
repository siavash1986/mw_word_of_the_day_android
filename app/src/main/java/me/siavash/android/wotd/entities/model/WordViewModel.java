package me.siavash.android.wotd.entities.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import me.siavash.android.wotd.database.WordDatabase;
import me.siavash.android.wotd.entities.Word;

public class WordViewModel extends AndroidViewModel {

  public final LiveData<PagedList<Word>> pagedWord;

  public WordViewModel(Application app) {
    super(app);

    pagedWord = WordDatabase.get(app)
        .wordDAO()
        .pagedByDate()
        .create(null, 10);
  }


}
