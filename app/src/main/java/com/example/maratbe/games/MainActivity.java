package com.example.maratbe.games;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
