package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.R;
import com.gmpsop.standardoperatingprocedures.util.IabHelper;
import com.gmpsop.standardoperatingprocedures.util.IabResult;
import com.gmpsop.standardoperatingprocedures.util.Inventory;
import com.gmpsop.standardoperatingprocedures.util.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Muhammad Ali on 01-May-17.
 */

public class Dashboard extends Activity implements View.OnClickListener {

    private static final String TAG = Dashboard.class.getSimpleName();

    static final String ITEM_SKU = "gmp_sop_member";
    MyDialogSubscription subscriptionDialog;
    MyDialogLogin loginDialog;
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener levelSubscriptionPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    private SessionManager session;
    private ProgressDialog pDialog;
    private ProgressDialog pSubscriptionDialog;

    ArrayList<String> logoutResponse = new ArrayList<>();
    ArrayList<String> subscriptionResponse = new ArrayList<>();

    LinearLayout notification, searchGMPTerm, gMPDiscussForum, sampleDocument, buyFromGMPSOPLibrary, overView, fAQs, aboutUs, contactUs, loginSignUp, logout;
    RelativeLayout logInButton, signUpButton, logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init_components();
        setMHelper();
        // compute your public key and store it in base64EncodedPublicKey

    }

    private void setSubscription(final String email) {
        pSubscriptionDialog.show();
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.LOGOUT_EMAIL, email);
        Log.d(TAG, email);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.SUBSCRIBE_USER, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                pSubscriptionDialog.dismiss();
//                    hideDialog();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        MyToast.showLong(getApplicationContext(), subscriptionResponse.get(resMsg));
                        // Launch login activity
                        //logoutSession();
                        session.setSubsription(1);
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                subscriptionResponse.get(resMsg));
                    }
                } catch (JSONException e) {
                    MyToast.showLong(getApplicationContext(),
                            subscriptionResponse.get(1));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pSubscriptionDialog.dismiss();
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MyToast.showLong(getApplicationContext(),
                        subscriptionResponse.get(1));
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);

    }


    public void setMHelper(){
        mHelper = new IabHelper(this, Constants.IAB);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
                // Hooray, IAB is fully set up!
            }
        });

        levelSubscriptionPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase) {
                if (result.isFailure()) {

                } else {
                    setSubscription(session.getUserDetail().getEmail());
                }

            }
        };


        mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (!result.isFailure()) {
                    /*Toast.makeText(Dashboard.this, "InventoryListener", Toast.LENGTH_SHORT).show();
                    // Handle failure
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                            mConsumeFinishedListener);*/
                } else {
//                    Toast.makeText(Dashboard.this, "InventoryListener failure", Toast.LENGTH_SHORT).show();
                }
            }
        };

        mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase,
                                          IabResult result) {

                /*if (result.isSuccess()) {
                    Toast.makeText(Dashboard.this, "ConsumeListener", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Dashboard.this, "ConsumeListener unsuccessful", Toast.LENGTH_SHORT).show();
                    // handle error
                }*/
            }
        };
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            logout.setVisibility(View.VISIBLE);
            loginSignUp.setVisibility(View.GONE);
        } else {
            logout.setVisibility(View.GONE);
            loginSignUp.setVisibility(View.VISIBLE);
        }
    }

    public void init_components() {
        notification = (LinearLayout) findViewById(R.id.dashboardLinearButton_notification);
        notification.setOnClickListener(this);
        searchGMPTerm = (LinearLayout) findViewById(R.id.dashboardLinearButton_searchGmpTerm);
        searchGMPTerm.setOnClickListener(this);
        gMPDiscussForum = (LinearLayout) findViewById(R.id.dashboardLinearButton_gmpDiscussionForum);
        gMPDiscussForum.setOnClickListener(this);
        sampleDocument = (LinearLayout) findViewById(R.id.dashboardLinearButton_sampleDocument);
        sampleDocument.setOnClickListener(this);
        buyFromGMPSOPLibrary = (LinearLayout) findViewById(R.id.dashboardLinearButton_buyFromSopLibrary);
        buyFromGMPSOPLibrary.setOnClickListener(this);
        overView = (LinearLayout) findViewById(R.id.dashboardLinearButton_overView);
        overView.setOnClickListener(this);
        fAQs = (LinearLayout) findViewById(R.id.dashboardLinearButton_FAQs);
        fAQs.setOnClickListener(this);
        aboutUs = (LinearLayout) findViewById(R.id.dashboardLinearButton_aboutUs);
        aboutUs.setOnClickListener(this);
        contactUs = (LinearLayout) findViewById(R.id.dashboardLinearButton_contactUs);
        contactUs.setOnClickListener(this);
        loginSignUp = (LinearLayout) findViewById(R.id.dashboard_loginSignUpLayout);
        logout = (LinearLayout) findViewById(R.id.dashboard_logoutLayout);
        session = new SessionManager(getApplicationContext());
        logInButton = (RelativeLayout) findViewById(R.id.dashboardLoginButton);
        logInButton.setOnClickListener(this);
        signUpButton = (RelativeLayout) findViewById(R.id.dashboardSignUpButton);
        signUpButton.setOnClickListener(this);
        logoutButton = (RelativeLayout) findViewById(R.id.dashboardLogoutButton);
        logoutButton.setOnClickListener(this);

        subscriptionDialog = new MyDialogSubscription(this);
        subscriptionDialog.setCanceledOnTouchOutside(true);
        subscriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        loginDialog = new MyDialogLogin(this);
        loginDialog.setCanceledOnTouchOutside(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        logoutResponse.add(Constants.LOGOUT_MSG_SUCCESS);
        logoutResponse.add(Constants.LOGOUT_MSG_ERROR);

        subscriptionResponse.add(Constants.SUBSCRIPTION_MSG_SUCCESS);
        subscriptionResponse.add(Constants.SUBSCRIPTION_MSG_UNSUCCESSFUL);

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Please wait while Logout...");
        pDialog.setCancelable(false);

        pSubscriptionDialog = new ProgressDialog(this);
        pSubscriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pSubscriptionDialog.setMessage("Please wait...");
        pSubscriptionDialog.setCancelable(false);
    }

    private void purchaseSubscription() {
        try {
            mHelper.launchPurchaseFlow(Dashboard.this, ITEM_SKU, Constants.LEVEL_SUBSCRIPTION_REQUEST_CODE, levelSubscriptionPurchaseFinishedListener);
            //this.selectedLevel = selectedLevel;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    public void onClick(View v) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        switch (v.getId()) {
            case R.id.dashboardLinearButton_notification:
//                startActivity(new Intent(this , AskGMP.class));
                MyToast.showShort(this, "Notification Clicked");
                break;
            case R.id.dashboardLinearButton_searchGmpTerm:
                startActivity(new Intent(this , SearchOption.class));
                break;
            case R.id.dashboardLinearButton_gmpDiscussionForum:

                startActivity(new Intent(this , DiscussionForum.class));
                break;
            case R.id.dashboardLinearButton_sampleDocument:
                // MyToast.showShort(this, "DocumentFiles Clicked");
                // User is already logged in. Take him to main activity
                Intent intent = new Intent(this,
                        DocumentFiles.class);
                intent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.SAMPLE_DOC);
                startActivity(intent);
                break;
            case R.id.dashboardLinearButton_buyFromSopLibrary:
                if (!session.isLoggedIn()) {
                    loginDialog.show();
                } else {
                    if (session.getSubsription() == 1) {
                        Intent libraryIntent = new Intent(this,
                                DocumentFiles.class);
                        libraryIntent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.GMP_DOC);
                        startActivity(libraryIntent);
                    } else {
                        subscriptionDialog.show();
                        // MyToast.showShort(this, "You need to Subscribe");
                    }
                }
                break;
            case R.id.dashboardLinearButton_overView:
                startOverView(this);
                break;
            case R.id.dashboardLinearButton_FAQs:
                Intent fAQsIntent = new Intent(this, FrequentlyAskQuestion.class);
                startActivity(fAQsIntent);
                break;
            case R.id.dashboardLinearButton_aboutUs:
                Intent aboutUsIntent = new Intent(this, AboutUs.class);
                startActivity(aboutUsIntent);
                break;
            case R.id.dashboardLinearButton_contactUs:
                //MyToast.showShort(this, "ContactUs Clicked");
                Intent contactIntent = new Intent(this,
                        ContactUs.class);
                startActivity(contactIntent);
                break;
            case R.id.dashboardLoginButton:
                if (!session.isLoggedIn()) {
                    Intent loginIntent = new Intent(this,
                            Login.class);
                    loginIntent.putExtra(Constants.INTENT_LOGIN_FROM,"");
                    startActivity(loginIntent);
                } else {
                    //session.setLogin(false);
                    MyToast.showShort(this, "You are already loggedIn");
                }
                break;
            case R.id.dashboardSignUpButton:
                Intent signUpIntent = new Intent(this,
                        SignUpMembers.class);
                startActivity(signUpIntent);
                break;
            case R.id.dashboardLogoutButton:
                //logoutSession();
                logOutUser(session.getUserDetail().getEmail());
                break;
        }
    }

    public void logoutSession() {
        session.setLogin(false);
        logout.setVisibility(View.GONE);
        loginSignUp.setVisibility(View.VISIBLE);
    }

    public void startOverView(Context context) {
        startActivity(new Intent(context, AboutGMP.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void logOutUser(final String email) {
        pDialog.show();
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.LOGOUT_EMAIL, email);
        Log.d(TAG, email);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGOUT_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                pDialog.dismiss();
//                    hideDialog();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        MyToast.showLong(getApplicationContext(), logoutResponse.get(resMsg));
                        // Launch login activity
                        logoutSession();
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                logoutResponse.get(resMsg));
                    }
                } catch (JSONException e) {
                    MyToast.showLong(getApplicationContext(),
                            logoutResponse.get(1));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MyToast.showLong(getApplicationContext(),
                        logoutResponse.get(1));
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);

    }

    private class MyDialogSubscription extends Dialog implements View.OnClickListener {

        LinearLayout sampleDocument;
        LinearLayout getMembership;

        MyDialogSubscription(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_window_subscription);
            init_components();
        }

        void init_components() {
            sampleDocument = (LinearLayout) findViewById(R.id.dialogSubscriptionLinearButton_SampleDocument);
            sampleDocument.setOnClickListener(this);
            getMembership = (LinearLayout) findViewById(R.id.dialogSubscriptionLinearButton_membership);
            getMembership.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialogSubscriptionLinearButton_SampleDocument:
                    Intent intent = new Intent(Dashboard.this,
                            DocumentFiles.class);
                    intent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.SAMPLE_DOC);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.dialogSubscriptionLinearButton_membership:
                    purchaseSubscription();
                    dismiss();
                    break;

            }
        }
    }


    private class MyDialogLogin extends Dialog implements View.OnClickListener {

        LinearLayout sampleDocument;
        LinearLayout loginLayout, signUpLayout;

        MyDialogLogin(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_window_login);
            init_components();
        }

        void init_components() {
            sampleDocument = (LinearLayout) findViewById(R.id.dialogLoginLinearButton_SampleDocument);
            sampleDocument.setOnClickListener(this);
            loginLayout = (LinearLayout) findViewById(R.id.dialogLoginLayout);
            loginLayout.setOnClickListener(this);
            signUpLayout = (LinearLayout) findViewById(R.id.dialogLogin_signUpLayout);
            signUpLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialogLoginLinearButton_SampleDocument:
                    Intent intent = new Intent(Dashboard.this,
                            DocumentFiles.class);
                    intent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.SAMPLE_DOC);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.dialogLoginLayout:
                    Intent loginIntent = new Intent(Dashboard.this,
                            Login.class);
                    loginIntent.putExtra(Constants.INTENT_LOGIN_FROM,"");
                    startActivity(loginIntent);
                    dismiss();
                    break;
                case R.id.dialogLogin_signUpLayout:
                    Intent signUpIntent = new Intent(Dashboard.this,
                            SignUpMembers.class);
                    startActivity(signUpIntent);
                    dismiss();
                    break;
            }
        }
    }

}
