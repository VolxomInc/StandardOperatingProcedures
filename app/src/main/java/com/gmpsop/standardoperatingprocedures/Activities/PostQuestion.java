package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.GMPSuggestionsListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mabbas007.tagsedittext.TagsEditText;

import static android.view.View.GONE;

public class PostQuestion extends Activity implements View.OnClickListener {
    private static final String TAG = PostQuestion.class.getSimpleName();
    private SessionManager session;
    LinearLayout loginCreateAcc, logoutPost;
    RelativeLayout loginButton, createAccountButton, postButton;

    EditText questionTitle, questionDetail;
    TagsEditText questionTags;

    private ProgressDialog pDialog;
    ListView termTags ;
    GMPSuggestionsListAdapter TagsListAdapter;
    ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        init_components();
        termTags.setAdapter(TagsListAdapter);
        termTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> tagslistordinary = questionTags.getTags();
                tagslistordinary.add(tags.get(position));
                String[] arrau = tagslistordinary.toArray(new String[tagslistordinary.size()]);
                questionTags.setTags(arrau);
                termTags.setVisibility(GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            logoutPost.setVisibility(View.VISIBLE);
            loginCreateAcc.setVisibility(GONE);
        } else {
            logoutPost.setVisibility(GONE);
            loginCreateAcc.setVisibility(View.VISIBLE);
        }
    }

    public void init_components() {
        session = new SessionManager(getApplicationContext());
        loginCreateAcc = (LinearLayout) findViewById(R.id.post_question_loginSignUpLayout);
        logoutPost = (LinearLayout) findViewById(R.id.post_question_logoutLayout);
        loginButton = (RelativeLayout) findViewById(R.id.post_question_LoginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (RelativeLayout) findViewById(R.id.post_question_SignUpButton);
        loginButton.setOnClickListener(this);
        postButton = (RelativeLayout) findViewById(R.id.post_question_button);
        postButton.setOnClickListener(this);
        questionTitle = (EditText) findViewById(R.id.post_question_title);
        questionDetail = (EditText) findViewById(R.id.post_question_detail);
        questionTags = (TagsEditText) findViewById(R.id.post_question_tags);
        questionTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    termTags.setVisibility(GONE);
                } else {

                    String[] splitter = s.toString().split(" ");

                    if (splitter.length > 0)
                        suggestionGmp(splitter[splitter.length - 1]);
                    else {
                        suggestionGmp(s.toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Please wait while Posting...");
        pDialog.setCancelable(false);
        
        termTags = (ListView) findViewById(R.id.post_question_termTags);
        TagsListAdapter = new GMPSuggestionsListAdapter(this, R.layout.list_view_search_gmp_suggestions, tags);
    }

    public void validate_form() {
        if (questionTitle.getText().toString().equals("")) {
            questionTitle.setError("Please Enter Title");
            return;
        }
        if (questionDetail.getText().toString().equals("")) {
            questionDetail.setError("Please Enter question Detail");
            return;
        }
        if (questionTags.getTags().size() < 2) {
            questionTags.setError("Please Enter at least two(2) tags");
            return;
        }
        pDialog.show();

        postQuestion(questionTitle.getText().toString(), questionDetail.getText().toString(), TextUtils.substring(string_format(questionTags.getTags()), 1, string_format(questionTags.getTags()).length()), session.getUserDetail().getEmail());

        /*tags.add("hello");
        */
/*
        */

    }

    public String string_format(List<String> list) {
        String formatted = "";
        for (int i = 0; i < list.size(); i++) {
            formatted = formatted + "," + list.get(i);
        }
        return formatted;
    }

    public void postQuestion(String title, String detail, String tags, String senderEmail) {
        String tag_string_req = "req_register";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_TITLE, title);
        params.put(Constants.PARAMETER_DETAIL, detail);
        params.put(Constants.PARAMETER_TAGS, tags);
        params.put(Constants.PARAMETER_SENDER_ID, senderEmail);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.POST_QUESTION, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                pDialog.dismiss();
//                    hideDialog();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        MyToast.showLong(getApplicationContext(), "Question Posted successfully");

                        //Trigger insert_notification for this
                        insertNotification();

                        PostQuestion.this.finish();
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                "Error while posting Question");
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }


    public void suggestionGmp(final String gmpTerm) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_SUGGESTION_TAGS, gmpTerm);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.GMP_TERM_SEARCH_SUGGESTED_TAGS_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    tags.clear();
                    if (resMsg == 0) {
                        termTags.setVisibility(View.VISIBLE);
                        JSONArray suggestions = response.getJSONArray("tags");

                        for (int i = 0; i < suggestions.length(); i++) {
                            tags.add(suggestions.getString(i));
                        }
                    } else {
                        termTags.setVisibility(GONE);
                    }
                    TagsListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    termTags.setVisibility(GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*hideProgressBar();
                showError(errorLoading);*/
                termTags.setVisibility(GONE);
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());

//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
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
            case R.id.post_question_SignUpButton:
                Intent signUpIntent = new Intent(this,
                        SignUpMembers.class);
                startActivity(signUpIntent);
                break;
            case R.id.post_question_button:
                validate_form();
                break;
        }
    }



    public void insertNotification() {
        String tag_string_req = "req_insert_notification";

//        1- notification_text 2- email 3- notification_date 4- status
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_NOTIFICATION_TEXT, "You have successfully posted a question in Community Forum.");
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
                pDialog.dismiss();
//                    hideDialog();

                try {
                    int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (error == 0) {
                        MyToast.showLong(getApplicationContext(), "INSERT_NOTIFICATION successfully");
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                "Error while INSERT_NOTIFICATION");
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
                MyToast.showShort(getApplicationContext(),
                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }
}
