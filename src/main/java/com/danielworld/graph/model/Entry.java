package com.danielworld.graph.model;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/**
 * Graph 하나의 Entry 정보
 * <p>x, y 값, Entry 데이터를 지님</p>
 * <br><br>
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class Entry extends BaseEntry implements Parcelable{
    private int x;

    public Entry() {

    }

    public Entry(int x, float y) {
        super(y);
        this.x = x;
    }

    public Entry(int x, float y, Object data) {
        super(y, data);
        this.x = x;
    }

    protected Entry(Parcel in) {
        this.x = in.readInt();
        this.setY(in.readFloat());
        if(in.readInt() == 1) {
            this.setData(in.readParcelable(Object.class.getClassLoader()));
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Entry copy() {
        return new Entry(this.x, this.getY(), this.getData());
    }

    public boolean equalTo(Entry e) {
        return (e != null) && (e.getData() == this.getData()) && (e.getX() == (this.getX())) && (e.getY() == this.getY());
    }

    @Override
    public String toString() {
        return "Entry | x : " + this.getX() + " / y : " + this.getY();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeFloat(this.getY());
        if(this.getData() != null) {
            if(!(this.getData() instanceof Parcelable)) {
                throw new ParcelFormatException("Cannot parcel an Entry with non-parcelable data");
            }

            dest.writeInt(1);
            dest.writeParcelable((Parcelable)this.getData(), flags);
        } else {
            dest.writeInt(0);
        }
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
