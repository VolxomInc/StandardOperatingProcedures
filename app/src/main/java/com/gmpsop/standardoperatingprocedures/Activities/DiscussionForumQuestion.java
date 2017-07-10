package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.DiscussionCommentListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.gmpsop.standardoperatingprocedures.AppController.TAG;

public class DiscussionForumQuestion extends Activity implements View.OnClickListener{

    TextView questionTextView, questionAnswerCountTextView, questionViewCountTextView,
            questionTagsTextView, noCommentsTextView;
    EditText commentEditText;
//    EditText searchQuestion;
    RelativeLayout postComment, search;
    ListView commentsListView;
    DiscussionCommentListAdapter myAdapter;
    ArrayList<com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestionComment> commentsList = new ArrayList<>();
    com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion question;

    private SessionManager session;
    private ProgressDialog pDialog;

    LinearLayout loginCreateAcc, logoutPost;
    RelativeLayout loginButton, createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_forum_question);

        question =
                (com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion) getIntent().getSerializableExtra("question");
        Log.d(TAG, "dfq.java received question tags, comments, views : "
                + question.getTags() + ", " + question.getAns() + ", " + question.getViews());

        init_components();
    }

    public void init_components() {

        session = new SessionManager(getApplicationContext());

        //copied from post - ignore naming
        loginCreateAcc = (LinearLayout) findViewById(R.id.post_question_loginSignUpLayout);
        logoutPost = (LinearLayout) findViewById(R.id.post_question_logoutLayout);
        loginButton = (RelativeLayout) findViewById(R.id.post_question_LoginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (RelativeLayout) findViewById(R.id.post_question_SignUpButton);
        createAccountButton.setOnClickListener(this);

        noCommentsTextView = (TextView) findViewById(R.id.no_comments);
        noCommentsTextView.setVisibility(GONE);

        questionTextView = (TextView) findViewById(R.id.discussion_forum_question);
        questionTextView.setText(question.getQuestion());

        questionAnswerCountTextView = (TextView) findViewById(R.id.question_answer_count);
        questionAnswerCountTextView.setText(question.getAns());

        questionViewCountTextView = (TextView) findViewById(R.id.question_view_count);
        questionViewCountTextView.setText(question.getViews());

        questionTagsTextView = (TextView) findViewById(R.id.discussion_forum_question_tags);
        questionTagsTextView.setText(question.getTags());

        commentEditText = (EditText) findViewById(R.id.discussion_forum_comment);

        postComment = (RelativeLayout) findViewById(R.id.discussion_forum_post_comment_button);
        postComment.setOnClickListener(this);

        commentsListView = (ListView) findViewById(R.id.discussion_forum_questions_list);

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Pulling Comments...");
        pDialog.setCancelable(false);
        pDialog.show();
        pullComments();

        myAdapter = new DiscussionCommentListAdapter(this,R.layout.list_view_discuss_forum_question_comment, commentsList);
        commentsListView.setAdapter(myAdapter);

        //increase view count if user is logged in
        if(session.isLoggedIn()) {
            new Thread(new Runnable() {
                public void run() {
                    increaseViewCount(question.getId());
                }
            }).start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.discussion_forum_post_comment_button:
                //check if logged in
                if(session.isLoggedIn()) {
                    //check if comment empty
                    if (commentEditText.getText().toString().trim().equals("")) {
                        MyToast.showLong(getApplicationContext(), "Comment can not be empty");
                    } else {
                        postComment();
                    }

                } else {
                    MyToast.showLong(getApplicationContext(), "You need to be logged in to comment");
                }
                break;
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

    public void pullComments() {
        String tag_string_req = "req_pull_comments";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_QUESTION_ID, question.getId());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.FORUM_COMMENTS, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "comments Response: " + response.toString());
                pDialog.dismiss();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        MyToast.showLong(getApplicationContext(), "comments pulled successfully");
                        Log.d(TAG, "comments response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));

                        //add values to adapter
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            commentsList.add(new com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestionComment(
                                    list.getJSONObject(i).getString(Constants.PARAMETER_COMMENT),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_QUESTION_ID) ,
                                    list.getJSONObject(i).getString(Constants.PARAMETER_COMMENTED_BY))
                            );
                        }

                        noCommentsTextView.setVisibility(View.GONE);
                        commentsListView.setVisibility(View.VISIBLE);

                        myAdapter.notifyDataSetChanged();


                    } else {
//                        MyToast.showLong(getApplicationContext(),
//                                "Error pulling comments or no comments");
                        Log.e(TAG, "Error while pulling comments");

                        noCommentsTextView.setVisibility(View.VISIBLE);
                        commentsListView.setVisibility(GONE);
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
                Log.e(TAG, "pulling comments Error: " + error.getMessage());
//                MyToast.showShort(getApplicationContext(),
//                        error.getMessage());
                    pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }



    public void postComment() {
        String tag_string_req = "req_post_comment";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_QUESTION_ID, question.getId());
        params.put(Constants.PARAMETER_COMMENT, commentEditText.getText().toString());
        params.put(Constants.PARAMETER_COMMENTED_BY, session.getUserDetail().getEmail());
        Log.e(TAG, "post comment: " + question.getId() + ", " + commentEditText.getText().toString() + ", "
        + session.getUserDetail().getEmail());

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.INSERT_COMMENT, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "post comment Response: " + response.toString());
//                pDialog.dismiss();
//                    hideDialog();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
//                        MyToast.showLong(getApplicationContext(), "post comment successfully");
//                        Log.d(TAG, "post comment response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));

                        insertNotification();

                        //refresh activity
                        startActivity(getIntent());
                        finish();

                    } else {
//                        MyToast.showLong(getApplicationContext(),
//                                "Error while post comment");
                        Log.e(TAG, "Error while post comment");

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
                Log.e(TAG, "post comment Error: " + error.getMessage());
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    @Override
    protected void onResume() {

        if (session.isLoggedIn()) {
            logoutPost.setVisibility(View.VISIBLE);
            loginCreateAcc.setVisibility(GONE);
        } else {
            logoutPost.setVisibility(GONE);
            loginCreateAcc.setVisibility(View.VISIBLE);
        }

        myAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void insertNotification() {
        String tag_string_req = "req_insert_notification";

//        1- notification_text 2- email 3- notification_date 4- status
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_NOTIFICATION_TEXT, "You have successfully commented on a question in Community Forum.");
        params.put(Constants.PARAMETER_EMAIL, session.getUserDetail().getEmail());
        long unixTime = System.currentTimeMillis() / 1000L;
        params.put(Constants.PARAMETER_NOTIFICATION_DATE, String.valueOf(unixTime));
        params.put(Constants.PARAMETER_STATUS, String.valueOf(0));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.INSERT_NOTIFICATION, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "INSERT_NOTIFICATION Response: " + response.toString());

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
//                        MyToast.showLong(getApplicationContext(), "INSERT_NOTIFICATION successfully");
                    } else {
//                        MyToast.showLong(getApplicationContext(),
//                                "Error while INSERT_NOTIFICATION");
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
                Log.e(TAG, "INSERT_NOTIFICATION Error: " + error.getMessage());
//                MyToast.showShort(getApplicationContext(),
//                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void increaseViewCount(String question_id) {
        String tag_string_req = "req_increase_view_count";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_VIEWER_EMAIL, session.getUserDetail().getEmail());
//        Log.d(TAG, "user email: " + session.getUserDetail().getEmail());
        params.put(Constants.PARAMETER_QUESTION_ID, question_id);
        Log.d(TAG, "sending increase_viewcount with viewer_email & question_id: "
                + session.getUserDetail().getEmail() + ", " + question_id);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.QUESTION_VIEWS, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "increase viewcount Response: " + response.toString());

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        Log.d(TAG, "increase viewcount response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));

                    } else {
                        Log.e(TAG, "Error while increase viewcount");
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
                Log.e(TAG, "increase viewcount Error: " + error.getMessage());
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
//                    hideDialog();
            }
        });
        if(AppController.getInstance() == null) {
            Log.e(TAG, "instance is null");
        }
        Log.e(TAG, jsonRequest.getBody().toString());
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }
}
