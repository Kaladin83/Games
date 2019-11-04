package com.example.maratbe.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.maratbe.games.R;

@SuppressLint("AppCompatCustomView")
public class SudokuButton extends TextView {
    private boolean stateColor;

    private static final int[] STATE_COLOR = {R.attr.state_color};

    public SudokuButton(Context context) {
        super(context, null, R.attr.sudoku_cell);
    }

    public SudokuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SudokuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SudokuButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean getStateColor() {
        return stateColor;
    }

    public void setStateColor(boolean stateColor) {
        this.stateColor = stateColor;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        mergeDrawableStates(drawableState, STATE_COLOR);
        return drawableState;
    }
}
