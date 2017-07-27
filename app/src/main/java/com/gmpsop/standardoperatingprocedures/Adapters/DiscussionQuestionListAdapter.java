package com.gmpsop.standardoperatingprocedures.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion;
import com.gmpsop.standardoperatingprocedures.R;

import java.util.List;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class DiscussionQuestionListAdapter extends ArrayAdapter<DiscussionForumQuestion> {

    Context context;
    TextView question, ans, views;
    ImageView forwardButton;


    public DiscussionQuestionListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DiscussionForumQuestion> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_view_discuss_forum_questionf, parent, false);
        }
        init_components(view);
        DiscussionForumQuestion forumQuestion = getItem(position);
        question.setText(forumQuestion.getTitle());
        ans.setText(forumQuestion.getAns());
        views.setText(forumQuestion.getViews());
        return view;
    }

    public void init_components(View view) {
        question = (TextView) view.findViewById(R.id.listViewDiscussionQuestion);
        ans = (TextView) view.findViewById(R.id.listViewDiscussionNoAnswers);
        views = (TextView) view.findViewById(R.id.listViewDiscussionQuestionNoViews);
    }
}
