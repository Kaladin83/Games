package com.example.maratbe.games;

import java.util.Objects;

class Cell {
    private int x;
    private int y;
    private String value;
    private boolean enable;

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
