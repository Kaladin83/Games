package com.example.maratbe.dataBase.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.maratbe.dataBase.dto.Kakuro;

import java.util.ArrayList;
import java.util.List;

public interface KakuroDao {
    @Query("SELECT * FROM kakuro WHERE id IN (SELECT id FROM SavedGames WHERE game_name = :name)")
    List<Kakuro> getAllCellsByName(String name);

    @Insert
    void insertKakuroCell(Kakuro kakuro);

    @Insert
    void insertAll(ArrayList<Kakuro> allCells);

    @Delete
    void delete(Kakuro kakuro);
}
