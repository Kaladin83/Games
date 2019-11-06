package com.example.maratbe.other;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import com.example.maratbe.dataBase.DataBase;
import com.example.maratbe.dataBase.MySharedPreferences;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.games.Sudoku;
import com.example.maratbe.games.TicTacToe;
import com.example.maratbe.listeners.MenuListenerImpl;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity {

    private static int screenWidth, screenHeight, logicalDensity, toolbarHeight, fontSize, currentTheme;

    private static DataBase db;

    private MySharedPreferences sp;

    public static DataBase getDb() {
        return db;
    }

    public static void setDb(DataBase db1) {
        db = db;
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static int getToolbarHeight()
    {
        return toolbarHeight;
    }

    public static int getLogicalDensity()
    {
        return logicalDensity;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        MainActivity.fontSize = fontSize;
    }

    public static void setCurrentTheme(int currentTheme) {
        MainActivity.currentTheme = currentTheme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = new MySharedPreferences(this);
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "Games database").build();
        setDimensions();
        getCurrentTheme();

        CardView ticTacToeBtn = findViewById(R.id.tictactoeCardView);
        ticTacToeBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, TicTacToe.class);
            startActivity(myIntent);
        });

        CardView sudokuBtn = findViewById(R.id.sudokuCardView);
        sudokuBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, Sudoku.class);
            startActivity(myIntent);
        });

        CardView kakuroBtn = findViewById(R.id.kakuroCardView);
        kakuroBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, Kakuro.class);
            startActivity(myIntent);
        });

        TextView kakuroTxt = findViewById(R.id.kakuroTxt);
        TextView sudokuTxt = findViewById(R.id.sudokuTxt);
        TextView ticTxt = findViewById(R.id.tictactoeTxt);

        kakuroTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        sudokuTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        ticTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void getCurrentTheme() {
        currentTheme = sp.getCurrentTheme();
        if (sp.getCurrentTheme() == 0)
        {
            currentTheme = R.style.SummerHeat;
            sp.saveTheme(currentTheme);
        }
        fontSize = getFontSize(currentTheme);
    }

    private int getFontSize(int theme) {
        switch (theme)
        {
            case R.style.SummerHeat:
                return 23;
            default: return 18;
        }
    }

    public void setDimensions()
    {
        int navigationBarHeight = 0;
        int resourceId = this.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = this.getResources().getDimensionPixelSize(resourceId);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels - navigationBarHeight;
        logicalDensity = (int) metrics.density;
        toolbarHeight = (int)getResources().getDimension(R.dimen.tool_bar);
    }
}
