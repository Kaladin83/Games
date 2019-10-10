package com.example.maratbe.other;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class Utils {

    public static Drawable createBorder(int radius, int color, int strokeWidth, int strokeColor) {
        GradientDrawable gd;
        gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        gd.setStroke(strokeWidth, strokeColor);
        return gd;
    }
}
