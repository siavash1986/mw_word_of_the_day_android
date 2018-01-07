package me.siavash.android.wotd.activities;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import me.siavash.android.wotd.R;
import me.siavash.android.wotd.util.SmoothLayoutManager;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.entities.model.WordViewModel;
import me.siavash.android.wotd.listeners.DataSourceChangeListener;
import me.siavash.android.wotd.util.DifferedEntityUpdate;
import me.siavash.android.wotd.util.Utils;


public class CardFragment extends Fragment implements DataSourceChangeListener {

  private final String TAG = this.getClass().getSimpleName();
  private RecyclerView MyRecyclerView;
  private WordAdapter adapter;
  private WordViewModel viewModel;

  public void notifyDateRequest(LocalDate dateRequest) {
    int dx = Days.daysBetween(dateRequest, Utils.getLatestWordLocalDate(getContext())).getDays();
    if (MyRecyclerView != null) {
      MyRecyclerView.smoothScrollToPosition(dx);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public void onResume() {
    super.onResume();
//        adapter.notifyDataSetChanged();
  }

  @Override
  public void onStop() {
    super.onStop();
    DifferedEntityUpdate.getInstance(getContext()).updateEntities();
  }

  @Override
  public void onPause() {
    super.onPause();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    viewModel = ViewModelProviders.of(this).get(WordViewModel.class);
    adapter = new WordAdapter(getActivity().getLayoutInflater(), getContext());
    viewModel.pagedWord.observe(this, words -> adapter.setList(words));

    View view = inflater.inflate(R.layout.fragment_card, container, false);
    MyRecyclerView = view.findViewById(R.id.cardView);
    MyRecyclerView.setHasFixedSize(true);
    LinearLayoutManager MyLayoutManager = new SmoothLayoutManager(getActivity());
    MyLayoutManager.setSmoothScrollbarEnabled(true);
    MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    if (MyRecyclerView != null) {
      MyRecyclerView.setAdapter(adapter);
      MyRecyclerView.setLayoutManager(MyLayoutManager);
    }

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void dataSourceModified() {

  }


  class MyViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView titleTextView;
    TextView attributeTextView;
    TextView syllablesTextView;
    TextView wordDateTextView;
    ImageView likeImageView;
    ImageView shareImageView;

    MyViewHolder(View v) {
      super(v);
      cardView = v.findViewById(R.id.card_view);
      titleTextView = v.findViewById(R.id.flashcard_title);
      attributeTextView = v.findViewById(R.id.flashcard_text_attr);
      syllablesTextView = v.findViewById(R.id.flashcard_text_syllables);
      wordDateTextView = v.findViewById(R.id.flashcard_wordDate);
      likeImageView = v.findViewById(R.id.likeImageView);
      shareImageView = v.findViewById(R.id.shareImageView);
      likeImageView.setOnClickListener(view -> {
        int id = (int) likeImageView.getTag(0xffffffff);
        if (id == R.drawable.ic_like) {

          likeImageView.setImageResource(R.drawable.ic_liked);
          likeImageView.setTag(0xffffffff, R.drawable.ic_liked);
          Word w = (Word) likeImageView.getTag(0xfffffffe);
          DifferedEntityUpdate.getInstance(getContext()).addToFavorites(w);

        } else {
          likeImageView.setImageResource(R.drawable.ic_like);
          likeImageView.setTag(0xffffffff, R.drawable.ic_like);
          Word w = (Word) likeImageView.getTag(0xfffffffe);
          DifferedEntityUpdate.getInstance(getContext()).removeFromFavorites(w);
        }
      });


      shareImageView.setOnClickListener(view -> {

        String message = "Merriam Webster Word Of The Day For " +
            Utils.dateFormatter(wordDateTextView.getText()) + " is: " + titleTextView.getText() +
            System.getProperty("line.separator") + System.getProperty("line.separator") +
            "https://www.merriam-webster.com/word-of-the-day/" + wordDateTextView.getText();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
      });

      cardView.setOnClickListener(view -> {
        Intent flashCardIntent = new Intent(getContext(), WordActivity.class);
        flashCardIntent.putExtra("EXTRA_KEY", cardView.getTag().toString());
        startActivity(flashCardIntent);
      });


    }

    void bind(Word word) {
      cardView.setTag(word.getDate());
      titleTextView.setText(word.getTitle());
      wordDateTextView.setText(word.getDate());
      attributeTextView.setText(word.getAttribute());
      syllablesTextView.setText(word.getSyllables());
      boolean isFavorite = (word.isFav() || DifferedEntityUpdate.getInstance(getContext()).isPendingFavorite(word)) &&
          (!DifferedEntityUpdate.getInstance(getContext()).isPendingRemove(word));
      if (isFavorite) {
        likeImageView.setImageResource(R.drawable.ic_liked);
        likeImageView.setTag(0xffffffff, R.drawable.ic_liked);
      } else {
        likeImageView.setImageResource(R.drawable.ic_like);
        likeImageView.setTag(0xffffffff, R.drawable.ic_like);
      }
      likeImageView.setTag(0xfffffffe, word);
      shareImageView.setTag(word.getDate());
    }

    void clear() {
      titleTextView.setText(null);
      wordDateTextView.setText(null);
      attributeTextView.setText(null);
      syllablesTextView.setText(null);
    }

  }


  private class WordAdapter extends PagedListAdapter<Word, MyViewHolder> {
    private final LayoutInflater inflater;

    WordAdapter(LayoutInflater inflater, Context context) {
      super(WORDS_DIFF);
      this.inflater = inflater;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return (new MyViewHolder(inflater.inflate(R.layout.recycle_items, parent, false)));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
      Word word = getItem(position);

      if (word == null) {
        holder.clear();
      } else {
        holder.bind(word);
      }
    }
  }

  static final DiffCallback<Word> WORDS_DIFF = new DiffCallback<Word>() {
    @Override
    public boolean areItemsTheSame(@NonNull Word oldItem,
                                   @NonNull Word newItem) {
      return (oldItem.equals(newItem));
    }

    @Override
    public boolean areContentsTheSame(@NonNull Word oldItem,
                                      @NonNull Word newItem) {
      return (areItemsTheSame(oldItem, newItem));
    }
  };
}
