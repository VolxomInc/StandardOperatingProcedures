package com.gmpsop.standardoperatingprocedures.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.Models.NotificationModel;
import com.gmpsop.standardoperatingprocedures.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationModel> {

    Context context;
    TextView notificationTextView, notificationDateTextView;
    ImageView forwardButton;
    LinearLayout listItemLayout;


    public NotificationListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NotificationModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        /*if (notification.getReceiving_status() == 0) {
            listItemLayout.setBackgroundColor(context.getResources().getColor(colorPrimaryLight2));
        }*/
        notificationTextView.setText(notification.getNotificationText());
        Date dateObject = new Date(notification.getNotification_date() * 1000);
        notificationDateTextView.setText(formatDate(dateObject));

        return view;
    }

    public void init_components(View view) {
        listItemLayout = (LinearLayout) view.findViewById(R.id.listViewNotificationLayout);
        notificationTextView = (TextView) view.findViewById(R.id.listViewNotificationText);
        notificationDateTextView = (TextView) view.findViewById(R.id.listViewNotificationDate);
//        views = (TextView) view.findViewById(R.id.listViewDiscussionQuestionNoViews);
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
