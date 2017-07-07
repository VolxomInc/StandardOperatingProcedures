package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DocumentFileReader extends AppCompatActivity {
    private static final String TAG = DocumentFileReader.class.getSimpleName();
    PDFView reader;
    URL url;
    ProgressDialog pDialog;
    String path, name, id, parentId;

    LinearLayout errorLayout;
    ImageView errorImageView;
    TextView errorTextView;
    String noConnection, errorLoading;
    InputStream ims;
    Drawable d;
    boolean isError = false;

    //ImageView back, next;
    //TextView currentPage, pageCount;

    Handler hnd, hndShowError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_file_reader);
        Bundle data = getIntent().getExtras();
        id = data.getString(Constants.GMP_FILE_ID);
        path = data.getString(Constants.GMP_FILE_PATH);
        name = data.getString(Constants.GMP_FILE_NAME);
        parentId = data.getString(Constants.GMP_FILE_PARENT_ID);
        setCustomActionBar(name);
        init_Components();
        if (!InternetOperations.isNetworkConnected(this)) {
            showError(noConnection);
            return;
        }
        NativeViewASync myAsync = new NativeViewASync();
        myAsync.execute(path);
    }

    public void setCustomActionBar(String fileName){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_custom_layout, null);

        TextView title = (TextView) v.findViewById(R.id.actionBar_fileName);
        title.setText(fileName);
        actionBar.setCustomView(v);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init_Components() {
        reader = (PDFView) findViewById(R.id.pdfView);
        /*back = (ImageView) findViewById(R.id.documentReaderBackButton);
        next = (ImageView) findViewById(R.id.documentReaderNextButton);
        currentPage = (TextView) findViewById(R.id.documentReaderCurrentPage);
        currentPage.setText("0");
        pageCount = (TextView) findViewById(R.id.documentReaderPageCount);
        pageCount.setText("0");*/
        getSupportActionBar().setTitle(name);
        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        errorLayout = (LinearLayout) findViewById(R.id.documentReaderErrorLayout);
        errorImageView = (ImageView) findViewById(R.id.documentReaderErrorImage);
        errorTextView = (TextView) findViewById(R.id.documentReaderErrorMsg);
        noConnection = getResources().getString(R.string.noInternetConnection);
        errorLoading = getResources().getString(R.string.errorLoading);
        hnd = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                /*pageCount.setText(String.valueOf(reader.getPageCount()));
                currentPage.setText(String.valueOf(reader.getCurrentPage()));*/
                pDialog.dismiss();
                return true;
            }
        });
        hndShowError = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                /*pageCount.setText(String.valueOf(reader.getPageCount()));
                currentPage.setText(String.valueOf(reader.getCurrentPage()));*/
                showError(errorLoading);
                return true;
            }
        });

    }

    public void nativeView(String urlStr) {
        try {
            url = new URL(urlStr);
            Log.d(TAG,"URL STRING : "+urlStr);
            Log.d(TAG,"URL STRING : "+url.getPath());
            InputStream inputStreamSource = url.openStream();
            try {
                reader.fromStream(inputStreamSource)
                        .defaultPage(0)
                        .onLoad(new com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(final int nbPages) {
                                //pDialog.dismiss();
                                hnd.sendEmptyMessage(0);
                            }
                        })
                        .load();

            } catch (Exception e) {
                showError(errorLoading);
                hndShowError.sendEmptyMessage(0);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            hndShowError.sendEmptyMessage(0);
        }

    }

    public void showError(String type) {
        reader.setVisibility(View.GONE);
        pDialog.dismiss();
        errorLayout.setVisibility(View.VISIBLE);
        isError = true;
        if (getResources().getString(R.string.noInternetConnection).equals(type)) {
            try {
                // get input stream
                ims = getAssets().open("no_internet.png");
                // load image as Drawable
                d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                errorImageView.setImageDrawable(d);
            } catch (IOException ex) {
                ex.printStackTrace();
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
            }
            errorTextView.setText(type);
        }
    }

    class NativeViewASync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            nativeView(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
