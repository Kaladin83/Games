package com.example.maratbe.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coordinates implements Parcelable {
    private int index;
    private int x;
    private int y;
    private int partRow;
    private int partCol;
    private ArrayList<Integer> value = new ArrayList<>();
    private int color;
    private boolean enabled;


    public Coordinates()
    {
        value.add(0);
    }

    public Coordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
        enabled = true;
    }

    public Coordinates(int x, int y, int partRow, int partCol)
    {
        this.x = x;
        this.y = y;
        this.partRow = partRow;
        this.partCol = partCol;
        enabled = true;
        setValue(0);
    }

    public Coordinates(int x, int y, int partRow, int partCol, int value, int color)
    {
        this.x = x;
        this.y = y;
        this.partRow = partRow;
        this.partCol = partCol;
        this.color = color;
        this.value.add(value);
    }

    protected Coordinates(Parcel in) {
        index = in.readInt();
        x = in.readInt();
        y = in.readInt();
        partRow = in.readInt();
        partCol = in.readInt();
        color = in.readInt();
        enabled = in.readByte() != 0;
        value = in.readArrayList(null);
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };

    public int getPartRow() {
        return partRow;
    }

    public void setPartRow(int partRow) {
        this.partRow = partRow;
    }

    public int getPartCol() {
        return partCol;
    }

    public void setPartCol(int partCol) {
        this.partCol = partCol;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Integer> getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value.add(value);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return getX() == that.getX() &&
                getY() == that.getY() &&
                getPartRow() == that.getPartRow() &&
                getPartCol() == that.getPartCol();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getX(), getY(), getPartRow(), getPartCol(), getValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(index);
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeInt(partRow);
        parcel.writeInt(partCol);
        parcel.writeInt(color);
        parcel.writeByte((byte) (enabled ? 1 : 0));
        parcel.writeList(value);
    }
}
