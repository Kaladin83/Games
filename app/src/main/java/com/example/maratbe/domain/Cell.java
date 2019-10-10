package com.example.maratbe.domain;

import java.util.ArrayList;
import java.util.Objects;

public class Cell {
    private int x;
    private int y;
    private String value;
    private boolean enable;
    private ArrayList<Sum> sum = new ArrayList<>();

    public Cell()
    {

    }

    public Cell(int x, int y, String value, boolean enable) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.enable = enable;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ArrayList<Sum> getSum() {
        return sum;
    }

    public void setSum(ArrayList<Sum> sum) {
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return getX() == cell.getX() &&
                getY() == cell.getY() &&
                isEnable() == cell.isEnable() &&
                Objects.equals(getValue(), cell.getValue());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getX(), getY(), getValue(), isEnable());
    }
}
