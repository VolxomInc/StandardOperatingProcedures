package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Timer;

public class AskGMP extends Activity {



    CircularProgressBar cpBar;
    ImageView speakButton;
    Chronometer chn;

    private static final int MIN_CLICK_DURATION = 600;
    private long startClickTime;
    private boolean longClickActive;
    private boolean recording, pause = false;
    private long elapsed;
    private long remaningSecs = 0;
    private long elapsedSecs = 0;
    private Timer timer;

    TextView txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_gmp);
        init_components();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void init_components() {

        txtText = (TextView) findViewById(R.id.ask_gmp_speechText);
        cpBar = (CircularProgressBar) findViewById(R.id.ask_gmp_progressBar);
        speakButton = (ImageView) findViewById(R.id.ask_gmp_speakButton);
        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        chn = (Chronometer) findViewById(R.id.ask_gmp_timer);
    }


}
