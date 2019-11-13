package com.example.maratbe.listeners;

import com.example.maratbe.dataBase.dto.TransferData;

public interface MenuListener {

    void startNewGame();

    void saveGame(TransferData transferData);

    TransferData loadGame();

    void backToGame();
}
