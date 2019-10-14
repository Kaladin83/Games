package com.example.maratbe.dataBase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.maratbe.dataBase.dto.KakuroTable;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface KakuroDao {
    @Query("SELECT * FROM KakuroTable WHERE id IN (SELECT id FROM SavedGames WHERE game_name = :name)")
    List<KakuroTable> getAllCellsByName(String name);

    @Insert
    void insertKakuroCell(KakuroTable kakuro);

    @Insert
    void insertAll(ArrayList<KakuroTable> allCells);

    @Delete
    void delete(KakuroTable kakuro);
}
