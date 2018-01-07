package me.siavash.android.wotd.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.siavash.android.wotd.entities.Word;

@Database(entities = {Word.class}, version = 1)
public abstract class WordDatabase extends RoomDatabase {

  public abstract WordDAO wordDAO();

  private static final String DB_NAME = "words.db";
  private static volatile WordDatabase INSTANCE = null;

  public synchronized static WordDatabase get(Context ctxt) {
    if (INSTANCE == null) {
      INSTANCE = create(ctxt, false);
    }

    return (INSTANCE);
  }

  private static WordDatabase create(Context ctxt, boolean memoryOnly) {
    RoomDatabase.Builder<WordDatabase> b;

    if (memoryOnly) {
      b = Room.inMemoryDatabaseBuilder(ctxt.getApplicationContext(),
          WordDatabase.class);
    } else {
      b = Room.databaseBuilder(ctxt.getApplicationContext(), WordDatabase.class,
          DB_NAME);
    }

    return (b.build());
  }
}
