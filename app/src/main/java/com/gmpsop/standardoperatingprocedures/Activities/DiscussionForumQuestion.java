package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

import static com.gmpsop.standardoperatingprocedures.AppController.TAG;

public class DiscussionForumQuestion extends Activity implements View.OnClickListener{

    TextView questionTextView, questionAnswerCountTextView, questionViewCountTextView, questionTagsTextView;
    EditText commentEditText;
//    EditText searchQuestion;
    RelativeLayout postComment, search;
    ListView commentsListView;
    DiscussionCommentListAdapter myAdapter;
    ArrayList<com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestionComment> commentsList = new ArrayList<>();
    com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion question;

    private SessionManager session;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_forum_question);

        question =
                (com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion) getIntent().getSerializableExtra("question");
        Log.d(TAG, "dfq.java received question tags: " + question.getTags());

        init_components();
    }

    public void init_components() {

        session = new SessionManager(getApplicationContext());

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
        }
    }

    public void pullComments() {
        String tag_string_req = "req_pull_comments";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_QUESTION_ID, question.getId());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, Constants.FORUM_COMMENTS, parameters, new Response.Listener<JSONObject>() {
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

                        myAdapter.notifyDataSetChanged();


                    } else {
                        MyToast.showLong(getApplicationContext(),
                                "Error pulling comments or no comments");
                        Log.e(TAG, "Error while pulling comments");

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
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
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
}
