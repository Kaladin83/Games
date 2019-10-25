package com.example.maratbe.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Sum implements Parcelable {
    private int value;
    private String direction;

    public Sum(int value, String direction) {
        this.value = value;
        this.direction = direction;
    }

    private Sum(Parcel in) {
        value = in.readInt();
        direction = in.readString();
    }

    public static final Creator<Sum> CREATOR = new Creator<Sum>() {
        @Override
        public Sum createFromParcel(Parcel in) {
            return new Sum(in);
        }

        @Override
        public Sum[] newArray(int size) {
            return new Sum[size];
        }
    };

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDirection() {
        return direction;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(value);
        parcel.writeString(direction);
    }
}
