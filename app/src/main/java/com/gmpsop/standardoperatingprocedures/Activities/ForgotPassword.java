package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
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
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends Activity  implements View.OnClickListener{
    private static final String TAG = ForgotPassword.class.getSimpleName();

    EditText email;
    LinearLayout send;
    ArrayList<String> forgotEmailResponseMessage = new ArrayList<>();
    ProgressDialog pDialog;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frogot_password);
        init_components();
    }

    public void init_components(){
        email = (EditText) findViewById(R.id.forgotPasswordEmail);
        send = (LinearLayout) findViewById(R.id.forgotPasswordButtonSendLayout);
        send.setOnClickListener(this);
        msg = (TextView) findViewById(R.id.forgotPasswordMessage);
        forgotEmailResponseMessage.add("Check your Email");
        forgotEmailResponseMessage.add("Error while sending your password");
        forgotEmailResponseMessage.add("Email does not Exist");

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Please wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        switch (v.getId()){
            case R.id.forgotPasswordButtonSendLayout:
                if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("Please enter login_email address");
                    break;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError("Invalid login_email address");
                    break;
                }
                onSend(email.getText().toString());
                break;
        }
    }

    public void onSend(final String email){
        pDialog.show();
        String tag_string_req = "req_register";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.EMAIL, email);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.FORGOT_PASSWORD_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                pDialog.dismiss();
//                    hideDialog();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        msg.setText(forgotEmailResponseMessage.get(resMsg));
                        // Launch login activity
                    } else if(resMsg == 1) {
                        msg.setText(forgotEmailResponseMessage.get(resMsg));
                    } else{
                        msg.setText(forgotEmailResponseMessage.get(resMsg));
                    }
                } catch (JSONException e) {
                    msg.setText(forgotEmailResponseMessage.get(1));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                msg.setText(forgotEmailResponseMessage.get(1));
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

}
