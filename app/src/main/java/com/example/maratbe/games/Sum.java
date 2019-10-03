package com.example.maratbe.games;

import java.util.Objects;

public class Sum {
    private int value;
    private String direction;

    public Sum(){

    }

    public Sum(int value, String direction) {
        this.value = value;
        this.direction = direction;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sum sum = (Sum) o;
        return value == sum.value &&
                Objects.equals(direction, sum.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, direction);
    }
}
