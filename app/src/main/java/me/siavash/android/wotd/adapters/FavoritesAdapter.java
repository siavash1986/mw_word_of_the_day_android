package me.siavash.android.wotd.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.siavash.android.wotd.entities.Word;

public class FavoritesAdapter extends ArrayAdapter<Word> {

  private Context mContext;
  private int mResourceLayout;
  private int mResourceTextView;

  public FavoritesAdapter(@NonNull Context context, int resource, @NonNull List<Word> objects, int textViewResource) {
    super(context, resource, objects);
    this.mContext = context;
    this.mResourceTextView = textViewResource;
    this.mResourceLayout = resource;
  }

  @SuppressWarnings("ConstantConditions")
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(mResourceLayout, parent, false);
    }

    ((TextView) convertView.findViewById(mResourceTextView)).setText(getItem(position).getTitle());
    convertView.setTag(getItem(position).getDate());
    return convertView;
  }
}
