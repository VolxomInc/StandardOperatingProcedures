package com.gmpsop.standardoperatingprocedures.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.Adapters.GMPFileListAdapter;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Models.GMPFiles;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DocumentFiles extends AppCompatActivity {
    private static final String TAG = DocumentFiles.class.getSimpleName();
    static ArrayList<GMPFiles> gmpFiles = new ArrayList<>();
    static ArrayList<GMPFiles> tempGmpFiles = new ArrayList<>();
    GMPFileListAdapter myAdapter;
    ListView listView;
    GMPFiles rootDirectory;
    GMPFiles currentDirectory;
    ProgressBar progressBar;

    LinearLayout errorLayout;
    ImageView errorImageView;
    TextView errorTextView;
    String noConnection, errorLoading;
    InputStream ims;
    Drawable d;
    boolean isError = false;
    boolean isLoading = false;

    ActionBar actionBar;
    LayoutInflater inflater;
    View v;
    TextView actionBarTitle;
    EditText searchTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_files);
        Bundle document = getIntent().getExtras();
        String root = document.getString(Constants.INTENT_ROOT_TYPE);
        setCustomActionBar(root);
        init_components();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gmpFiles.get(position).getType().equals(Constants.TYPE_FOLDER)) {
                    getChildren(position);
                } else {
                    Intent fileIntent = new Intent(DocumentFiles.this, DocumentFileReader.class);
                    fileIntent.putExtra(Constants.GMP_FILE_ID, gmpFiles.get(position).getId());
                    fileIntent.putExtra(Constants.GMP_FILE_NAME, gmpFiles.get(position).getName());
                    fileIntent.putExtra(Constants.GMP_FILE_PATH, gmpFiles.get(position).getPath());
                    fileIntent.putExtra(Constants.GMP_FILE_PARENT_ID, gmpFiles.get(position).getParentId());
                    startActivity(fileIntent);
                }
            }
        });
        populateInitList(root);
    }

    public void setCustomActionBar(String fileName) {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.action_bar_custom_layout, null);
        actionBarTitle = (TextView) v.findViewById(R.id.actionBar_fileName);
        actionBarTitle.setText(fileName);
        actionBar.setCustomView(v);
    }

    public void init_components() {


        myAdapter = new GMPFileListAdapter(this, 0, gmpFiles);
        listView = (ListView) findViewById(R.id.documentFilesExpListView);
        listView.setTextFilterEnabled(true);
        searchTitle = (EditText) findViewById(R.id.documentFiles_editText);
        searchTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.equals("")){
                    gmpFiles.clear();
                    gmpFiles.addAll(tempGmpFiles);
                    Log.d(TAG , s.toString());
                }else{
                    gmpFiles.clear();
                    for(int i =0 ; i<tempGmpFiles.size();i++){
                        Log.d(TAG , s.toString());
                        if(tempGmpFiles.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())){
                            gmpFiles.add(tempGmpFiles.get(i));
                            Log.d(TAG , " Name : " +tempGmpFiles.get(i).getName().toLowerCase());
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        currentDirectory = new GMPFiles();
        rootDirectory = new GMPFiles();
        errorLayout = (LinearLayout) findViewById(R.id.documentErrorLayout);
        errorImageView = (ImageView) findViewById(R.id.documentErrorImage);
        errorTextView = (TextView) findViewById(R.id.documentErrorMsg);
        progressBar = (ProgressBar) findViewById(R.id.documentFilesProgressBar);
        progressBar.setClickable(false);
        noConnection = getResources().getString(R.string.noInternetConnection);
        errorLoading = getResources().getString(R.string.errorLoading);

    }

    @Override
    public void onBackPressed() {
        if (isLoading) {
            MyToast.showShort(this, "Please wait while loading");
        } else if (isError || rootDirectory.getId() == currentDirectory.getId()) {
            super.onBackPressed();
        } else {
            getParentList();
        }
    }

    public void getChildren(final int position) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_ID, String.valueOf(gmpFiles.get(position).getId()));
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.SAMPLE_DATA_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();
                hideProgressBar();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        setCurrentDirectory(position);
                        gmpFiles.clear();
                        tempGmpFiles.clear();
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            gmpFiles.add(new GMPFiles(list.getJSONObject(i).getInt(Constants.GMP_FILE_ID), list.getJSONObject(i).getString(Constants.GMP_FILE_NAME), list.getJSONObject(i).getString(Constants.GMP_FILE_TYPE), list.getJSONObject(i).getString(Constants.GMP_FILE_PATH), list.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                        }
                        update_view();
                        // Launch login activity
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                Constants.NO_RECORD_FOUND);
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
                /*MyToast.showShort(getApplicationContext(),
                        error.getMessage());*/
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void getParentList() {
        if (!InternetOperations.isNetworkConnected(this)) {
            showError(noConnection);
            return;
        }
        /*if(currentDirectory.getParentId()==0){
            finish();
            return;
        }*/
        showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_ID, String.valueOf(currentDirectory.getParentId()));
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.SAMPLE_DATA_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();
                hideProgressBar();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        setParentDirectory(currentDirectory.getParentId());
                        gmpFiles.clear();
                        tempGmpFiles.clear();
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            gmpFiles.add(new GMPFiles(list.getJSONObject(i).getInt(Constants.GMP_FILE_ID), list.getJSONObject(i).getString(Constants.GMP_FILE_NAME), list.getJSONObject(i).getString(Constants.GMP_FILE_TYPE), list.getJSONObject(i).getString(Constants.GMP_FILE_PATH), list.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                        }
                        update_view();
                        // Launch login activity
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                Constants.NO_RECORD_FOUND);
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
                /*MyToast.showShort(getApplicationContext(),
                        error.getMessage());*/
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void populateInitList(String file_name) {
        if (!InternetOperations.isNetworkConnected(this)) {
            showError(noConnection);
            return;
        }
        showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_FILE_NAME, file_name);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.SAMPLE_DOC_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                hideProgressBar();
//                    hideDialog();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        gmpFiles.clear();
                        tempGmpFiles.clear();
                        JSONObject rootDetails = response.getJSONObject(Constants.DOCUMENT_RESPONSE_ROOT_DETAIL);
                        setRootDirectory(rootDetails);
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            gmpFiles.add(new GMPFiles(list.getJSONObject(i).getInt(Constants.GMP_FILE_ID), list.getJSONObject(i).getString(Constants.GMP_FILE_NAME), list.getJSONObject(i).getString(Constants.GMP_FILE_TYPE), list.getJSONObject(i).getString(Constants.GMP_FILE_PATH), list.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                        }
                        update_view();
                        // Launch login activity
                    } else if (resMsg == 2) {
                        gmpFiles.clear();
                        JSONObject rootDetails = response.getJSONObject(Constants.DOCUMENT_RESPONSE_ROOT_DETAIL);
                        setRootDirectory(rootDetails);
                        MyToast.showLong(getApplicationContext(),
                                Constants.NO_RECORD_FOUND);

                    } else {
                        MyToast.showLong(getApplicationContext(),
                                Constants.NO_RECORD_FOUND);
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
//                MyToast.showShort(getApplicationContext(),
//                        error.getMessage());
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    public void setCurrentDirectory(int position) {
        currentDirectory.setId(gmpFiles.get(position).getId());
        currentDirectory.setName(gmpFiles.get(position).getName());
        currentDirectory.setType(gmpFiles.get(position).getType());
        currentDirectory.setPath(gmpFiles.get(position).getPath());
        currentDirectory.setParentId(gmpFiles.get(position).getParentId());
        //  getSupportActionBar().setTitle(currentDirectory.getName());
        setCustomActionBar(currentDirectory.getName());
        searchTitle.setText("");
    }

    public void setParentDirectory(int id) {
        if (!InternetOperations.isNetworkConnected(this)) {
            showError(noConnection);
            return;
        }
        showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_ID, String.valueOf(id));
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.PARENT_DETAIL_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
//                    hideDialog();
                hideProgressBar();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        JSONArray aray = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        JSONObject list = aray.getJSONObject(0);
                        currentDirectory.setId(list.getInt(Constants.GMP_FILE_ID));
                        currentDirectory.setName(list.getString(Constants.GMP_FILE_NAME));
                        currentDirectory.setType(list.getString(Constants.GMP_FILE_TYPE));
                        currentDirectory.setPath(list.getString(Constants.GMP_FILE_PATH));
                        currentDirectory.setParentId(list.getInt(Constants.GMP_FILE_PARENT_ID));
                        setCustomActionBar(currentDirectory.getName());
                        searchTitle.setText("");
                        // Launch login activity
                    } else {
                        MyToast.showLong(getApplicationContext(),
                                Constants.NO_RECORD_FOUND);
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
                /*MyToast.showShort(getApplicationContext(),
                        error.getMessage());*/
//                    hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);

    }

    public void setRootDirectory(JSONObject rootObj) {
        try {
            rootDirectory.setId(rootObj.getInt(Constants.GMP_FILE_ID));
            rootDirectory.setName(rootObj.getString(Constants.GMP_FILE_NAME));
            rootDirectory.setType(rootObj.getString(Constants.GMP_FILE_TYPE));
            rootDirectory.setPath(rootObj.getString(Constants.GMP_FILE_PATH));
            rootDirectory.setParentId(rootObj.getInt(Constants.GMP_FILE_PARENT_ID));
            currentDirectory.setId(rootObj.getInt(Constants.GMP_FILE_ID));
            currentDirectory.setName(rootObj.getString(Constants.GMP_FILE_NAME));
            currentDirectory.setType(rootObj.getString(Constants.GMP_FILE_TYPE));
            currentDirectory.setPath(rootObj.getString(Constants.GMP_FILE_PATH));
            currentDirectory.setParentId(rootObj.getInt(Constants.GMP_FILE_PARENT_ID));
            setCustomActionBar(rootDirectory.getName());
            searchTitle.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showProgressBar() {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        isLoading = true;
    }

    public void hideProgressBar() {
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        isLoading = false;
    }

    public void showError(String type) {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
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

    public void hideError() {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    public void update_view() {

        tempGmpFiles.clear();
        myAdapter.notifyDataSetChanged();
        tempGmpFiles.addAll(gmpFiles) ;

    }
}
