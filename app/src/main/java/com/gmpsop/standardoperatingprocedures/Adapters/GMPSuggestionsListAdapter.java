package com.gmpsop.standardoperatingprocedures.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.R;

import java.util.List;

/**
 * Created by BD1 on 09-May-17.
 */

public class GMPSuggestionsListAdapter extends ArrayAdapter<String> {

    TextView fileName;
    Context context;
    String suggestionTitle;

    public GMPSuggestionsListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_view_search_gmp_suggestions, parent, false);
        }
        init_components(view);
        suggestionTitle = getItem(position);
        fileName.setText(suggestionTitle);
        return view;
    }

    public void init_components(View view) {
        fileName = (TextView) view.findViewById(R.id.listViewSearchGmpSuggestion);
    }
}
