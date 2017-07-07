package com.gmpsop.standardoperatingprocedures.Helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;

/**
 * Created by BD1 on 17-Apr-17.
 */

public class UIHelper {

    public static Typeface setFontTypeFace(Context context) {
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "century_gothic.ttf"));
        return typeface;
    }
}
