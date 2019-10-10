package com.example.maratbe.dataBase.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Kakuro{
    @PrimaryKey
    private int id;
    private int x;
    private int y;
    private String value;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
