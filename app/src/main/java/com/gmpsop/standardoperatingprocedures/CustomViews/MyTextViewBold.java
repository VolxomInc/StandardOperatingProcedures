package com.gmpsop.standardoperatingprocedures.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by BD1 on 19-Apr-17.
 */

public class MyTextViewBold extends android.support.v7.widget.AppCompatTextView {
    public MyTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/century_gothic_bold.ttf");
        setTypeface(tf);

    }
}
