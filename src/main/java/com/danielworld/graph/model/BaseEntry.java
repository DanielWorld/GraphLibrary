package com.danielworld.graph.model;

/**
 * Graph's primitive Entry
 * <p>has y value and data</p>
 * <br><br>
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public abstract class BaseEntry {
    private float y;
    private Object mData;

    public BaseEntry() {
        this.y = 0.0f;
        this.mData = null;
    }

    public BaseEntry(float y) {
        this.y = y;
        this.mData = null;
    }

    public BaseEntry(float y, Object data) {
        this.y = y;
        this.mData = data;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }
}
