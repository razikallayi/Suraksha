package com.razikallayi.suraksha_ssf.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Razi Kallayi on 07-02-2016.
 */
public class FontUtils {
    public static final String ROOT = "fonts/";
    public static final String FONTAWSOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(),font);
    }

//    tv.setTypeface(com.razikallayi.suraksha.FontUtils.getTypeface(com.razikallayi.suraksha.FontUtils.FONTAWSOME));


    public static void markAsIconContainer(View v, Typeface typeface){
        if (v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++){
                View child = vg.getChildAt(i);
                markAsIconContainer(child,typeface);
            }
        }else if (v instanceof TextView){
            ((TextView) v).setTypeface(typeface);
        }
    }
}
