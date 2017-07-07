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

import com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestionComment;
import com.gmpsop.standardoperatingprocedures.R;

import java.util.List;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class DiscussionCommentListAdapter extends ArrayAdapter<DiscussionForumQuestionComment> {

    Context context;
    TextView comment, commented_by, views;
    ImageView forwardButton;


    public DiscussionCommentListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DiscussionForumQuestionComment> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_view_discuss_forum_question_comment, parent, false);
        }
        init_components(view);
        DiscussionForumQuestionComment forumQuestionComment = getItem(position);
        comment.setText(forumQuestionComment.getComment());
        commented_by.setText(forumQuestionComment.getCommentedBy());
//        views.setText(forumQuestionComment.getViews());
        return view;
    }

    public void init_components(View view) {
        comment = (TextView) view.findViewById(R.id.listViewDiscussionQuestionCommentText);
        commented_by = (TextView) view.findViewById(R.id.listViewDiscussionQuestionCommentBy);
        views = (TextView) view.findViewById(R.id.listViewDiscussionQuestionNoViews);
    }
}
