package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.DiscussionQuestionListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.Models.DiscussionForumQuestion;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.gmpsop.standardoperatingprocedures.AppController.TAG;

public class DiscussionForum extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    EditText searchQuestion;
    RelativeLayout postQuestion, search;
    ListView questionsListView;
    DiscussionQuestionListAdapter myAdapter;
    ArrayList<DiscussionForumQuestion> questionsList = new ArrayList<>();
    ArrayList<DiscussionForumQuestion>  tempQuestionsList = new ArrayList<>();

    //pagination
    public int TOTAL_LIST_ITEMS = 80;
    public int NUM_ITEMS_PAGE   = 8;
    private int noOfBtns;
    private Button[] btns;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_forum);
        init_components();
    }

    public void init_components() {

        session = new SessionManager(getApplicationContext());

        searchQuestion = (EditText) findViewById(R.id.discussion_forum_search_question);
        postQuestion = (RelativeLayout) findViewById(R.id.discussion_forum_post_question_button);
        postQuestion.setOnClickListener(this);

        search = (RelativeLayout) findViewById(R.id.discussion_forum_search_button);
        search.setOnClickListener(this);
        searchQuestion = (EditText) findViewById(R.id.discussion_forum_search_question);

        questionsListView = (ListView) findViewById(R.id.discussion_forum_questions_list);
        questionsListView.setOnItemClickListener(this);

//        populateDummyList();
        pullQuestions();
        myAdapter = new DiscussionQuestionListAdapter(this,R.layout.list_view_discuss_forum_questionf, questionsList);
        questionsListView.setAdapter(myAdapter);

        //search functionality
        questionsListView.setTextFilterEnabled(true);

    }

    public void populateDummyList(){
        questionsList.clear();
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
//        questionsList.add(new DiscussionForumQuestion("I have some problem with SignUp?","2 Answers" , "2 Views"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.discussion_forum_post_question_button:
                startActivity(new Intent(this, PostQuestion.class));
                break;
            case R.id.discussion_forum_search_button:
//                MyToast.showShort(this, "Search Clicked");

                String s = searchQuestion.getText().toString();
                if(s.equals("")){
                    questionsList.clear();
                    questionsList.addAll(tempQuestionsList);
                    Log.d(TAG , s.toString());
                }else{
                    questionsList.clear();
                    for(int i =0 ; i<tempQuestionsList.size();i++){
                        Log.d(TAG , s.toString());
                        //search in questions & tags
                        if(tempQuestionsList.get(i).getQuestion().toLowerCase().contains(s.toString().toLowerCase())
                                || tempQuestionsList.get(i).getTags().toLowerCase().contains(s.toString().toLowerCase())) {
                            questionsList.add(tempQuestionsList.get(i));
                            Log.d(TAG , " Name : " +tempQuestionsList.get(i).getQuestion().toLowerCase());
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();

                break;
        }
    }

    public void pullQuestions() {
        String tag_string_req = "req_pull_questions";

        Map<String, String> params = new HashMap<String, String>();
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, Constants.FORUM_QUESTIONS, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "questions Response: " + response.toString());
//                pDialog.dismiss();
//                    hideDialog();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        MyToast.showLong(getApplicationContext(), "Questions pulled successfully");
                        Log.d(TAG, "questions response: " + response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA));

                        //add values to adapter
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            questionsList.add(new DiscussionForumQuestion(
                                    list.getJSONObject(i).getString(Constants.PARAMETER_ID),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_TITLE),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_COMMENT_COUNT),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_VIEW_COUNT),
                                    list.getJSONObject(i).getString(Constants.PARAMETER_QUESTION_TAG))
                            );
                        }

                        tempQuestionsList.addAll(questionsList);

//                        myAdapter.notifyDataSetChanged();

                        //pagination
                        Btnfooter();
                        loadList(0);
                        CheckBtnBackGroud(0);



                    } else {
                        MyToast.showLong(getApplicationContext(),
                                "Error while pulling questions");
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
                Log.e(TAG, "pulling questions Error: " + error.getMessage());
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }


    //pagination
    private void Btnfooter()
    {
        int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;
        val = val==0?0:1;
        noOfBtns=TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;

        LinearLayout ll = (LinearLayout)findViewById(R.id.btnLay);

        btns = new Button[noOfBtns];

        for(int i=0;i<noOfBtns;i++)
        {
            btns[i] = new Button(this);
            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btns[i].setText(""+(i+1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(btns[i], lp);

            final int j = i;
            btns[j].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v)
                {
                    loadList(j);
                    CheckBtnBackGroud(j);
                }
            });
        }

    }

    /**
     * Method for Checking Button Backgrounds
     */
    private void CheckBtnBackGroud(int index)
    {
//        title.setText("Page "+(index+1)+" of "+noOfBtns);
        for(int i=0;i<noOfBtns;i++)
        {
            if(i==index)
            {
                btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_circle_button));
                btns[i].setTextColor(getResources().getColor(android.R.color.white));
//                btns[i].setTextSize(8);
            }
            else
            {
                btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btns[i].setTextColor(getResources().getColor(R.color.input_field_background));
//                btns[i].setTextSize(6);
            }
        }

    }

    /**
     * Method for loading data in listview
     * @param number
     */
    private void loadList(int number)
    {
        ArrayList<DiscussionForumQuestion> sort = new ArrayList<DiscussionForumQuestion>();

        int start = number * NUM_ITEMS_PAGE;
        for(int i=start;i<(start)+NUM_ITEMS_PAGE;i++)
        {
            if(i<questionsList.size())
            {
                sort.add(questionsList.get(i));
            }
            else
            {
                break;
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    //question clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        Log.d(TAG, "clicked question id: " + i);

        //increase view count if user is logged in
        if(session.isLoggedIn()) {
            new Thread(new Runnable() {
                public void run() {
                    increaseViewCount(questionsList.get(i).getId(), questionsList.get(i));
                }
            }).start();
        }
//        else {
//            MyToast.showLong(getApplicationContext(), "You need to be logged in to increase count");
//        }

        Intent intent = new Intent(getApplicationContext(), com.gmpsop.standardoperatingprocedures.Activities.DiscussionForumQuestion.class);
        intent.putExtra("question", questionsList.get(i));
        startActivity(intent);
    }

    public void increaseViewCount(String question_id , final DiscussionForumQuestion question) {
        String tag_string_req = "req_increase_view_count";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_VIEWER_EMAIL, session.getUserDetail().getEmail());
//        Log.d(TAG, "user email: " + session.getUserDetail().getEmail());
        params.put(Constants.PARAMETER_QUESTION_ID, question_id);
        Log.d(TAG, "sending increase_viewcount with user_email & question_id: "
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
