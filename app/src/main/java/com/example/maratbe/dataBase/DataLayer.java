package com.example.maratbe.dataBase;

import com.example.maratbe.dataBase.dto.Kakuro;
import com.example.maratbe.domain.Cell;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.translators.KakuroTranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataLayer implements Constants {
    private DataBase db = MainActivity.getDb();

    public boolean isNameExists(String name, String gameName){
        Integer id = db.savedGamesDao().getId(name, gameName);
        return id != null;
    }

    public void saveKakuroGame(String name, HashMap<String, Cell> mapOfCells)
    {
        db.savedGamesDao().saveGame(name, KAKURO);
        int id = db.savedGamesDao().getId(name, KAKURO);
        ArrayList<Kakuro> lisOfCells = new ArrayList<>();
        KakuroTranslator translator = new KakuroTranslator();
        for (HashMap.Entry<String, Cell> entry : mapOfCells.entrySet())
        {
            lisOfCells.add(translator.daoObjectFromKakuroCell(entry.getValue(), id));
        }
        db.kakuroDao().insertAll(lisOfCells);
    }

    public HashMap<String, Cell> loadKakuroGame(String name)
    {
        HashMap<String, Cell> mapOfCells = new HashMap<>();
        List<Kakuro> listOfKakuroCells = db.kakuroDao().getAllCellsByName(name);
        KakuroTranslator translator = new KakuroTranslator();
        for (Kakuro kakuroCell: listOfKakuroCells)
        {
            mapOfCells.put("r"+kakuroCell.getX()+"b"+kakuroCell.getY() ,translator.cellFromDaoObject(kakuroCell));
        }
        return mapOfCells;
    }
}
