package com.example.maratbe.other;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.listeners.ClickHandler;

import java.util.ArrayList;

import io.alterac.blurkit.BlurLayout;

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
                new int[] {color1,colorInMiddle,color1, colorInMiddle});
        gd.setCornerRadius(0f);
        return gd;
    }

    public static void slideToBottom(View view){
        if (view.getVisibility() == View.VISIBLE)
        {
            TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
            animate.setDuration(400);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            setVisibility(view, View.GONE);
        }
    }

    public static void slideToTop(View view){
        if (view.getVisibility() != View.VISIBLE)
        {
            TranslateAnimation animate = new TranslateAnimation(0,0,view.getHeight(),0);
            animate.setDuration(400);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            setVisibility(view, View.VISIBLE);
        }

    }

    private static void setVisibility(View view, int visible) {

        BlurLayout lLayout = (BlurLayout) view;
        for (int i = 0; i< lLayout.getChildCount(); i++)
        {
            for (int j = 0; j< lLayout.getChildCount(); j++)
            {
                if (lLayout.getChildAt(i) instanceof  LinearLayout)
                {
                    ((LinearLayout)lLayout.getChildAt(i)).getChildAt(j).setVisibility(visible);
                }
                lLayout.getChildAt(i).setVisibility(visible);
            }
        }
        view.setVisibility(visible);
    }

    public static ArrayList<Cell> translateMatrixIntoList(int[][] hintMatrix, int size) {
        ArrayList<Cell> listOfHints = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                listOfHints.add(new Cell(new Coordinates(i, j), hintMatrix[i][j]));
            }
        }
        return listOfHints;
    }

    public static void translateListIntoMatrix(ArrayList<Cell> listOfHints, int[][] matrix) {
        for (Cell cell: listOfHints)
        {
            Coordinates c = cell.getCoordinates();
            matrix[c.getX()][c.getY()] = cell.getValue().get(cell.getValue().size() - 1);
        }
    }


    public static String updateValueWhenHintPressed(ClickHandler clickHandler, String hintValue) {
        String chosenNumber = clickHandler.getChosenNumber();
        if (clickHandler.isHintPressed())
        {
            chosenNumber = hintValue;
            clickHandler.setChosenNumber(chosenNumber);
        }
        return chosenNumber;
    }

    public static long handleChronometer(Chronometer chronometer, Button timeButton, long pauseOffset, boolean isPauseButtonShowing) {
        if (isPauseButtonShowing)
        {
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.stop();
        }
        else
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset );
            chronometer.start();
        }
        timeButton.setSelected(!timeButton.isSelected());
        return pauseOffset;
    }
}
