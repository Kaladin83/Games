package com.example.maratbe.dataBase;

import com.example.maratbe.dataBase.dto.SavedGame;
import com.example.maratbe.dataBase.dto.TransferData;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.translators.ObjectTranslator;

import java.util.List;

public class DataLayer implements Constants {
    private DataBase db = MainActivity.getDb();


    public boolean isNameExists(String name, String gameName){
        //db.savedGamesDao().deleteAllGames();
        Integer id = db.savedGamesDao().getId(name, gameName);
        return id != 0;
    }

    public void saveGame(String game, TransferData transferData){
        ObjectTranslator translator = new ObjectTranslator();
        SavedGame savedGame = translator.savedGameFromTransferData(transferData, game);
        db.savedGamesDao().saveGame(savedGame);
    }

    public TransferData loadGame(String game, String nameToLoad)
    {
        TransferData data = new TransferData();
        SavedGame savedGame = db.savedGamesDao().getAllCellsByName(game ,nameToLoad);
        if (savedGame != null)
        {
            ObjectTranslator translator = new ObjectTranslator();
            data = translator.transferDataFromSavedGame(savedGame);
        }
        return data;
    }

    public List<String> loadAllGameNames(String game)
    {
        return db.savedGamesDao().getAllNames(game);
    }
}
