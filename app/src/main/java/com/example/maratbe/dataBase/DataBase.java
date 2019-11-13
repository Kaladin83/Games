package com.example.maratbe.dataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.maratbe.dataBase.dao.SavedGamesDao;
import com.example.maratbe.dataBase.dto.SavedGame;

@Database(entities = {SavedGame.class}, version = 1, exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public abstract SavedGamesDao savedGamesDao();
}
