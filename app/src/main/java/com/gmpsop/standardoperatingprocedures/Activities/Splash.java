package com.gmpsop.standardoperatingprocedures.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.gmpsop.standardoperatingprocedures.Helper.SessionManager;
import com.gmpsop.standardoperatingprocedures.Helper.UIHelper;
import com.gmpsop.standardoperatingprocedures.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash extends Activity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //keyhash();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    // User is already logged in. Take him to main activity
                    Intent intent = new Intent(Splash.this,
                            Dashboard.class);
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }

    public void keyhash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.gmpsop.standardoperatingprocedures", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
