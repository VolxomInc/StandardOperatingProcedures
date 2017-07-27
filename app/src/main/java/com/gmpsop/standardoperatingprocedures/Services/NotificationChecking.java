package com.gmpsop.standardoperatingprocedures.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Activities.Notifications;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Models.NotificationModel;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NotificationChecking extends Service {
    public final String TAG = NotificationChecking.class.getSimpleName();
    static Bundle bundle;
    String emailId;
    int NOTIFICATION_ID = 1;
    ArrayList<NotificationModel> notificationsList = new ArrayList<>();
    int notification_id;
    String notification_text;
    long notification_time;
    String email;
    int status_reading;
    int status_receiving;
    Intent myIntent;
    PendingIntent startIntent;

    public NotificationChecking() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NOTIFICATION_ID = new Random().nextInt();

        if (intent != null) {
            bundle = intent.getExtras();
        }
        if (bundle != null) {
            emailId = bundle.getString(Constants.EMAIL);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        pullNotifications(emailId);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        return START_STICKY;
    }

    public void pullNotifications(String emailId) {
        String tag_string_req = "req_pull_notifications";

        Map<String, String> params = new HashMap<String, String>();
        //set params for pulling notifications
        params.put(Constants.PARAMETER_EMAIL, emailId);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.NOTIFICATIONS_BY_EMAIL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        Log.d(TAG, "notifications Response: " + response.toString());
                        Log.d(TAG, "notifications response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));
                        notificationsList.clear();
                        //add values to adapter
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            status_receiving = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_STATUS_RECEIVING));
                            status_reading = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_STATUS_READING));
                            email = list.getJSONObject(i).getString(Constants.PARAMETER_EMAIL);
                            notification_time = Long.parseLong(list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_TIME));
                            notification_text = list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_TEXT);
                            notification_id = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_ID));
                            if (status_receiving == 0) {
                                notificationsList.add(new NotificationModel(
                                        notification_id,
                                        notification_text,
                                        notification_time,
                                        email,
                                        status_reading,
                                        status_receiving)
                                );
                            }
                        }
                        if (notificationsList.size() != 0) {
                            showNotification(notificationsList);
                        }


                    } else {
//                        MyToast.showLong(getApplicationContext(),
//                                "Error pulling comments or no comments");
                        Log.e(TAG, "Error while pulling notifications");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "pulling notifications Error: " + error.getMessage());
//                MyToast.showShort(getApplicationContext(),
//                        error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void showNotification(ArrayList<NotificationModel> noti) {

        for (int i = 0; i < noti.size(); i++) {
            NOTIFICATION_ID++;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.xhdpi_logo)
                            .setContentTitle("GMPSOP")
                            .setContentText(noti.get(i).getNotificationText());
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(uri);
            mBuilder.setAutoCancel(true);
            //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears
            myIntent = new Intent(this, Notifications.class);
            startIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent
            mBuilder.setContentIntent(startIntent);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

    }


}
