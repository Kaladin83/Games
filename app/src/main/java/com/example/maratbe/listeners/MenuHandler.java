package com.example.maratbe.listeners;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.maratbe.games.R;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

public abstract class MenuHandler implements Constants, OnClickListener {
    private RelativeLayout rLayout;
    private LinearLayout menuLayout;
    private TableLayout numberLayout;
    private MenuListener menuListener;

    MenuHandler(RelativeLayout rLayout, TableLayout numberLayout, Object gameInstance) {
        this.rLayout = rLayout;
        this.numberLayout = numberLayout;

        new MenuListenerImpl(this, gameInstance);
        buildMenu();
    }

    void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    private void buildMenu() {
        LayoutInflater inflater = (LayoutInflater) rLayout.getContext().getSystemService(rLayout.getContext().LAYOUT_INFLATER_SERVICE);
        menuLayout = (LinearLayout) inflater.inflate(R.layout.menu, rLayout.findViewById(R.id.mainMenuLayout));
        menuLayout.setBackground(Utils.createGradientBackground(GREEN_4, WHITE));

        if (rLayout.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            addToLayout(ORIENTATION_LANDSCAPE);
        } else {
            addToLayout(ORIENTATION_PORTRAIT);
        }
    }

    private void addToLayout(String orientation) {
        if (orientation.equals(ORIENTATION_LANDSCAPE))
        {
            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams((int) (MainActivity.getScreenHeight() * 0.5),
                    MainActivity.getScreenWidth());
            lparams.addRule(RelativeLayout.ALIGN_PARENT_END);
            lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rLayout.addView(menuLayout, lparams);
        }
        else
        {
            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(),
                    MainActivity.getScreenHeight()/2);
            lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rLayout.addView(menuLayout, lparams);
        }
    }

    private void setControlButtonsSize() {
        for (int i = 0; i < numberLayout.getChildCount(); i++)
        {
            TableRow row = (TableRow) numberLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++)
            {
                Button b = ((Button) row.getChildAt(j));
                System.out.println("MeasuredHeight = "+ b.getMeasuredHeight());
                System.out.println("height = "+ b.getHeight());
                b.setHeight(33 * MainActivity.getLogicalDensity());
                System.out.println("correct height = "+ 33 * MainActivity.getLogicalDensity());
            }
        }
    }

    void showMenu() {
        Utils.slideToTop(menuLayout);
        Button startGameButton = rLayout.findViewById(R.id.startButton);
        Button saveButton = rLayout.findViewById(R.id.saveButton);
        Button loadButton = rLayout.findViewById(R.id.loadButton);
        Button backButton = rLayout.findViewById(R.id.backButton);
        startGameButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.startButton: menuListener.startNewGame(); break;
            case R.id.saveButton: saveGame(menuListener); break;
            case R.id.loadButton: menuListener.loadGame(); break;
            default: menuListener.backToGame(); break;
        }
        Utils.slideToBottom(menuLayout);
        setControlButtonsSize();
    }

    public abstract void saveGame(MenuListener menuListener);
}
