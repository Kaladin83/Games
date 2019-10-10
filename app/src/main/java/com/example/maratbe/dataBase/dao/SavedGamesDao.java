package com.example.maratbe.dataBase.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.maratbe.games.Kakuro;

import java.util.ArrayList;
import java.util.List;

public interface SavedGamesDao {
    @Query("SELECT id FROM SavedGames WHERE name = :name and game_name = :gameName")
    int getId(String name, String gameName);

    @Insert
    void saveGame(String name, String gameName);

    @Delete
    void deleteGame(String name, String gameName);

    @Delete
    void deleteGame(int id);
}
