package com.gmpsop.standardoperatingprocedures.Helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by BD1 on 21-Mar-17.
 */

public class MyToast {

    public static void showShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
