package com.example.maratbe.dataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.maratbe.dataBase.dao.KakuroDao;
import com.example.maratbe.dataBase.dao.SavedGamesDao;
import com.example.maratbe.dataBase.dto.KakuroTable;
import com.example.maratbe.dataBase.dto.SavedGames;

@Database(entities = {KakuroTable.class, SavedGames.class}, version = 1, exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public abstract KakuroDao kakuroDao();
    public abstract SavedGamesDao savedGamesDao();
}
