package com.example.maratbe.translators;

import com.example.maratbe.dataBase.dto.SavedGame;
import com.example.maratbe.dataBase.dto.TransferData;
import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.domain.Sum;
import com.example.maratbe.other.Constants;

import java.util.ArrayList;
import java.util.List;

public class ObjectTranslator implements Constants {

    public SavedGame savedGameFromTransferData(TransferData transferData, String gameName)
    {
        SavedGame savedGame = new SavedGame();
        savedGame.setSudokuLevel(transferData.getLevel());
        savedGame.setHintValues(getValuesString(transferData.getListOfHints(), gameName));
        savedGame.setValues(getValuesString(transferData.getListOfCells(), gameName));
        savedGame.setNumberOfHints(transferData.getNumberOfHintsLeft());
        savedGame.setTimePlayed(transferData.getTimeToAdd());
        savedGame.setGameName(gameName);
        savedGame.setChosenName(transferData.getChosenName());
        return savedGame;
    }

    private String getValuesString(List<Cell> list, String gameName) {
        StringBuilder sb = new StringBuilder();
        list.forEach(c -> convertCellsToString(sb, c, gameName));
        return sb.toString();
    }

    private void convertCellsToString(StringBuilder sb, Cell c, String gameName) {
        StringBuilder sb1 = new StringBuilder();
        if (c.getSums().size() > 0)
        {
            c.getSums().forEach(c1 -> convertValuesToString(sb1, c1));
        }
        else
        {
            sb1.append(c.getValue().get(c.getValue().size() - 1)).append(c.isEnabled()? "t": "f");
        }

        if (KAKURO.equals(gameName))
        {
            append2Coordinates(c, sb, sb1);
        }
        else
        {
            append4Coordinates(c, sb, sb1);
        }
    }

    private void append2Coordinates(Cell c, StringBuilder sb, StringBuilder sb1) {
        sb.append(c.getCoordinates().getX()).append(c.getCoordinates().getY()).
                append(":").append(sb1.toString()).append(";");
    }

    private void append4Coordinates(Cell c, StringBuilder sb, StringBuilder sb1) {
        sb.append(c.getCoordinates().getX()).append(c.getCoordinates().getPartRow()).append(c.getCoordinates().getPartCol()).
                append(c.getCoordinates().getY()).append(":").append(sb1.toString()).append(";");
    }

    private void convertValuesToString(StringBuilder sb1, Sum c1) {
        sb1.append(c1.getValue()).append("#").append(c1.getDirection()).append("%");
    }

    public TransferData transferDataFromSavedGame(SavedGame savedGame)
    {
        TransferData transferData = new TransferData();
        transferData.setLevel(savedGame.getSudokuLevel());
        transferData.setChosenName(savedGame.getChosenName());
        transferData.setTimeToAdd(savedGame.getTimePlayed());
        transferData.setNumberOfHintsLeft(savedGame.getNumberOfHints());
        transferData.setListOfCells(splitStringOfValues(savedGame.getValues(), savedGame.getGameName()));
        transferData.setListOfHints(splitStringOfValues(savedGame.getHintValues(), savedGame.getGameName()));
        return transferData;
    }

    private List<Cell> splitStringOfValues(String rawStringList, String gameName) {
        List<Cell> list = new ArrayList<>();
        String[] cells = rawStringList.split(";");

        for (int i = 0; i < cells.length; i++)
        {
            String[] rawCell = cells[i].split(":");
            Cell cell = new Cell();
            Coordinates c = splitIntoCoordinates(rawCell[0], gameName);
            splitIntoValueAndSums(rawCell[1], cell);
            cell.setCoordinates(c);
            cell.setIndex(list.size());
            list.add(cell);
        }
        return list;
    }

    private Coordinates splitIntoCoordinates(String cell, String gameName) {
        return SUDOKU.equals(gameName)? new Coordinates(Integer.parseInt(cell.substring(0, 1)), Integer.parseInt(cell.substring(1, 2)),
                Integer.parseInt(cell.substring(2, 3)), Integer.parseInt(cell.substring(3, 4))):
                new Coordinates(Integer.parseInt(cell.substring(0, 1)), Integer.parseInt(cell.substring(1, 2)));
    }

    private void splitIntoValueAndSums(String rawCell, Cell cell) {
        if(rawCell.length() == 2)
        {
            cell.setValue(Integer.parseInt(rawCell.substring(0, 1)));
            cell.setEnabled(rawCell.substring(1, 2).equals("t"));
        }
        else
        {
            parseSums(rawCell ,cell);
        }
    }

    private void parseSums(String rawCell, Cell cell) {
        String[] sums = rawCell.split("%");
        cell.setEnabled(false);
        if (sums.length == 1)
        {
            addSumToCell(sums[0], cell);
        }
        if (sums.length == 2)
        {
            addSumToCell(sums[0], cell);
            addSumToCell(sums[1], cell);
        }
    }

    private void addSumToCell(String sum, Cell cell) {
        String[] sum1 = sum.split("#");
        if (sum1.length > 1)
        {
            cell.getSums().add(new Sum(Integer.parseInt(sum1[0]), sum1[1]));
        }
    }
}
