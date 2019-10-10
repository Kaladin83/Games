package com.example.maratbe.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Coordinates implements Parcelable {
    private int x;
    private int y;
    private int partRow;
    private int partCol;
    private int value;
    private int color;
    private boolean enabled;

    public Coordinates()
    {
        value = 0;
    }

    public Coordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Coordinates(int x, int y, int partRow, int partCol)
    {
        this.x = x;
        this.y = y;
        this.partRow = partRow;
        this.partCol = partCol;
    }

    public Coordinates(int x, int y, int partRow, int partCol, int value, int color)
    {
        this.x = x;
        this.y = y;
        this.partRow = partRow;
        this.partCol = partCol;
        this.color = color;
        this.value = value;
    }

    protected Coordinates(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        partRow = in.readInt();
        partCol = in.readInt();
        color = in.readInt();
        value = in.readInt();
        enabled = in.readInt() == 1;
    }

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(partRow);
        dest.writeInt(partCol);
        dest.writeInt(value);
        dest.writeInt(color);
        dest.writeInt(enabled? 1: 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return getX() == that.getX() &&
                getY() == that.getY() &&
                getPartRow() == that.getPartRow() &&
                getPartCol() == that.getPartCol() &&
                getValue() == that.getValue();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getX(), getY(), getPartRow(), getPartCol(), getValue());
    }
}
