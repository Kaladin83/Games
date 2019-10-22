package com.example.maratbe.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Coordinates implements Parcelable {
    private int x;
    private int y;
    private int partRow = 0;
    private int partCol = 0;


    public Coordinates()
    {
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

    protected Coordinates(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        partRow = in.readInt();
        partCol = in.readInt();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x &&
                y == that.y &&
                partRow == that.partRow &&
                partCol == that.partCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, partRow, partCol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeInt(partRow);
        parcel.writeInt(partCol);
    }
}
