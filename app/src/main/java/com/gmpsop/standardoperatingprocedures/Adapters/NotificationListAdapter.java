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

import com.gmpsop.standardoperatingprocedures.Models.NotificationModel;
import com.gmpsop.standardoperatingprocedures.R;

import java.util.List;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationModel> {

    Context context;
    TextView notificationTextView, notificationDateTextView;
    ImageView forwardButton;


    public NotificationListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NotificationModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_view_notification, parent, false);
        }
        init_components(view);
        NotificationModel notification = getItem(position);
        notificationTextView.setText(notification.getNotificationText());

//        String notificationDate = notification.getNotificationDate();
//        long dv = Long.valueOf(notificationDate)*1000; // its need to be in milisecond
//        Date df = new java.util.Date(dv);
//        String vv = new SimpleDateFormat("MM dd, yyyy hh:mma").format(df);
//        notificationDateTextView.setText(vv);

//        commented_by.setText(forumQuestionComment.getCommentedBy());
//        views.setText(forumQuestionComment.getViews());
        return view;
    }

    public void init_components(View view) {
        notificationTextView = (TextView) view.findViewById(R.id.listViewNotificationText);
        notificationDateTextView = (TextView) view.findViewById(R.id.listViewNotificationDate);
//        views = (TextView) view.findViewById(R.id.listViewDiscussionQuestionNoViews);
    }
}
