package com.example.maratbe.other;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.maratbe.dataBase.DataBase;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.games.Sudoku;
import com.example.maratbe.games.TicTacToe;

public class MainActivity extends AppCompatActivity {

    private static int screenWidth, screenHeight, logicalDensity, toolbarHeight;

    private static DataBase db;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "Games database").build();
        setDimensions();

        Button ticTacToeBtn = findViewById(R.id.ticTacToeBtn);
        ticTacToeBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, TicTacToe.class);
            startActivity(myIntent);
        });

        Button sudokuBtn = findViewById(R.id.sudokuBtn);
        sudokuBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, Sudoku.class);
            startActivity(myIntent);
        });

        Button kakuroBtn = findViewById(R.id.kakuroBtn);
        kakuroBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, Kakuro.class);
            startActivity(myIntent);
        });
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