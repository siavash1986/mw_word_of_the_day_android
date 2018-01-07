package me.siavash.android.wotd.database;

import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Set;

import me.siavash.android.wotd.entities.Word;

@Dao
public interface WordDAO {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  List<Long> insertWords(Word... words);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertWords(List<Word> words);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  Long insertWord(Word word);

  @Update
  int updateWord(Word word);

  @Update
  void updateWordSet(Set<Word> wordSet);

  @Delete
  void deleteWord(Word word);

  @Query("SELECT * FROM WORD ORDER BY date DESC")
  LivePagedListProvider<?, Word> pagedByDate();

  @Query("SELECT * FROM Word WHERE date = :date_req LIMIT 1")
  Word getWordAtDate(String date_req);

  @Query("SELECT * FROM Word")
  List<Word> loadWordsLocalDB();

  @Query("SELECT * FROM Word WHERE fav = 1 ORDER BY title")
  List<Word> getFavoriteWords();

  @Query("SELECT * FROM Word WHERE LastVisitTimeStamp != 0 ORDER BY LastVisitTimeStamp DESC ")
  List<Word> getVisitedWordsHistory();

  @Query("SELECT count(*) FROM word")
  int getNumberOfRecoreds();

  @Query("SELECT * FROM Word ORDER BY date DESC LIMIT 1")
  Word getLatestWord();

  @Query("SELECT importedToAnki from Word WHERE Word.date = :date")
  boolean importedToAnki(String date);

}
