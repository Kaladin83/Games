package com.example.maratbe.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Cell implements Parcelable {
    private int index;
    private Coordinates coordinates;
    private ArrayList<Integer> value = new ArrayList<>();
    private ArrayList<Sum> sums = new ArrayList<>();
    private int color;
    private boolean enabled;

    public Cell(){
        enabled = true;
        value.add(0);
    }

    public Cell(Coordinates coordinates, int value) {
        this.coordinates = coordinates;
        this.value.add(value);
    }

    public Cell(Coordinates coordinates, Integer value, int color, boolean enabled) {
        this.coordinates = coordinates;
        this.value.add(value);
        this.color = color;
        this.enabled = enabled;
    }

    public Cell(Coordinates coordinates, ArrayList<Integer> value, ArrayList<Sum> sums, boolean enabled) {
        this.coordinates = coordinates;
        this.value = value;
        this.sums = sums;
        this.enabled = enabled;
    }

    protected Cell(Parcel in) {
        index = in.readInt();
        coordinates = in.readParcelable(Coordinates.class.getClassLoader());
        color = in.readInt();
        enabled = in.readByte() != 0;
        in.readList(sums, ArrayList.class.getClassLoader());
        in.readList(value, ArrayList.class.getClassLoader());
    }

    public static final Creator<Cell> CREATOR = new Creator<Cell>() {
        @Override
        public Cell createFromParcel(Parcel in) {
            return new Cell(in);
        }

        @Override
        public Cell[] newArray(int size) {
            return new Cell[size];
        }
    };

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Integer> getValue() {
        return value;
    }

    public void setValue(ArrayList<Integer> value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value.add(value);
    }

    public ArrayList<Sum> getSums() {
        return sums;
    }

    public void setSums(ArrayList<Sum> sums) {
        this.sums = sums;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(index);
        parcel.writeParcelable(coordinates, i);
        parcel.writeInt(color);
        parcel.writeByte((byte) (enabled ? 1 : 0));
        parcel.writeList(sums);
        parcel.writeList(value);
    }
}
