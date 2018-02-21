package com.gelostech.zoomsta.commoners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tirgei on 10/29/17.
 */

@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
    private static final String font = "Billabong.ttf";

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), font));
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), font));
    }

    public CustomTextView(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), font));
    }

}