package com.example.maratbe.dataBase.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SavedGame {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String chosenName;

    @ColumnInfo(name = "game_name")
    private String gameName;

    @ColumnInfo(name = "num_of_hints")
    private int numberOfHints;

    @ColumnInfo(name = "time_played")
    private long timePlayed;

    @ColumnInfo(name = "values")
    private String values;

    @ColumnInfo(name = "hint_values")
    private String hintValues;

    @ColumnInfo(name = "sudoku_level")
    private int sudokuLevel;

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getChosenName() {
        return chosenName;
    }

    public void setChosenName(String name) {
        this.chosenName = name;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getNumberOfHints() {
        return numberOfHints;
    }

    public void setNumberOfHints(int numberOfHints) {
        this.numberOfHints = numberOfHints;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getHintValues() {
        return hintValues;
    }

    public void setHintValues(String hintValues) {
        this.hintValues = hintValues;
    }

    public int getSudokuLevel() {
        return sudokuLevel;
    }

    public void setSudokuLevel(int sudokuLevel) {
        this.sudokuLevel = sudokuLevel;
    }
}
