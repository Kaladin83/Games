package com.example.maratbe.listeners;

import android.view.View;

public interface ClickListener {

    void onCellClick(String name);

    void onButtonControlClicked(View view);

    void onLayoutControlClicked(View view);

    void onMenuButtonClicked();

    void onTimeButtonClicked(boolean isPaused);

    void onVictory();
}
