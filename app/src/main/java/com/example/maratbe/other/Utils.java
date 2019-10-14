package com.example.maratbe.other;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import java.util.List;

public class Utils {

    public static Drawable createBorder(int radius, int color, int strokeWidth, int strokeColor) {
        GradientDrawable gd;
        gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        gd.setStroke(strokeWidth, strokeColor);
        return gd;
    }

    public static Drawable createGradientBackground(int color1, int colorInMiddle) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {color1,colorInMiddle,color1});
        gd.setCornerRadius(0f);
        return gd;
    }

    public static void slideToBottom(View view){
        if (view.getVisibility() == View.VISIBLE)
        {
            TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
            animate.setDuration(500);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            setVisibility(view, View.GONE);
        }
    }

    public static void slideToTop(View view){
        if (view.getVisibility() != View.VISIBLE)
        {
            TranslateAnimation animate = new TranslateAnimation(0,0,view.getHeight(),0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            setVisibility(view, View.VISIBLE);
        }

    }

    private static void setVisibility(View view, int visible) {

        LinearLayout lLayout = (LinearLayout) view;
        for (int i = 0; i< lLayout.getChildCount(); i++)
        {
            lLayout.getChildAt(i).setVisibility(visible);
        }
        view.setVisibility(visible);
    }
}
