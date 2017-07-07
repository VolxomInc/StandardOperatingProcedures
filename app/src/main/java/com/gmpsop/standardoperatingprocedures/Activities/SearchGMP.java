package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.GMPSearchFileListAdapter;
import com.gmpsop.standardoperatingprocedures.Adapters.GMPSuggestionsListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.Models.GMPFiles;
import com.gmpsop.standardoperatingprocedures.R;
import com.gmpsop.standardoperatingprocedures.util.IabHelper;
import com.gmpsop.standardoperatingprocedures.util.IabResult;
import com.gmpsop.standardoperatingprocedures.util.Inventory;
import com.gmpsop.standardoperatingprocedures.util.Purchase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class SearchGMP extends Activity implements View.OnClickListener {
    private static final String TAG = SearchGMP.class.getSimpleName();
    EditText searchTerm;
    ImageView goSearch, definitionFlag, regulatoryFlag, gmpLibraryFlag;
    LinearLayout resultLayout, definitionButton, regulatoryReferenceButton, gmpLibraryButton, errorLayout;
    TextView termDefinition, onStartMessage, regulatoryNotFound, gmpNotFound, definitionNotFound;
    ListView termRegulatory, termGmpLibrary, suggestionListView;

    boolean isDefinitionOpen = false;
    boolean isRegulatoryReferenceOpen = false;
    boolean isGmpLibraryOpen = false;

    boolean isDefinitionZero = true;
    boolean isRegulatoryZero = true;
    boolean isGmpZero = true;

    ProgressDialog pDialog;
    boolean isLoading = false;
    String errorLoading, noConnection;
    GMPSearchFileListAdapter regulatoryAdapter, gMPSOPAdapter;
    static ArrayList<GMPFiles> regulatoryFiles = new ArrayList<>();
    static ArrayList<GMPFiles> gmpFiles = new ArrayList<>();
    static ArrayList<String> suggestionList = new ArrayList<>();

    InputStream ims;
    Drawable d;
    boolean isError = false;
    ImageView errorImageView;
    TextView errorTextView;

    private SessionManager session;
    MyDialogSubscription subscriptionDialog;
    MyDialogLogin loginDialog;
    static final String ITEM_SKU = "gmp_sop_member";
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener levelSubscriptionPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    private ProgressDialog pSubscriptionDialog;
    ArrayList<String> subscriptionResponse = new ArrayList<>();
    GMPSuggestionsListAdapter suggestedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gmp);
        init_components();
        setMHelper();
        termRegulatory.setAdapter(regulatoryAdapter);
        termRegulatory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent fileIntent = new Intent(SearchGMP.this, DocumentFileReader.class);
                fileIntent.putExtra(Constants.GMP_FILE_ID, regulatoryFiles.get(position).getId());
                fileIntent.putExtra(Constants.GMP_FILE_NAME, regulatoryFiles.get(position).getName());
                fileIntent.putExtra(Constants.GMP_FILE_PATH, regulatoryFiles.get(position).getPath());
                fileIntent.putExtra(Constants.GMP_FILE_PARENT_ID, regulatoryFiles.get(position).getParentId());
                startActivity(fileIntent);

            }
        });
        termGmpLibrary.setAdapter(gMPSOPAdapter);
        termGmpLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent fileIntent = new Intent(SearchGMP.this, DocumentFileReader.class);
                fileIntent.putExtra(Constants.GMP_FILE_ID, gmpFiles.get(position).getId());
                fileIntent.putExtra(Constants.GMP_FILE_NAME, gmpFiles.get(position).getName());
                fileIntent.putExtra(Constants.GMP_FILE_PATH, gmpFiles.get(position).getPath());
                fileIntent.putExtra(Constants.GMP_FILE_PARENT_ID, gmpFiles.get(position).getParentId());
                startActivity(fileIntent);

            }
        });
        getFromIntent();
    }

    @Override
    protected void onResume() {
        regulatory_list();
        super.onResume();

    }

    public void getFromIntent() {
        Bundle data = getIntent().getExtras();
        String term = data.getString(Constants.INTENT_SEARCH_TERM);
        searchTerm.setText(term);
        suggestionListView.setVisibility(GONE);
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        if (searchTerm.getText().toString().equals("")) {
            return;
        }
        hideError();
        searchGMPTERM(searchTerm.getText().toString());

    }

    public void init_components() {
        regulatoryAdapter = new GMPSearchFileListAdapter(this, 0, regulatoryFiles);
        gMPSOPAdapter = new GMPSearchFileListAdapter(this, 0, gmpFiles);

        searchTerm = (EditText) findViewById(R.id.search_gmp_editText);
        searchTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    suggestionListView.setVisibility(GONE);
                    goSearch.setImageResource(R.drawable.search_gmp_go);
                } else {
                    suggestionListView.setVisibility(View.VISIBLE);
                    goSearch.setImageResource(R.drawable.search_gmp_go_enable);
                    suggestionGmp(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        goSearch = (ImageView) findViewById(R.id.search_gmp_goButton);
        goSearch.setOnClickListener(this);
        onStartMessage = (TextView) findViewById(R.id.search_gmp_onStartMessage);

        definitionFlag = (ImageView) findViewById(R.id.search_gmp_definitionFlag);
        regulatoryFlag = (ImageView) findViewById(R.id.search_gmp_regulatoryReferenceFlag);
        gmpLibraryFlag = (ImageView) findViewById(R.id.search_gmp_gmpLibraryFlag);

        resultLayout = (LinearLayout) findViewById(R.id.search_gmp_resultLayout);
        definitionButton = (LinearLayout) findViewById(R.id.search_gmp_definitionButton);
        definitionButton.setOnClickListener(this);
        regulatoryReferenceButton = (LinearLayout) findViewById(R.id.search_gmp_regulatoryReferenceButton);
        regulatoryReferenceButton.setOnClickListener(this);
        gmpLibraryButton = (LinearLayout) findViewById(R.id.search_gmp_gmpLibraryButton);
        gmpLibraryButton.setOnClickListener(this);

        termDefinition = (TextView) findViewById(R.id.search_gmp_termDefinition);

        termRegulatory = (ListView) findViewById(R.id.search_gmp_termRegulatoryReference);
        termGmpLibrary = (ListView) findViewById(R.id.search_gmp_termGmpLibrary);

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Searching...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        errorLayout = (LinearLayout) findViewById(R.id.search_gmp_errorLayout);
        noConnection = getResources().getString(R.string.noInternetConnection);
        errorLoading = getResources().getString(R.string.errorLoading);
        errorImageView = (ImageView) findViewById(R.id.search_gmp_errorImage);
        errorTextView = (TextView) findViewById(R.id.search_gmp_errorMsg);

        regulatoryNotFound = (TextView) findViewById(R.id.search_gmp_regulatory_zero);
        gmpNotFound = (TextView) findViewById(R.id.search_gmp_library_zero);
        definitionNotFound = (TextView) findViewById(R.id.search_gmp_definition_zero);

        session = new SessionManager(getApplicationContext());
        pSubscriptionDialog = new ProgressDialog(this);
        pSubscriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pSubscriptionDialog.setMessage("Please wait...");
        pSubscriptionDialog.setCancelable(false);

        loginDialog = new MyDialogLogin(this);
        loginDialog.setCanceledOnTouchOutside(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        subscriptionDialog = new MyDialogSubscription(this);
        subscriptionDialog.setCanceledOnTouchOutside(true);
        subscriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        suggestedListAdapter = new GMPSuggestionsListAdapter(this, R.layout.list_view_search_gmp_suggestions, suggestionList);

        suggestionListView = (ListView) findViewById(R.id.search_gmp_suggestionList);
        suggestionListView.setAdapter(suggestedListAdapter);
        suggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                suggestionListView.setVisibility(GONE);
                if (!InternetOperations.isNetworkConnected(SearchGMP.this)) {
                    MyToast.showLong(SearchGMP.this, getString(R.string.noInternetConnection));
                    return;
                }
                hideError();
                searchGMPTERM(suggestionList.get(position));

            }
        });

        subscriptionResponse.add(Constants.SUBSCRIPTION_MSG_SUCCESS);
        subscriptionResponse.add(Constants.SUBSCRIPTION_MSG_UNSUCCESSFUL);

    }

    public void setMHelper() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_gmp_goButton:
                suggestionListView.setVisibility(GONE);
                if (!InternetOperations.isNetworkConnected(this)) {
                    MyToast.showLong(this, getString(R.string.noInternetConnection));
                    break;
                }
                if (searchTerm.getText().toString().equals("")) {
                    MyToast.showShort(this, getResources().getString(R.string.search_gmp_onStartMessage));
                    break;
                }
                hideError();
                searchGMPTERM(searchTerm.getText().toString());
                break;
            case R.id.search_gmp_definitionButton:
                onDefinitionClicked();
                break;
            case R.id.search_gmp_regulatoryReferenceButton:
                onRegulatoryClicked();
                break;
            case R.id.search_gmp_gmpLibraryButton:
                if (!session.isLoggedIn()) {
                    loginDialog.show();
                } else {
                    if (session.getSubsription() == 1) {
                        onGmpLibraryClicked();
                    } else {
                        subscriptionDialog.show();
                        // MyToast.showShort(this, "You need to Subscribe");
                    }
                }

                break;

        }
    }

    public void searchGMPTERM(final String gmpTerm) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_GMP_TERM, gmpTerm);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.GMP_TERM_SEARCH_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();
                hideProgressBar();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        // setCurrentDirectory(position);
                        onStartMessage.setVisibility(GONE);
                        resultLayout.setVisibility(View.VISIBLE);
                        gmpFiles.clear();

                        String definition = response.getString(Constants.DOCUMENT_RESPONSE_DEFINITION);
                        if (definition.equals("")) {
                            isDefinitionZero = true;
                        } else {
                            isDefinitionZero = false;
                            termDefinition.setText(definition);
                        }


                        JSONArray gmpList = response.getJSONArray(Constants.DOCUMENT_RESPONSE_GMP_SEARCH);


                        if (gmpList.length() == 0) {
                            isGmpZero = true;

                        } else {
                            isGmpZero = false;

                            for (int i = 0; i < gmpList.length(); i++) {
                                gmpFiles.add(new GMPFiles(gmpList.getJSONObject(i).getInt(Constants.GMP_FILE_ID), gmpList.getJSONObject(i).getString(Constants.GMP_FILE_NAME), gmpList.getJSONObject(i).getString(Constants.GMP_FILE_PATH), gmpList.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                            }
                        }

                        update_view();
                        // Launch login activity
                    } else {
                        onStartMessage.setVisibility(View.VISIBLE);
                        onStartMessage.setText(getResources().getString(R.string.search_gmp_definition_files_not_found));
                        resultLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                showError(errorLoading);
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());

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
        params.put(Constants.PARAMETER_SUGGESTION_TERM, gmpTerm);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.GMP_TERM_SEARCH_SUGGESTION_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    suggestionList.clear();
                    if (resMsg == 0) {
                        JSONArray suggestions = response.getJSONArray(Constants.DOCUMENT_RESPONSE_SUGGESTION);

                        for (int i = 0; i < suggestions.length(); i++) {
                            suggestionList.add(suggestions.getString(i));
                        }
                    } else {
                        suggestionListView.setVisibility(GONE);
                    }
                    suggestedListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                showError(errorLoading);
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());

//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void regulatory_list() {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        String tag_string_req = "req_register";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.REGULATORY_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_REGULATORY);
                        if (list.length() > 0) {
                            isRegulatoryZero = false;
                            for (int i = 0; i < list.length(); i++) {
                                regulatoryFiles.add(new GMPFiles(list.getJSONObject(i).getInt(Constants.GMP_FILE_ID), list.getJSONObject(i).getString(Constants.GMP_FILE_NAME), list.getJSONObject(i).getString(Constants.GMP_FILE_PATH), list.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                            }
                        }else{
                            isRegulatoryZero = true;
                        }
                    } else {
                        isRegulatoryZero = true;
                    }
                    regulatoryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    isRegulatoryZero = true;
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                isRegulatoryZero = true;
                showError(errorLoading);
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());

//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void showProgressBar() {
        onStartMessage.setVisibility(View.GONE);
        resultLayout.setVisibility(GONE);
        pDialog.show();
        isLoading = true;
    }

    public void hideProgressBar() {
        pDialog.dismiss();
        isLoading = false;
    }

    public void showError(String type) {

        isLoading = false;
        errorLayout.setVisibility(View.VISIBLE);
        isError = true;
        if (getResources().getString(R.string.noInternetConnection).equals(type)) {
            try {
                // get input stream
                ims = getAssets().open("no_internet.png");
                Log.d(TAG, "stream clear");
                // load image as Drawable
                d = Drawable.createFromStream(ims, null);
                Log.d(TAG, "drawable clear");
                // set image to ImageView
                errorImageView.setImageDrawable(d);
                Log.d(TAG, "imageView clear");
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "no connection file not found");
            }
            errorTextView.setText(type);
        } else {
            try {
                // get input stream
                ims = getAssets().open("error_icon.png");
                // load image as Drawable
                d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                errorImageView.setImageDrawable(d);
            } catch (IOException ex) {
                Log.d(TAG, "error file not found");
            }
            errorTextView.setText(type);
        }
    }

    public void onDefinitionClicked() {
        if (isRegulatoryReferenceOpen)
            onRegulatoryClicked();
        if (isGmpLibraryOpen)
            onGmpLibraryClicked();
        if (!isDefinitionOpen) {
            isDefinitionOpen = true;
            definitionFlag.setImageResource(R.drawable.search_minimize);
            if (isDefinitionZero) {
                definitionNotFound.setVisibility(View.VISIBLE);
                termDefinition.setVisibility(GONE);
            } else {
                definitionNotFound.setVisibility(View.GONE);
                termDefinition.setVisibility(View.VISIBLE);
            }
        } else {
            isDefinitionOpen = false;
            definitionFlag.setImageResource(R.drawable.search_maximize);
            definitionNotFound.setVisibility(GONE);
            termDefinition.setVisibility(GONE);
        }
    }

    public void onRegulatoryClicked() {
        if (isDefinitionOpen)
            onDefinitionClicked();
        if (isGmpLibraryOpen)
            onGmpLibraryClicked();
        if (!isRegulatoryReferenceOpen) {
            isRegulatoryReferenceOpen = true;
            regulatoryFlag.setImageResource(R.drawable.search_minimize);
            if (isRegulatoryZero) {
                regulatoryNotFound.setVisibility(View.VISIBLE);
                termRegulatory.setVisibility(GONE);
            } else {
                regulatoryNotFound.setVisibility(View.GONE);
                termRegulatory.setVisibility(View.VISIBLE);
            }
        } else {
            isRegulatoryReferenceOpen = false;
            regulatoryFlag.setImageResource(R.drawable.search_maximize);
            regulatoryNotFound.setVisibility(GONE);
            termRegulatory.setVisibility(GONE);
        }
    }

    public void onGmpLibraryClicked() {
        if (isDefinitionOpen)
            onDefinitionClicked();
        if (isRegulatoryReferenceOpen)
            onRegulatoryClicked();
        if (!isGmpLibraryOpen) {
            isGmpLibraryOpen = true;
            gmpLibraryFlag.setImageResource(R.drawable.search_minimize);
            if (isGmpZero) {
                gmpNotFound.setVisibility(View.VISIBLE);
                termGmpLibrary.setVisibility(GONE);
            } else {
                gmpNotFound.setVisibility(View.GONE);
                termGmpLibrary.setVisibility(View.VISIBLE);
            }
        } else {
            isGmpLibraryOpen = false;
            gmpLibraryFlag.setImageResource(R.drawable.search_maximize);
            gmpNotFound.setVisibility(GONE);
            termGmpLibrary.setVisibility(GONE);
        }
    }


    public void hideError() {
        /*listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);*/
        isError = false;
        errorLayout.setVisibility(View.GONE);
    }

    private void purchaseSubscription() {
        try {
            mHelper.launchPurchaseFlow(SearchGMP.this, ITEM_SKU, Constants.LEVEL_SUBSCRIPTION_REQUEST_CODE, levelSubscriptionPurchaseFinishedListener);
            //this.selectedLevel = selectedLevel;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update_view() {
        gMPSOPAdapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(SearchGMP.this,
                            DocumentFiles.class);
                    intent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.SAMPLE_DOC);
                    startActivity(intent);
                    dismiss();
                    SearchGMP.this.finish();
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
                    Intent intent = new Intent(SearchGMP.this,
                            DocumentFiles.class);
                    intent.putExtra(Constants.INTENT_ROOT_TYPE, Constants.SAMPLE_DOC);
                    startActivity(intent);
                    dismiss();
                    SearchGMP.this.finish();
                    break;
                case R.id.dialogLoginLayout:
                    Intent loginIntent = new Intent(SearchGMP.this,
                            Login.class);
                    loginIntent.putExtra(Constants.INTENT_LOGIN_FROM, Constants.LOGIN_FROM_GMP);
                    startActivity(loginIntent);
                    dismiss();
                    break;
                case R.id.dialogLogin_signUpLayout:
                    Intent signUpIntent = new Intent(SearchGMP.this,
                            SignUpMembers.class);
                    startActivity(signUpIntent);
                    dismiss();
                    SearchGMP.this.finish();
                    break;
            }
        }
    }


}
