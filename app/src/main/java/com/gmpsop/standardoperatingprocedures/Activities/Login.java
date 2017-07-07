package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.Models.ApplicationUser;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends Activity implements View.OnClickListener {

    private static final String TAG = Login.class.getSimpleName();
    public ArrayList<String> loginResponseMsg;
    public ArrayList<String> userDetailResponseMsg;
    Handler myViewHandler;
    String loginFrom;


    private ProgressDialog pDialog;
    private SessionManager session;

    TextView forgotPassword, createAccountButton;
    EditText emailId, password;
    RelativeLayout loginButton;
    //LoginButton fbLoginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle context = getIntent().getExtras();
        loginFrom = context.getString(Constants.INTENT_LOGIN_FROM,"");
        initComponents();
    }

    public void initComponents() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (RelativeLayout) findViewById(R.id.loginButtonLayout);
        loginButton.setOnClickListener(this);
        forgotPassword = (TextView) findViewById(R.id.loginForgotPassword);
        forgotPassword.setOnClickListener(this);
        createAccountButton = (TextView) findViewById(R.id.loginCreateAccount);
        createAccountButton.setOnClickListener(this);
        emailId = (EditText) findViewById(R.id.loginEmailId);
        password = (EditText) findViewById(R.id.loginPassword);

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Please wait while Login...");
        pDialog.setCancelable(false);

        loginResponseMsg = new ArrayList<>();
        loginResponseMsg.add(Constants.LOGIN_MSG_SUCCESS);
        loginResponseMsg.add(Constants.LOGIN_MSG_FAILED);

        userDetailResponseMsg = new ArrayList<>();
        userDetailResponseMsg.add(Constants.USER_DETAIL_MSG_SUCCESS);
        userDetailResponseMsg.add(Constants.USER_DETAIL_MSG_UNSUCCESSFUL);

        session = new SessionManager(getApplicationContext());

        myViewHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                pDialog.dismiss();
                return true;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        switch (v.getId()) {
            case R.id.loginButtonLayout:
                validateLoginForm();
                break;
            case R.id.loginForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
            case R.id.loginCreateAccount:
                createAccountActivity(this);
                break;
        }
    }

    public void validateLoginForm() {
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError("Please enter login_email address");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()) {
            emailId.setError("Invalid login_email address");
            return;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Please enter your login_password");
            return;
        }
        pDialog.show();
        LoginUser backgroundTask = new LoginUser(this);
        backgroundTask.execute(emailId.getText().toString().trim(), password.getText().toString().trim());
    }

    public void createAccountActivity(Context context) {
        startActivity(new Intent(context, SignUpMembers.class));
    }


    public class LoginUser extends AsyncTask<String, Void, String> {
        Context ctx;

        LoginUser(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String pass = params[1];
            loginUser(email, pass);
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

        }

        private void loginUser(final String email, final String password) {
            // Tag used to cancel the request
            String tag_string_req = "req_register";

            Map<String, String> params = new HashMap<String, String>();
            params.put(Constants.LOGIN_EMAIL, email);
            params.put(Constants.LOGIN_PASSWORD, password);

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGIN_URL, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();

                    try {
                        int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                        if (resMsg == 0) {
                            MyToast.showLong(getApplicationContext(), loginResponseMsg.get(resMsg));
                            // Launch login activity
                            session.setLogin(true);
                            getUserDetailFromServer(email);
                        } else {
                            myViewHandler.sendEmptyMessage(0);
                            MyToast.showLong(getApplicationContext(),
                                    loginResponseMsg.get(resMsg));
                        }
                    } catch (JSONException e) {
                        myViewHandler.sendEmptyMessage(0);
                        MyToast.showLong(getApplicationContext(),
                                loginResponseMsg.get(1));
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    myViewHandler.sendEmptyMessage(0);
                    MyToast.showLong(getApplicationContext(),
                            loginResponseMsg.get(1));
                    error.printStackTrace();
                    //TODO: handle failure
                    Log.e(TAG, "Registration Error: " + error.getMessage());
//                    hideDialog();
                }
            });
            AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);

        }
    }

    public void getUserDetailFromServer(String emailAddress) {
        String tag_string_req = "req_register";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.EMAIL, emailAddress);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.GET_USER_DETAIL_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();
                myViewHandler.sendEmptyMessage(0);
                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);

                    if (resMsg == 0) {
                        JSONObject data = response.getJSONObject(Constants.DOCUMENT_RESPONSE_DATA);
                        ApplicationUser loginUser = new ApplicationUser();
                        loginUser.setEmail(data.getString(Constants.EMAIL));
                        loginUser.setFullName(data.getString(Constants.FULL_NAME));
                        loginUser.setUserName(data.getString(Constants.USER_NAME));
                        loginUser.setPassword(data.getString(Constants.PASSWORD));
                        loginUser.setSubscription(data.getInt(Constants.SUBSCRIPTION));
                        session.setLogin(true);
                        session.setUserDetails(loginUser);

                        if (loginFrom.equals(Constants.LOGIN_FROM_GMP) || loginFrom.equals(Constants.LOGIN_FROM_POST_QUESTION)) {
                            finish();
                        } else {
                            Intent intent = new Intent(Login.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        MyToast.showLong(getApplicationContext(),
                                userDetailResponseMsg.get(resMsg));
                    }
                } catch (JSONException e) {
                    MyToast.showLong(getApplicationContext(),
                            userDetailResponseMsg.get(1));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myViewHandler.sendEmptyMessage(0);
                MyToast.showLong(getApplicationContext(),
                        userDetailResponseMsg.get(1));
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

}
