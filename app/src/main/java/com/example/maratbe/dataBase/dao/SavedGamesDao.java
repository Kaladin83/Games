package com.example.maratbe.dataBase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.maratbe.dataBase.dto.SavedGame;

import java.util.List;
@Dao
public interface SavedGamesDao {
    @Query("SELECT id FROM SavedGame WHERE name = :name and game_name = :gameName")
    int getId(String name, String gameName);

    @Query("SELECT name FROM SavedGame WHERE game_name = :gameName")
    List<String> getAllNames(String gameName);

    @Query("SELECT * FROM SavedGame WHERE game_name = :gameName AND name = :name")
    SavedGame getAllCellsByName(String gameName, String name);

    @Insert
    void saveGame(SavedGame savedGame);

    @Query("DELETE FROM SavedGame")
    void deleteAllGames();

    @Delete
    void deleteGame(SavedGame savedGame);
//
//    @Delete
//    void deleteGame(int id);
}
