package me.siavash.android.wotd.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;
import android.widget.ImageView;

import org.joda.time.LocalDate;

import java.lang.ref.WeakReference;
import java.util.Locale;

import me.siavash.android.wotd.GlobalInfo;
import me.siavash.android.wotd.R;
import me.siavash.android.wotd.listeners.DateChangeListener;
import me.siavash.android.wotd.util.DifferedEntityUpdate;
import me.siavash.android.wotd.util.Utils;

public class Main2Activity extends BaseActivity
    implements DateChangeListener {

  private ImageView calendar_imageView;
  private CardFragment fragment;
  private static DatePickerFragment mDatePickerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    initView();
    android.app.FragmentManager fragmentManager = getFragmentManager();
    calendar_imageView = findViewById(R.id.calendar_imageView);
    calendar_imageView.setOnClickListener(v -> {
      if (mDatePickerFragment == null) {
        mDatePickerFragment = new DatePickerFragment();
        mDatePickerFragment.setContext(this);
        mDatePickerFragment.setDateChangeListener(this);
      }
      mDatePickerFragment.show(fragmentManager, "datePicker");
    });

    if (savedInstanceState == null) {
      FragmentManager fm = getSupportFragmentManager();
      fragment = new CardFragment();

      fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
    }


  }


  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    DifferedEntityUpdate.getInstance(this).updateEntities();
  }

  @Override
  public void setTheme(int resid) {
    super.setTheme(resid);
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }


  @Override
  public void datePickerChanged(LocalDate jumpToDate) {
    fragment.notifyDateRequest(jumpToDate);
  }


  public static class DatePickerFragment extends DialogFragment
      implements DatePickerDialog.OnDateSetListener {

    private DateChangeListener dateChangeListener;
    private static WeakReference<Context> mWeakContext;
    private static volatile LocalDate mDate;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      if (mWeakContext != null && mDate == null) {
        String latestWordDate = Utils.getLatestWordDate(mWeakContext.get());
        mDate = LocalDate.parse(latestWordDate);
      }
      int year = mDate.getYear();
      int month = mDate.getMonthOfYear();
      int day = mDate.getDayOfMonth();
      DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, --month, day);
      datePickerDialog.getDatePicker().setMinDate(GlobalInfo.FIREST_PODCAST_DATE);
      datePickerDialog.getDatePicker().setMaxDate(Utils.getLatestWordEpoch(getActivity()));
      return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
      String newDate = String.format(Locale.US, "%d-%d-%d", year, ++month, dayOfMonth);
      mDate = LocalDate.parse(newDate);
      dateChangeListener.datePickerChanged(mDate);
    }

    public synchronized void setDateChangeListener(DateChangeListener listener) {
      this.dateChangeListener = listener;
    }

    public synchronized void setContext(Context context) {
      if (mWeakContext == null)
        mWeakContext = new WeakReference<>(context);
    }
  }

}
