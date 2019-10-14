package com.example.maratbe.listeners;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public interface MenuListener {

    void startNewGame(Object gameInstance, Context context);

    void saveGame(Object gameInstance,Context context, List<Object> list);

    void loadGame(Object gameInstance, Context context);
}
