package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmpsop.standardoperatingprocedures.Helper.Constants;
import com.gmpsop.standardoperatingprocedures.Helper.InternetOperations;
import com.gmpsop.standardoperatingprocedures.Helper.MyToast;
import com.gmpsop.standardoperatingprocedures.R;

import java.util.ArrayList;

public class SearchOption extends Activity implements View.OnClickListener {
    private static final String TAG = SearchOption.class.getSimpleName();
    protected static final int RESULT_SPEECH = 2;
    LinearLayout voiceButton, searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_option);
        init_components();
    }

    public void init_components() {
        voiceButton = (LinearLayout) findViewById(R.id.search_option_voice_layout_button);
        voiceButton.setOnClickListener(this);
        searchButton = (LinearLayout) findViewById(R.id.search_option_search_layout_button);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!InternetOperations.isNetworkConnected(this)) {
            MyToast.showLong(this, getString(R.string.noInternetConnection));
            return;
        }
        switch (v.getId()) {
            case R.id.search_option_voice_layout_button:
                Log.d(TAG, "Voice Clicked");
                voiceSearch();
                break;
            case R.id.search_option_search_layout_button:
                Log.d(TAG, "Search Clicked");
                startActivity(new Intent(this, SearchGMP.class).putExtra(Constants.INTENT_SEARCH_TERM, ""));
                break;
        }
    }

    public void voiceSearch() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    startActivity(new Intent(this, SearchGMP.class).putExtra(Constants.INTENT_SEARCH_TERM, text.get(0)));

                }
                break;
            }

        }
    }
}
