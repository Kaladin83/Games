package com.example.maratbe.dataBase.dto;

import com.example.maratbe.domain.Cell;

import java.util.ArrayList;
import java.util.List;

public class TransferData {
    private List<Cell> listOfCells = new ArrayList<>();
    private List<Cell> listOfHints = new ArrayList<>();
    private int numberOfHintsLeft;
    private long timeToAdd;
    private String chosenName;
    private int level;

    public List<Cell> getListOfCells() {
        return listOfCells;
    }

    public void setListOfCells(List<Cell> listOfCells) {
        this.listOfCells = listOfCells;
    }

    public List<Cell> getListOfHints() {
        return listOfHints;
    }

    public void setListOfHints(List<Cell> listOfHints) {
        this.listOfHints = listOfHints;
    }

    public int getNumberOfHintsLeft() {
        return numberOfHintsLeft;
    }

    public void setNumberOfHintsLeft(int numberOfHintsLeft) {
        this.numberOfHintsLeft = numberOfHintsLeft;
    }

    public long getTimeToAdd() {
        return timeToAdd;
    }

    public void setTimeToAdd(long timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public String getChosenName() {
        return chosenName;
    }

    public void setChosenName(String chosenName) {
        this.chosenName = chosenName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
