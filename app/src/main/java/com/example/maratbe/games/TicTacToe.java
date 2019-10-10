package com.example.maratbe.games;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.maratbe.domain.Counters;

public class TicTacToe extends AppCompatActivity{

    private Button b00, b01, b02, b10, b11, b12, b20, b21, b22, startGameBtn;
    private RadioGroup signChoiceLayout, aiChoiceLayout;
    private RadioButton compRadio, humanRadio, xRadio, oRadio;
    private String[] victories = new String[0];

    private char choice = 'X';
    private boolean isAi = true;
    private boolean aITurn = false;
    private Counters counters = new Counters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tic_tac_toe);

        buildGui();
        Button newGameBtn = findViewById(R.id.newGameBtn);
        newGameBtn.setOnClickListener(v ->
                resetBoard()
        );

        startGameBtn = findViewById(R.id.startGameBtn);
        startGameBtn.setOnClickListener(v ->
                startGame()
        );
    }

    private void startGame() {
        if (counters.getNumOfTurns() == 0)
        {
            rollTurn();
            resetButtons(false, false, Color.BLACK);
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
        signChoiceLayout = findViewById(R.id.signChoiceLayout);
        aiChoiceLayout = findViewById(R.id.aiChoiceLayout);
        compRadio = findViewById(R.id.compRadio);
        compRadio.setOnClickListener(v ->
            enableRadios(true, true)
        );
        humanRadio = findViewById(R.id.humanRadio);
        humanRadio.setOnClickListener(v ->
                enableRadios(true, false)
        );
        xRadio = findViewById(R.id.xRadio);
        xRadio.setOnClickListener(v ->
            choice = 'X'
        );
        oRadio = findViewById(R.id.oRadio);
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

    private void resetBoard() {
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
        int cellColor, textColor;

        if (isVictory)
        {
            cellColor = ContextCompat.getColor(this, R.color.colorPrimary);
            textColor = Color.WHITE;
            enableButtons(false);
        }
        else
        {
            cellColor = ContextCompat.getColor(this, R.color.green1);
            textColor = ContextCompat.getColor(this, R.color.lightBlack);
        }
        setFields(cellColor, textColor, b0, b1, b2);
    }

    private void setFields(int cellColor, int textColor, Button b0, Button b1, Button b2) {
        b0.setBackgroundColor(cellColor);
        b0.setTextColor(textColor);
        b1.setBackgroundColor(cellColor);
        b1.setTextColor(textColor);
        b2.setBackgroundColor(cellColor);
        b2.setTextColor(textColor);
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
