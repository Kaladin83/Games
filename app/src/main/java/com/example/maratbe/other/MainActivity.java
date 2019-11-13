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
import com.example.maratbe.domain.Theme;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.games.Sudoku;
import com.example.maratbe.games.TicTacToe;
import com.example.maratbe.listeners.MenuListenerImpl;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity implements Constants{

    private static int screenWidth, screenHeight, logicalDensity;

    private static List<Theme> themes = new ArrayList<>();

    private static Theme currentTheme;

    private static DataBase db;

    private MySharedPreferences sp;

    public static DataBase getDb() {
        return db;
    }

    public static void setDb(DataBase db1) {
        db = db1;
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static int getLogicalDensity()
    {
        return logicalDensity;
    }

    public static void setCurrentTheme(Theme currentTheme_) {
        currentTheme = currentTheme_;
    }

    public static Theme getCurrentTheme()
    {
        return currentTheme;
    }

    public static List<Theme> getThemes() {
        return themes;
    }

    public static void setThemes(List<Theme> themes) {
        MainActivity.themes = themes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemes();
        setCurrentTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "Games database").build();
        setDimensions();


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

        kakuroTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        sudokuTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        ticTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
    }

    private void setThemes() {
        themes.clear();
        themes.add(new Theme("Summer heat",R.style.SummerHeat,
                "Summer heat dialog", R.style.SummerHeatDialog, 24));
        themes.add(new Theme("Spring Blossom",R.style.SpringBlossom,
                "Spring Blossom dialog", R.style.SpringBlossomDialog, 18));
    }

    private void setCurrentTheme() {
        sp = new MySharedPreferences(this);
        //sp.clearTheme();
        currentTheme = sp.getCurrentTheme();
        if (currentTheme == null)
        {
            currentTheme = themes.get(0);
            sp.saveTheme(currentTheme);
        }
        setTheme(currentTheme.getThemeId());
        setCurrentTheme(currentTheme);
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
    }
}
