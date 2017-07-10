package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class Notifications extends Activity implements View.OnClickListener{

    TextView questionTextView, questionAnswerCountTextView, questionViewCountTextView,
            questionTagsTextView, noNotificationsTextView;
//    EditText commentEditText;
//    EditText searchQuestion;
    RelativeLayout postComment, search;
    ListView notificationsListView;
    NotificationListAdapter myAdapter;
    ArrayList<NotificationModel> notificationsList = new ArrayList<>();
//    com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion question;

    private SessionManager session;
    private ProgressDialog pDialog;

    LinearLayout loginCreateAcc, logoutPost;
    RelativeLayout loginButton, createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);
        init_components();
    }

    public void init_components() {

        session = new SessionManager(getApplicationContext());

        //copied from post - ignore naming
        loginCreateAcc = (LinearLayout) findViewById(R.id.post_question_loginSignUpLayout);
//        logoutPost = (LinearLayout) findViewById(R.id.post_question_logoutLayout);
        loginButton = (RelativeLayout) findViewById(R.id.post_question_LoginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (RelativeLayout) findViewById(R.id.post_question_SignUpButton);
        createAccountButton.setOnClickListener(this);

        noNotificationsTextView = (TextView) findViewById(R.id.no_notifications);
        noNotificationsTextView.setVisibility(GONE);

        notificationsListView = (ListView) findViewById(R.id.notifications_list);

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Pulling Notifications...");
        pDialog.setCancelable(false);
        pDialog.show();
        pullNotifications();

        //build notification model + hook it up
        myAdapter = new NotificationListAdapter(this,R.layout.list_view_notification, notificationsList);
        notificationsListView.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            case R.id.post_question_SignUpButton:
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
                Log.d(TAG, "notifications Response: " + response.toString());
                pDialog.dismiss();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        MyToast.showLong(getApplicationContext(), "notifications pulled successfully");
                        Log.d(TAG, "notifications response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));

                        //add values to adapter
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            notificationsList.add(new NotificationModel(
                                    list.getJSONObject(i).getString(Constants.PARAMETER_NOTIFICATION_TEXT),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_EMAIL) ,
                                    list.getJSONObject(i).getInt(Constants.PARAMETER_STATUS))
                            );
                        }

                        noNotificationsTextView.setVisibility(View.GONE);
                        notificationsListView.setVisibility(View.VISIBLE);

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