package me.siavash.android.wotd.tasks;

import java.util.Set;

public interface Command<T> {
  void execute(Set<T> set);
}