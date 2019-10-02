package com.example.maratbe.games;

import android.graphics.Color;

public interface Constants {
    int WHITE = Color.parseColor("#ffffff");
    int BLACK_1 = Color.parseColor("#716e72");
    int BLACK_2 = Color.parseColor("#767477");
    int GRAY_1 = Color.parseColor("#e0e0e0");
    int GRAY_2 = Color.parseColor("#f2efef");
    int GRAY_3 = Color.parseColor("#e1dede");

    int RED_1 = Color.parseColor("#f7120e");
    int RED_2 = Color.parseColor("#ff3b2d");
    int RED_3 = Color.parseColor("#db3632");
    int RED_4 = Color.parseColor("#f79491");
    int RED_5 = Color.parseColor("#fcd2d1");
    int GREEN_1 = Color.parseColor("#20e83a");
    int GREEN_2 = Color.parseColor("#25c63a");
    int GREEN_3 = Color.parseColor("#87f295");
    int GREEN_4 = Color.parseColor("#eafcd1");
    int GREEN_5 = Color.parseColor("#e8f4e0");
    int GREEN_6 = Color.parseColor("#fff9fff0");

    int BLUE_1 = Color.parseColor("#2e79f4");
    int BLUE_2 = Color.parseColor("#7fa9ef");
    int BLUE_3 = Color.parseColor("#ceebef");
    int BLUE_4 = Color.parseColor("#dae3f2");
    int BLUE_5 = Color.parseColor("#f4f9fc");
    int BLUE_6 = Color.parseColor("#eaebed");

    int YELLOW_1 = Color.parseColor("#d3f713");
    int YELLOW_2 = Color.parseColor("#f4ec58");
    int YELLOW_3 = Color.parseColor("#e7f986");
    int YELLOW_4 = Color.parseColor("#eff9bb");
    int ORANGE = Color.parseColor("#fc8225");
    int PINK = Color.parseColor("#ff75d1");
    int PURPLE_1 = Color.parseColor("#cf63ed");
    int PURPLE_2 = Color.parseColor("#db9ced");
    int PURPLE_3 = Color.parseColor("#ebc0f7");
    int PURPLE_4 = Color.parseColor("#eddcf2");

    String HORIZONTAL = "Horizontal";
    String VERTICAL = "Vertical";
    String NO_CONFLICT = "No Conflict";
    int FUNCTION_FILL_UP_CELL_MAP = 1;
    int FUNCTION_COLOR_CELL_VICTORY = 3;
    int FUNCTION_EMPTY_BOARD = 4;
    int FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE = 5;
    int FUNCTION_READ_LIST_FROM_SAVED_INSTANCE = 6;
    int FUNCTION_POPULATE_MATRIX = 7;

    int DIFFICULTY_START_EASY = 3;
    int DIFFICULTY_START_MODERATE = 4;
    int DIFFICULTY_START_HARD = 5;
    int DIFFICULTY_RANGE_EASY = 2;
    int DIFFICULTY_RANGE_MODERATE = 3;
    int DIFFICULTY_RANGE_HARD = 3;

    int KAKURO_CELL = 33;
}
