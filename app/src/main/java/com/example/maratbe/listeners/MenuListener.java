package com.example.maratbe.listeners;

import java.util.List;

public interface MenuListener {

    void startNewGame();

    void saveGame(List<Object> list);

    void loadGame();

    void backToGame();

    void chooseTheme();

    void onVictory();
}
