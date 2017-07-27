package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.NotificationListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.Models.NotificationModel;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.gmpsop.standardoperatingprocedures.AppController.TAG;

public class Notifications extends Activity implements View.OnClickListener {

    TextView noNotificationsTextView;
    ListView notificationsListView;
    NotificationListAdapter myAdapter;
    ArrayList<NotificationModel> notificationsList = new ArrayList<>();

    private SessionManager session;
    private ProgressDialog pDialog;

    LinearLayout loginCreateAcc;
    RelativeLayout loginButton, createAccountButton;

    int notification_id;
    String notification_text;
    long notification_time;
    String email;
    int status_reading;
    int status_receiving;

    LinearLayout errorLayout;
    ImageView errorImageView;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);
        init_components();
        if (session.isLoggedIn()) {
            pDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        pullNotifications();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            //startService(new Intent(this, NotificationChecking.class).putExtra(Constants.EMAIL, session.getUserDetail().getEmail()));
        }
    }

    public void init_components() {
        session = new SessionManager(getApplicationContext());
        errorLayout = (LinearLayout) findViewById(R.id.documentReaderErrorLayout);
        errorImageView = (ImageView) findViewById(R.id.documentReaderErrorImage);
        errorTextView = (TextView) findViewById(R.id.documentReaderErrorMsg);
        loginCreateAcc = (LinearLayout) findViewById(R.id.post_question_loginSignUpLayout);
        loginButton = (RelativeLayout) findViewById(R.id.post_question_LoginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (RelativeLayout) findViewById(R.id.discussion_forum_question_SignUpButton);
        createAccountButton.setOnClickListener(this);
        noNotificationsTextView = (TextView) findViewById(R.id.no_notifications);
        noNotificationsTextView.setVisibility(GONE);
        notificationsListView = (ListView) findViewById(R.id.notifications_list);
        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Pulling Notifications...");
        pDialog.setCancelable(false);


        //build notification model + hook it up
        myAdapter = new NotificationListAdapter(this, R.layout.list_view_notification, notificationsList);
        notificationsListView.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_question_LoginButton:
                if (!session.isLoggedIn()) {
                    Intent loginIntent = new Intent(this,
                            Login.class);
                    loginIntent.putExtra(Constants.INTENT_LOGIN_FROM, Constants.LOGIN_FROM_POST_QUESTION);
                    startActivity(loginIntent);
                } else {
                    //session.setLogin(false);
                    MyToast.showShort(this, "You are already loggedIn");
                }
                break;
            case R.id.discussion_forum_question_SignUpButton:
                Intent signUpIntent = new Intent(this,
                        SignUpMembers.class);
                startActivity(signUpIntent);
                break;
        }
    }

    public void pullNotifications() {
        String tag_string_req = "req_pull_notifications";

        Map<String, String> params = new HashMap<String, String>();
        //set params for pulling notifications
        params.put(Constants.PARAMETER_EMAIL, session.getUserDetail().getEmail());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.NOTIFICATIONS_BY_EMAIL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "notifications Response a: " + response.toString());
                pDialog.dismiss();
                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        notificationsList.clear();
                        Log.d(TAG, "notifications response a: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));
                        //add values to adapter
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            status_receiving = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_STATUS_RECEIVING));
                            status_reading = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_STATUS_READING));
                            email = list.getJSONObject(i).getString(Constants.PARAMETER_EMAIL);
                            notification_time = Long.parseLong(list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_TIME));
                            notification_text = list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_TEXT);
                            notification_id = Integer.parseInt(list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_ID));

                            notificationsList.add(new NotificationModel(
                                    notification_id,
                                    notification_text,
                                    notification_time,
                                    email,
                                    status_reading,
                                    status_receiving)
                            );
                        }
                        if (notificationsList.size() == 0) {
                            noNotificationsTextView.setVisibility(View.VISIBLE);
                            notificationsListView.setVisibility(GONE);
                        } else {
                            noNotificationsTextView.setVisibility(View.GONE);
                            notificationsListView.setVisibility(View.VISIBLE);
                        }

                        myAdapter.notifyDataSetChanged();


                    } else {
//                        MyToast.showLong(getApplicationContext(),
//                                "Error pulling comments or no comments");
                        Log.e(TAG, "Error while pulling notifications");

                        noNotificationsTextView.setVisibility(View.VISIBLE);
                        notificationsListView.setVisibility(GONE);
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
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }


//    public void setNotificationRead(int i1) {
//        String tag_string_req = "req_post_comment";
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(Constants.PARAMETER_COMMENT,notificationsList.get(i1).);
//        params.put(Constants.PARAMETER_STATUS, String.valueOf(0));
//        params.put(Constants.PARAMETER_COMMENTED_BY, session.getUserDetail().getEmail());
//        Log.e(TAG, "post comment: " + question.getId() + ", " + commentEditText.getText().toString() + ", "
//        + session.getUserDetail().getEmail());
//
//        JSONObject parameters = new JSONObject(params);
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.INSERT_COMMENT, parameters, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                //TODO: handle success
//                Log.d(TAG, "post comment Response: " + response.toString());
////                pDialog.dismiss();
////                    hideDialog();
//
//                try {
//                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
//                    if (error == 0) {
////                        MyToast.showLong(getApplicationContext(), "post comment successfully");
////                        Log.d(TAG, "post comment response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));
//
//                        //refresh activity
//                        startActivity(getIntent());
//                        finish();
//
//                    } else {
////                        MyToast.showLong(getApplicationContext(),
////                                "Error while post comment");
//                        Log.e(TAG, "Error while post comment");
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                //TODO: handle failure
//                Log.e(TAG, "post comment Error: " + error.getMessage());
//                MyToast.showShort(getApplicationContext(),
//                        error.getMessage());
////                    hideDialog();
//            }
//        });
//        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
//    }

    @Override
    protected void onResume() {

        if (session.isLoggedIn()) {
//            logoutPost.setVisibility(View.VISIBLE);
            loginCreateAcc.setVisibility(GONE);

        } else {
//            logoutPost.setVisibility(GONE);
            loginCreateAcc.setVisibility(View.VISIBLE);

            //hide listview if not logged in
//            noNotificationsTextView.setVisibility(View.VISIBLE);
            notificationsListView.setVisibility(GONE);
        }

//        myAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
