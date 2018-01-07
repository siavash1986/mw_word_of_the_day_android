package me.siavash.android.wotd.listeners;

import org.joda.time.LocalDate;

public interface DateChangeListener {

  void datePickerChanged(LocalDate jumpToDate);
}
