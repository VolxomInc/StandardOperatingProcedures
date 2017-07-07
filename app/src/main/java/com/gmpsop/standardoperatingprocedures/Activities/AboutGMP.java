package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gmpsop.standardoperatingprocedures.AppController;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.Models.GMPFiles;
import com.gmpsop.standardoperatingprocedures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AboutGMP extends Activity implements View.OnClickListener {
    private static final String TAG = DocumentFiles.class.getSimpleName();
    static final ArrayList<GMPFiles> gmpFiles = new ArrayList<>();
    GMPFiles rootDirectory;
    LinearLayout analyticalMicroBiologyLab, gmpQualityManagementSystem, gmpProcessCleaningMethodValidation, sterileNonSterileManufacturingOperation, gmpStandardOperatingProceduresForPharmaceuticals;
    //LinearLayout membershipNow, viewPricing, membersBenefit;
    boolean isError = false;
    boolean isLoading = false;

    public static final String MICROBIOLOGY = "Microbiology";
    public static final String QUALITY_MANAGEMENT = "Quality Management";
    public static final String VALIDATION = "Validation";
    public static final String STERILE = "Sterile";
    public static final String PHARMACEUTICAL = "Pharmaceuticals";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_gmp);
        init_components();
        populateInitList(Constants.GMP_OVER_VIEW);
    }

    public void init_components() {
        analyticalMicroBiologyLab = (LinearLayout) findViewById(R.id.aboutUsLinearButton_analytical_microBiology_lab);
        analyticalMicroBiologyLab.setOnClickListener(this);
        gmpQualityManagementSystem = (LinearLayout) findViewById(R.id.aboutUsLinearButton_gmp_quality_management_system);
        gmpQualityManagementSystem.setOnClickListener(this);
        gmpProcessCleaningMethodValidation = (LinearLayout) findViewById(R.id.aboutUsLinearButton_gmp_process_cleaning_method_validation);
        gmpProcessCleaningMethodValidation.setOnClickListener(this);
        sterileNonSterileManufacturingOperation = (LinearLayout) findViewById(R.id.aboutUsLinearButton_sterile_non_sterile_manufacturing_operation);
        sterileNonSterileManufacturingOperation.setOnClickListener(this);
        gmpStandardOperatingProceduresForPharmaceuticals = (LinearLayout) findViewById(R.id.aboutUsLinearButton_gmp_standard_operating_procedures_for_pharmaceuticals);
        gmpStandardOperatingProceduresForPharmaceuticals.setOnClickListener(this);
        /*membershipNow = (LinearLayout) findViewById(R.id.aboutUsLinearButton_MembershipNow);
        membershipNow.setOnClickListener(this);
        viewPricing = (LinearLayout) findViewById(R.id.aboutUsViewPricingLayout);
        viewPricing.setOnClickListener(this);
        membersBenefit = (LinearLayout) findViewById(R.id.aboutUsMembersBenefitLayout);
        membersBenefit.setOnClickListener(this);*/
        rootDirectory = new GMPFiles();
    }

    public void checkDocument(String fileName) {
        for (int i = 0; i < gmpFiles.size(); i++) {
            if (gmpFiles.get(i).getName().contains(fileName)) {
                Intent fileIntent = new Intent(this, DocumentFileReader.class);
                fileIntent.putExtra(Constants.GMP_FILE_ID, gmpFiles.get(i).getId());
                fileIntent.putExtra(Constants.GMP_FILE_NAME, gmpFiles.get(i).getName());
                fileIntent.putExtra(Constants.GMP_FILE_PATH, gmpFiles.get(i).getPath());
                fileIntent.putExtra(Constants.GMP_FILE_PARENT_ID, gmpFiles.get(i).getParentId());
                startActivity(fileIntent);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        switch (v.getId()) {
            case R.id.aboutUsLinearButton_analytical_microBiology_lab:
                if(isError){
                    MyToast.showLong(this, getString(R.string.errorLoading));
                    break;
                }else if(isLoading){
                    MyToast.showLong(this, getString(R.string.loading));
                    break;
                }
                checkDocument(MICROBIOLOGY);
                break;
            case R.id.aboutUsLinearButton_gmp_quality_management_system:
                if(isError){
                    MyToast.showLong(this, getString(R.string.errorLoading));
                    break;
                }else if(isLoading){
                    MyToast.showLong(this, getString(R.string.loading));
                    break;
                }
                checkDocument(QUALITY_MANAGEMENT);
                break;
            case R.id.aboutUsLinearButton_gmp_process_cleaning_method_validation:
                if(isError){
                    MyToast.showLong(this, getString(R.string.errorLoading));
                    break;
                }else if(isLoading){
                    MyToast.showLong(this, getString(R.string.loading));
                    break;
                }
                checkDocument(VALIDATION);
                break;
            case R.id.aboutUsLinearButton_sterile_non_sterile_manufacturing_operation:
                if(isError){
                    MyToast.showLong(this, getString(R.string.errorLoading));
                    break;
                }else if(isLoading){
                    MyToast.showLong(this, getString(R.string.loading));
                    break;
                }
                checkDocument(STERILE);
                break;
            case R.id.aboutUsLinearButton_gmp_standard_operating_procedures_for_pharmaceuticals:
                if(isError){
                    MyToast.showLong(this, getString(R.string.errorLoading));
                    break;
                }else if(isLoading){
                    MyToast.showLong(this, getString(R.string.loading));
                    break;
                }
                checkDocument(PHARMACEUTICAL);
                break;
            /*case R.id.aboutUsLinearButton_MembershipNow:
                MyToast.showShort(this, "membershipNow Clicked");
                break;
            case R.id.aboutUsViewPricingLayout:
                MyToast.showShort(this, "viewPricing Clicked");
                break;
            case R.id.aboutUsMembersBenefitLayout:
                MyToast.showShort(this, "membersBenefit Clicked");
                break;*/
        }
    }

    public void populateInitList(String file_name) {
        if (!InternetOperations.isNetworkConnected(this)) {
            //showError(noConnection);
            return;
        }
        isLoading=true;
        //showProgressBar();
        String tag_string_req = "req_register";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAMETER_FILE_NAME, file_name);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.SAMPLE_DOC_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                Log.d(TAG, "Register Response: " + response.toString());
                isLoading=false;
                //hideProgressBar();
//                    hideDialog();

                try {
                    int resMsg = response.getInt(Constants.DOCUMENT_RESPONSE_MSG);
                    if (resMsg == 0) {
                        gmpFiles.clear();
                        JSONObject rootDetails = response.getJSONObject(Constants.DOCUMENT_RESPONSE_ROOT_DETAIL);
                        setRootDirectory(rootDetails);
                        JSONArray list = response.getJSONArray(Constants.DOCUMENT_RESPONSE_DATA);
                        for (int i = 0; i < list.length(); i++) {
                            gmpFiles.add(new GMPFiles(list.getJSONObject(i).getInt(Constants.GMP_FILE_ID), list.getJSONObject(i).getString(Constants.GMP_FILE_NAME), list.getJSONObject(i).getString(Constants.GMP_FILE_TYPE), list.getJSONObject(i).getString(Constants.GMP_FILE_PATH), list.getJSONObject(i).getInt(Constants.GMP_FILE_PARENT_ID)));
                        }
                        // Launch login activity
                    } else if (resMsg == 2) {
                        isError = true;
                        gmpFiles.clear();
                        JSONObject rootDetails = response.getJSONObject(Constants.DOCUMENT_RESPONSE_ROOT_DETAIL);
                        setRootDirectory(rootDetails);
                        MyToast.showLong(getApplicationContext(),
                                getResources().getString(R.string.errorLoading));

                    } else {
                        isError = true;
                        MyToast.showLong(getApplicationContext(),
                                getResources().getString(R.string.errorLoading));
                    }
                } catch (JSONException e) {
                    isError = true;
                    MyToast.showLong(getApplicationContext(),
                            getResources().getString(R.string.errorLoading));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isError = true;
                error.printStackTrace();
                //TODO: handle failure
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MyToast.showLong(getApplicationContext(),
                        getResources().getString(R.string.errorLoading));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
