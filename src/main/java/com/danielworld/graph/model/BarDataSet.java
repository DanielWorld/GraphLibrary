package com.danielworld.graph.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import java.util.ArrayList;

/**
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarDataSet {

    private ArrayList<BarEntry> mEntries;

//    private double mMinY = Double.MAX_VALUE;     // 최소 y값
    private float mMinY = 0;     // 0은 무조건 보여줘야 함
    private float mMaxY = Float.MIN_VALUE;     // 최대 y값

    @ColorInt
    private int mBarColor;

//    public BarDataSet(ArrayList<BarEntry> entries) {
//        this.mEntries = entries;
//        this.calcYSize(entries);
//        this.mBarColor = Color.parseColor("#119203");
//    }

    public BarDataSet(ArrayList<BarEntry> entries, @ColorInt int barColor) {
        this.mEntries = entries;
        this.calcYSize(entries);
        this.mBarColor = barColor;
    }

    /**
     * 최소 / 최대 y값 구하기
     * @param yValues
     */
    private void calcYSize(ArrayList<BarEntry> yValues) {
        for (int i = 0; i < yValues.size(); i++) {
            float yValue = yValues.get(i).getY();
            mMaxY = Math.max(mMaxY, yValue);
            mMinY = Math.min(mMinY, yValue);
        }
    }

    public ArrayList<BarEntry> getEntries() {
        return mEntries;
    }

    public float getMinY() {
        return mMinY;
    }

    public float getMaxY() {
        return mMaxY;
    }

    public int getEntrySize() {
        return mEntries.size();
    }

    public int getBarColor() {
        return mBarColor;
    }
}
