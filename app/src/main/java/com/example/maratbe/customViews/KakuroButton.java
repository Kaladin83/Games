package com.example.maratbe.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.example.maratbe.games.R;

@SuppressLint("AppCompatCustomView")
public class KakuroButton extends TextView {
    private boolean stateLevel1;
    private boolean stateLevel2;
    private static final int[] STATE_LEVEL_1 = {R.attr.state_level_1};
    private static final int[] STATE_LEVEL_2 = {R.attr.state_level_2};

    public KakuroButton(Context context) {
        super(context, null, 0,R.style.KakuroButtonStyle);
    }

    public KakuroButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KakuroButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KakuroButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean getStateLevel1() {
        return stateLevel1;
    }
    public void setStateLevels(boolean stateLevel1, boolean stateLevel2) {
        this.stateLevel1 = stateLevel1;
        this.stateLevel2 = stateLevel2;
        refreshDrawableState();
    }

    public void setStateLevel1(boolean stateLevel1) {
        this.stateLevel1 = stateLevel1;
        refreshDrawableState();
    }

    public boolean getStateLevel2() {
        return stateLevel1;
    }

    public void setStateLevel2(boolean stateLevel2) {
        this.stateLevel2 = stateLevel2;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (stateLevel1)
        {
            mergeDrawableStates(drawableState, STATE_LEVEL_1);
        }

        if (stateLevel2)
        {
            mergeDrawableStates(drawableState, STATE_LEVEL_2);
        }
        return drawableState;
    }
}
