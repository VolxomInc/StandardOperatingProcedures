package com.gmpsop.standardoperatingprocedures.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Models.GMPFiles;
import com.gmpsop.standardoperatingprocedures.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BD1 on 09-May-17.
 */

public class GMPSearchFileListAdapter extends ArrayAdapter<GMPFiles> implements Filterable{

    ImageView imageView;
    TextView fileName;
    Context context;
    GMPFiles gmpFiles;
    List<GMPFiles> objs;

    public GMPSearchFileListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<GMPFiles> objects) {
        super(context, resource, objects);
        objs = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_view_search_gmp_file, parent, false);
        }
        init_components(view);
        gmpFiles = getItem(position);
        fileName.setText(gmpFiles.getName());
        if(gmpFiles.getType().equals(Constants.TYPE_FILE)){
            imageView.setImageResource(R.drawable.document_file2);
        }else{
            imageView.setImageResource(R.drawable.document_folder2);
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                objs = (List<GMPFiles>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<GMPFiles> FilteredArrayNames = new ArrayList<GMPFiles>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < objs.size(); i++) {
                    GMPFiles dataNames = objs.get(i);
                    if (dataNames.getName().toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }

    public void init_components(View view){
        imageView = (ImageView)view.findViewById(R.id.listViewSearchGmpFolderImageView);
        fileName = (TextView)view.findViewById(R.id.listViewSearchGmpFolderTextView);
    }
}
