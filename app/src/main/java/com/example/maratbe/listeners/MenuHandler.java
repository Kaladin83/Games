package com.example.maratbe.listeners;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.maratbe.dataBase.dto.TransferData;
import com.example.maratbe.games.R;
import com.example.maratbe.games.TicTacToe;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

import io.alterac.blurkit.BlurLayout;


public abstract class MenuHandler implements Constants, OnClickListener {
    private RelativeLayout rLayout;
    private BlurLayout menuLayout;
    private TableLayout numberLayout;
    private MenuListener menuListener;
    private Object gameInstance;

    MenuHandler(RelativeLayout rLayout, TableLayout numberLayout, Object gameInstance) {
        this.rLayout = rLayout;
        this.numberLayout = numberLayout;
        this.gameInstance = gameInstance;

        new MenuListenerImpl(this, gameInstance);
        buildMenu();
    }

    void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    private void buildMenu() {
        LayoutInflater inflater = (LayoutInflater) rLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuLayout = (BlurLayout) inflater.inflate(R.layout.menu, rLayout.findViewById(R.id.mainMenuLayout));

        if (rLayout.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            addToLayout(ORIENTATION_LANDSCAPE);
        } else {
            addToLayout(ORIENTATION_PORTRAIT);
        }
    }

    BlurLayout getBlurLayout()
    {
        return menuLayout;
    }

    private void addToLayout(String orientation) {
        if (orientation.equals(ORIENTATION_LANDSCAPE))
        {
            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(265*MainActivity.getLogicalDensity() + 30 * MainActivity.getLogicalDensity(),
                    MainActivity.getScreenWidth());
            lparams.leftMargin = 30 * MainActivity.getLogicalDensity();
            lparams.addRule(RelativeLayout.ALIGN_PARENT_START);
            lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rLayout.addView(menuLayout, lparams);
        }
        else
        {
            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(),
                    getMenuHeight() * MainActivity.getLogicalDensity());
            lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rLayout.addView(menuLayout, lparams);
        }
    }

    private int getMenuHeight() {
      return gameInstance instanceof TicTacToe? 2*125: 3*125;
    }

    void setControlButtonsSize() {
        for (int i = 0; i < numberLayout.getChildCount(); i++)
        {
            TableRow row = (TableRow) numberLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++)
            {
                Button b = ((Button) row.getChildAt(j));
                b.setHeight(33 * MainActivity.getLogicalDensity());
            }
        }
    }

    void showMenu() {
        Utils.slideToTop(menuLayout);

        LinearLayout saveButton = rLayout.findViewById(R.id.saveButton);
        LinearLayout loadButton = rLayout.findViewById(R.id.loadButton);
        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
        TextView saveGameTxt = rLayout.findViewById(R.id.saveGameText);
        TextView loadGameTxt = rLayout.findViewById(R.id.loadGameText);
        saveGameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        loadGameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        LinearLayout startGameButton = rLayout.findViewById(R.id.startButton);
        LinearLayout backButton = rLayout.findViewById(R.id.backButton);
        LinearLayout themeButton = rLayout.findViewById(R.id.themeButton);
        startGameButton.setOnClickListener(this);

        if (gameInstance instanceof TicTacToe)
        {
            saveButton.setVisibility(View.GONE);
            loadButton.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(this);
        themeButton.setOnClickListener(this);

        TextView startGameTxt = rLayout.findViewById(R.id.startGameText);

        TextView backTxt = rLayout.findViewById(R.id.returnToGameText);
        TextView chooseThemeTxt = rLayout.findViewById(R.id.chooseThemeText);
        startGameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        backTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        chooseThemeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
    }

    void showVictoryDialog()
    {
        menuListener.onVictory();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.startButton: menuListener.startNewGame(); break;
            case R.id.saveButton: saveGame(menuListener); break;
            case R.id.loadButton: handleLoadedData(); break;
            case R.id.themeButton: menuListener.chooseTheme(); break;
            default: menuListener.backToGame(); break;
        }
        Utils.slideToBottom(menuLayout);
        if (!(gameInstance instanceof TicTacToe))
        {
            setControlButtonsSize();
        }
    }

    private void handleLoadedData() {
        menuListener.loadGame();
    }

    public abstract void saveGame(MenuListener menuListener);
}
