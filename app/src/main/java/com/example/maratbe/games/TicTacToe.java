package com.example.maratbe.games;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.domain.Counters;
import com.example.maratbe.listeners.ClickHandler;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToe extends AppCompatActivity implements Constants {

    private Button b00, b01, b02, b10, b11, b12, b20, b21, b22, startGameBtn;
    private RadioGroup signChoiceLayout, aiChoiceLayout;
    private RadioButton compRadio, humanRadio, xRadio, oRadio;
    private String[] victories = new String[0];

    private char choice = 'X';
    private boolean isAi = true;
    private boolean aITurn = false;
    private Counters counters = new Counters();
    private ClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MainActivity.getCurrentTheme().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tic_tac_toe);

        buildGui();
        findViewById(R.id.startGameBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.timeBtn).setVisibility(View.GONE);
        findViewById(R.id.chronometer).setVisibility(View.GONE);
        startGameBtn = findViewById(R.id.startGameBtn);
        startGameBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        startGameBtn.setOnClickListener(v ->
                startGame()
        );

        TextView moreBtn = findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(v ->
                clickHandler.onMenuButtonClicked()
        );
        if(savedInstanceState != null)
        {
            getValuesFromBundle(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("victories", victories);
        outState.putCharArray("cells", counters.getArrayFromMatrix());
        outState.putIntArray("countersX", counters.getCountersX());
        outState.putIntArray("counters0", counters.getCounters0());
        outState.putBoolean("isAiTurn", aITurn);
        outState.putBoolean("isAi", isAi);
        outState.putInt("numOfTurns", counters.getNumOfTurns());
        outState.putChar("choice", choice);
    }

    public void startGame() {
        if (counters.getNumOfTurns() == 0)
        {
            rollTurn();
            resetButtons(false, false, GRAY_2);
            if (aITurn && isAi)
            {
                aiMove(choice);
            }
            enableButtons(true);
        }
    }

    private void resetButtons(boolean bool, boolean xBool, int color) {
        enableRadios(bool, xBool);
        startGameBtn.setEnabled(bool);
        startGameBtn.setTextColor(color);
    }

    private void enableRadios(boolean bool, boolean xBool) {
        aiChoiceLayout.setEnabled(bool);
        signChoiceLayout.setEnabled(xBool);
        xRadio.setEnabled(xBool);
        oRadio.setEnabled(xBool);
        compRadio.setEnabled(bool);
        humanRadio.setEnabled(bool);
    }

    private void rollTurn() {
        isAi = compRadio.isChecked();
        choice = oRadio.isChecked()? '0': 'X';
        if (isAi)
        {
            if (choice == '0')
            {
                aITurn = true;
                choice = 'X';
            }
            else
            {
                aITurn = false;
            }
        }
    }

    private void buildGui() {
        setupClickHandler();
        signChoiceLayout = findViewById(R.id.signChoiceLayout);
        aiChoiceLayout = findViewById(R.id.aiChoiceLayout);
        compRadio = findViewById(R.id.compRadio);
        compRadio.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        compRadio.setOnClickListener(v ->
            enableRadios(true, true)
        );
        humanRadio = findViewById(R.id.humanRadio);
        humanRadio.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        humanRadio.setOnClickListener(v ->
                enableRadios(true, false)
        );
        xRadio = findViewById(R.id.xRadio);
        xRadio.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        xRadio.setOnClickListener(v ->
            choice = 'X'
        );
        oRadio = findViewById(R.id.oRadio);
        oRadio.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        oRadio.setOnClickListener(v ->
                choice = '0'
        );

        b00 = findViewById(R.id.b00);
        b00.setOnClickListener(v ->
                putChoice(0,0, choice, b00)
        );

        b01 = findViewById(R.id.b01);
        b01.setOnClickListener(v ->
                putChoice(0,1, choice, b01)
        );

        b02 = findViewById(R.id.b02);
        b02.setOnClickListener(v ->
                putChoice(0,2, choice, b02)
        );

        b10 = findViewById(R.id.b10);
        b10.setOnClickListener(v ->
                putChoice(1,0, choice, b10)
        );

        b11 = findViewById(R.id.b11);
        b11.setOnClickListener(v ->
                putChoice(1,1, choice, b11)
        );

        b12 = findViewById(R.id.b12);
        b12.setOnClickListener(v ->
                putChoice(1,2, choice, b12)
        );

        b20 = findViewById(R.id.b20);
        b20.setOnClickListener(v ->
                putChoice(2,0, choice, b20)
        );

        b21 = findViewById(R.id.b21);
        b21.setOnClickListener(v ->
                putChoice(2,1, choice, b21)
        );

        b22 = findViewById(R.id.b22);
        b22.setOnClickListener(v ->
                putChoice(2,2, choice, b22)
        );
        enableButtons(false);
    }

    @SuppressWarnings("unchecked")
    private void getValuesFromBundle(Bundle savedInstanceState) {
        counters = new Counters();
        counters.setMatrixFromArray(savedInstanceState.getCharArray("cells"));
        populateCells();
        counters.setCounters0(savedInstanceState.getIntArray("counters0"));
        counters.setCountersX(savedInstanceState.getIntArray("countersX"));
        isAi = savedInstanceState.getBoolean("isAi");
        aITurn = savedInstanceState.getBoolean("isAiTurn");
        choice = savedInstanceState.getChar("choice");
        String[] v = savedInstanceState.getStringArray("victories");
        victories = Arrays.copyOf(v, v.length);
        counters.setNumOfTurns(savedInstanceState.getInt("numOfTurns"));
        if (counters.getNumOfTurns()>0)
        {
            resetButtons(false, false, GRAY_2);
            enableButtons(true);
            compRadio.setChecked(isAi);
            humanRadio.setChecked(!isAi);
            oRadio.setChecked(choice == '0');
            xRadio.setChecked(choice == 'X');
            enableRadios(false, false);
            colorButtons(!victories[0].equals("dr"));
        }
    }

    private void populateCells() {
        b00.setText(String.valueOf(counters.getMatrix()[0][0]));
        b01.setText(String.valueOf(counters.getMatrix()[0][1]));
        b02.setText(String.valueOf(counters.getMatrix()[0][2]));
        b10.setText(String.valueOf(counters.getMatrix()[1][0]));
        b11.setText(String.valueOf(counters.getMatrix()[1][1]));
        b12.setText(String.valueOf(counters.getMatrix()[1][2]));
        b20.setText(String.valueOf(counters.getMatrix()[2][0]));
        b21.setText(String.valueOf(counters.getMatrix()[2][1]));
        b22.setText(String.valueOf(counters.getMatrix()[2][2]));
    }

    private void setupClickHandler() {
        RelativeLayout relativeLayout = findViewById(R.id.mainRelativeLayout);
        clickHandler = new ClickHandler(findViewById(R.id.numbersLayout), findViewById(R.id.revert), findViewById(R.id.hintLayout)) {
            @Override
            protected void colorCellsForHints() {
            }

            @Override
            protected void updateCellRevertValue(Cell previous) {
            }

            @Override
            protected void updateCellNewValue(boolean isPreviousExists) {
            }

            @Override
            protected void handleChronometer(boolean isPaused) {
            }
        };

        clickHandler.setupMenuLayout(relativeLayout, this);
    }

    public void resetBoard() {
        clearFields();
        colorButtons(false);
        counters = new Counters();
        choice = 'X';
        rollTurn();
        resetButtons(true, true, ContextCompat.getColor(this, R.color.red));
        xRadio.setChecked(true);
    }

    private void colorButtons(boolean isVictory) {
        for (String victory: victories) {
            switch(victory)
            {
                case "r0": setFields(b00, b01, b02, isVictory); break;
                case "r1": setFields(b10, b11, b12, isVictory); break;
                case "r2": setFields(b20, b21, b22, isVictory); break;
                case "c0": setFields(b00, b10, b20, isVictory); break;
                case "c1": setFields(b01, b11, b21, isVictory); break;
                case "c2": setFields(b02, b12, b22, isVictory); break;
                case "d0": setFields(b00, b11, b22, isVictory); break;
                case "d1": setFields(b02, b11, b20, isVictory); break;
            }
        }
    }

    private void clearFields() {
        b00.setText("");
        b01.setText("");
        b02.setText("");
        b11.setText("");
        b12.setText("");
        b10.setText("");
        b20.setText("");
        b21.setText("");
        b22.setText("");
    }

    private void putChoice(int i, int j, char c, Button b) {
        aITurn = !aITurn;
        b.setText(String.valueOf(c));
        b.setEnabled(false);
        counters.setI(i);
        counters.setJ(j);
        counters.setSign(c);

        victories = checkVictory();

        colorButtons(true);
        choice = choice == 'X'? '0': 'X';
        if (isAi && aITurn && victories[0].equals(""))
        {
            aiMove(choice);
        }
    }

    private void setFields(Button b0, Button b1, Button b2, boolean isVictory) {
        if (isVictory)
        {
            enableButtons(false);
        }
        setFields(isVictory, b0, b1, b2);
    }

    private void setFields(boolean isVictory, Button b0, Button b1, Button b2) {
        b0.setSelected(isVictory);
        b1.setSelected(isVictory);
        b2.setSelected(isVictory);
    }

    private String[] checkVictory() {
        String[] result = counters.getResult().split(",");
        if (!result[0].equals(""))
        {
            return result;
        }

        if (counters.getNumOfTurns() == 9)
        {
            return new String[]{"dr"};
        }
        return new String[]{""};
    }

    private void enableButtons(boolean bool) {
        b00.setEnabled(bool);
        b01.setEnabled(bool);
        b02.setEnabled(bool);
        b10.setEnabled(bool);
        b11.setEnabled(bool);
        b12.setEnabled(bool);
        b20.setEnabled(bool);
        b21.setEnabled(bool);
        b22.setEnabled(bool);
    }

    private void aiMove(char sign) {
        int[] index = counters.setFirstMove('X');
        if (index[0] == -1)
        {
            index = counters.setSpecialMove();
            if (index[0] == -1)
            {
                index = counters.getBestChoice(sign);
            }
        }
        putChoice(index[0], index[1], sign, getButton(index));
    }

    private Button getButton(int[] index) {
        switch (index[0]) {
            case 0:
                switch (index[1]) {
                    case 0:  return b00;
                    case 1:  return b01;
                    case 2:  return b02;
                }
            case 1:
                switch (index[1]) {
                    case 0: return b10;
                    case 1: return b11;
                    case 2: return b12;
                }
            case 2:
                switch (index[1]) {
                    case 0: return b20;
                    case 1: return b21;
                    case 2: return b22;
            }
        }
        return b00;
    }
}
