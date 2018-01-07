package me.siavash.android.wotd.listeners;

import java.util.List;

import me.siavash.android.wotd.entities.Word;

public interface DatabaseTaskListener {

  default void complete(List<Word> wordList) {
  }

  default void complete(Word word) {
  }
}
