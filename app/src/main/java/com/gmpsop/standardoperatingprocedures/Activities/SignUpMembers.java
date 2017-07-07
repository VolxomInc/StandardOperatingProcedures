package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

/**
 * Created by BD1 on 01-May-17.
 */

public class SignUpMembers extends Activity implements View.OnClickListener {

    private static final String TAG = SignUpMembers.class.getSimpleName();
    public  ArrayList<String> signUpResponseMsg;

    private ProgressDialog pDialog;
    private SessionManager session;

    TextView termsOfUse, privacyPolicy;
    LinearLayout signUpButtonLayout;
    EditText firstLastName, userName, emailAddress, password, confirmPassword;
    ApplicationUser applicationUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_members);
        init_components();
    }

    public void init_components() {
        firstLastName = (EditText) findViewById(R.id.signUpFirstLastName);
        userName = (EditText) findViewById(R.id.signUpUserName);
        emailAddress = (EditText) findViewById(R.id.signUpEmailAddress);
        password = (EditText) findViewById(R.id.signUpPassword);
        confirmPassword = (EditText) findViewById(R.id.signUpConfirmPassword);
        termsOfUse = (TextView) findViewById(R.id.signUpTermOfUse);
        termsOfUse.setOnClickListener(this);
        privacyPolicy = (TextView) findViewById(R.id.signUpPrivacyPolicy);
        privacyPolicy.setOnClickListener(this);
        signUpButtonLayout = (LinearLayout) findViewById(R.id.signUpButtonLayout);
        signUpButtonLayout.setOnClickListener(this);
        applicationUser = new ApplicationUser();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        signUpResponseMsg = new ArrayList<>();
        signUpResponseMsg.add(Constants.REG_MSG_INSERT_SUCCESS);
        signUpResponseMsg.add(Constants.REG_MSG_INSERT_UNSUCCESSFUL);
        signUpResponseMsg.add(Constants.REG_MSG_INSERT_UNSUCCESSFUL_EMAIL_EXIST);
        // Session manager
        session = new SessionManager(getApplicationContext());
        /*if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this,
                    Dashboard.class);
            startActivity(intent);
            finish();
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpButtonLayout:
                if (!InternetOperations.isNetworkConnected(this)) {
                    MyToast.showLong(this,getString(R.string.noInternetConnection));
                    break;
                }
                validateSignUpForm();
                break;
            case R.id.signUpTermOfUse:
                Intent termIntent = new Intent(this, TermAndConditions.class);
                startActivity(termIntent);
                break;
            case R.id.signUpPrivacyPolicy:
                MyToast.showShort(this, "PrivacyPolicy Clicked");
                break;
        }
    }

    public void validateSignUpForm() {
        if (TextUtils.isEmpty(firstLastName.getText().toString().trim())) {
            firstLastName.setError("Please enter Name");
            return;
        }
        if (TextUtils.isEmpty(userName.getText().toString().trim())) {
            userName.setError("Please enter User name");
            return;
        }
        if (TextUtils.isEmpty(emailAddress.getText().toString().trim())) {
            emailAddress.setError("Please enter Email address");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress.getText().toString().trim()).matches()) {
            emailAddress.setError("Invalid Email address");
            return;
        }
        if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError("Please enter Password");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword.getText().toString().trim())) {
            confirmPassword.setError("Please Confirm your login_password");
            return;
        }
        if (!confirmPassword.getText().toString().trim().equals(password.getText().toString().trim())) {
            confirmPassword.setError("Password not Match");
            return;
        }
        applicationUser = populateUserDetails(firstLastName.getText().toString().trim(), userName.getText().toString().trim(), emailAddress.getText().toString().trim(), password.getText().toString().trim());
        //  applicationUser = new ApplicationUser();
        RegisterUser backgroundTask = new RegisterUser(this);
        backgroundTask.execute(applicationUser);

    }

    public ApplicationUser populateUserDetails(String name, String userName, String email, String password) {
        ApplicationUser appUser = new ApplicationUser();
        appUser.setFullName(name);
        appUser.setUserName(userName);
        appUser.setEmail(email);
        appUser.setPassword(password);
        appUser.setLoginStatus(1);
        return appUser;
    }

    public class RegisterUser extends AsyncTask<ApplicationUser, Void, String> {
        AlertDialog alertDialog;
        Context ctx;

        RegisterUser(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(ApplicationUser... params) {
            ApplicationUser newUser = params[0];
            registerUser(newUser);
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

        private void registerUser(final ApplicationUser registeringUser) {
            // Tag used to cancel the request
            String tag_string_req = "req_register";

            Map<String, String> params = new HashMap<String, String>();
            params.put(Constants.FULL_NAME, registeringUser.getFullName());
            params.put(Constants.USER_NAME, registeringUser.getUserName());
            params.put(Constants.EMAIL, registeringUser.getEmail());
            params.put(Constants.PASSWORD, registeringUser.getPassword());
            params.put(Constants.SUBSCRIPTION, String.valueOf(registeringUser.getSubscription()));
            params.put(Constants.IS_LOGGED_IN, String.valueOf(registeringUser.getLoginStatus()));

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.REGISTER_URL, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();

                    try {
                        int error = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                        if (error == 0) {
                            MyToast.showLong(getApplicationContext(), signUpResponseMsg.get(error));
                            // Launch login activity
                            session.setLogin(true);
                            session.setUserDetails(registeringUser);
                            Intent intent = new Intent(SignUpMembers.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            MyToast.showLong(getApplicationContext(),
                                    signUpResponseMsg.get(error));
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


    }


}
