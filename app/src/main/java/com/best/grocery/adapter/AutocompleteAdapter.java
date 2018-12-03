package com.best.grocery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;
import com.best.grocery.service.ProductService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AutocompleteAdapter extends ArrayAdapter<String> {
    List<String> tempItems, suggestions;
    private static final int MAX_ITEM_AUTOCOMPLETE = 6;
    private OnItemClickListener listener;
    private ProductService mService;


    public AutocompleteAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);
        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
        mService = new ProductService(context);
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (row == null) {
            row = inflater.inflate(R.layout.item_autocomplete, parent, false);
        }
        try {
            final String text = suggestions.get(position);
            TextView autoItem = row.findViewById(R.id.text);
            autoItem.setText(text);
            autoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(text);
                    }

                }
            });
            ImageView imageDelete = row.findViewById(R.id.image_delete);
            imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mService.deleteItemAutocomplete(text);
                    tempItems.remove(text);
                    suggestions.remove(position);
                    clear();
                    addAll(suggestions);

                }
            });

        } catch (Exception e) {
            Log.w("Error", "Autocomplete: " + e.getMessage());
        }
        return row;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }


    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = resultValue.toString();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                int dem = 0;
                ArrayList<String> listPrefix = new ArrayList<>();
                //luu cac tu matching startWith khong can dau
                ArrayList<String> listPrefix2 = new ArrayList<>();
                ArrayList<String> listContains = new ArrayList<>();
                String constraintString = constraint.toString().toLowerCase().trim();
                suggestions.clear();
                for (String text : tempItems) {
                    if (dem == MAX_ITEM_AUTOCOMPLETE) break;
                    if (text.toLowerCase().startsWith(constraintString)) {
                        listPrefix.add(text);
                        dem++;
                    }
                }
                for (String text : tempItems) {
                    if (dem == MAX_ITEM_AUTOCOMPLETE) break;
                    if (removeCharacterSpecial(text).startsWith(removeCharacterSpecial(constraintString)) && !listPrefix.contains(text)) {
                        listPrefix2.add(text);
                        dem++;
                    }
                }
                for (String text : tempItems) {
                    if (dem == MAX_ITEM_AUTOCOMPLETE) break;
                    if (text.toLowerCase().contains(constraintString) && !listPrefix.contains(text) && !listPrefix2.contains(text)) {
                        listContains.add(text);
                        dem++;
                    }
                }

                suggestions.addAll(listPrefix);
                suggestions.addAll(listPrefix2);
                suggestions.addAll(listContains);

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                Log.d("BBBB", "AAAA");
                if (results != null && results.count > 0) {
                    clear();
                    addAll(suggestions);
                }
            } catch (Exception e) {
                Log.e("Error", "filter: " + e.getMessage());
            }
        }
    };

    private String removeCharacterSpecial(String text) {
        try {
            String temp = Normalizer.normalize(text, Normalizer.Form.NFD).trim();
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String result = pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("đ", "d")
                    .replaceAll("[^a-zA-Z0-9- ]", "");
            return result;
        } catch (Exception e) {
            return text;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
