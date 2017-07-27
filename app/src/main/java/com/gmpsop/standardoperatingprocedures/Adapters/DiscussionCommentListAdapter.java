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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class DiscussionCommentListAdapter extends ArrayAdapter<DiscussionForumQuestionComment> {

    Context context;
    TextView comment, commented_by, commented_date;
    ImageView forwardButton;
    DiscussionForumQuestionComment forumQuestionComment;


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

        forumQuestionComment = getItem(position);
        comment.setText(forumQuestionComment.getComment());
        commented_by.setText(forumQuestionComment.getCommentedBy());
        if (forumQuestionComment.getCommentTime() != 0) {
            Date dateObject = new Date(forumQuestionComment.getCommentTime() * 1000);
            commented_date.setText(formatDate(dateObject));
        }else{
            commented_date.setText("");
        }
//        views.setText(forumQuestionComment.getViews());
        return view;
    }

    public void init_components(View view) {
        comment = (TextView) view.findViewById(R.id.listViewDiscussionQuestionCommentText);
        commented_by = (TextView) view.findViewById(R.id.listViewDiscussionQuestionCommentBy);
        commented_date = (TextView) view.findViewById(R.id.listViewDiscussionQuestionCommentDate);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
