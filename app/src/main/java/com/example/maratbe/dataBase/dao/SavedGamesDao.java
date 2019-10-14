package com.example.maratbe.dataBase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.maratbe.dataBase.dto.SavedGames;
import com.example.maratbe.games.Kakuro;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface SavedGamesDao {
    @Query("SELECT id FROM SavedGames WHERE name = :name and game_name = :gameName")
    int getId(String name, String gameName);

    @Query("SELECT name FROM SavedGames WHERE game_name = :gameName")
    List<String> getAllNames(String gameName);

    @Insert
    void saveGame(SavedGames savedGames);

    @Delete
    void deleteGame(SavedGames savedGames);
//
//    @Delete
//    void deleteGame(int id);
}
