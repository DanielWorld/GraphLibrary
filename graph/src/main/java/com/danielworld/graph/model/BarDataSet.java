package com.danielworld.graph.model;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarDataSet {

    private ArrayList<BarEntry> mEntries;

    private float mMinY = 0;
    private float mMaxY = Float.MIN_VALUE;

    private int mMinX = Integer.MAX_VALUE;
    private int mMaxX = Integer.MIN_VALUE;

    @ColorInt
    private int mBarColor;

    public BarDataSet(@NonNull ArrayList<BarEntry> entries, @ColorInt int barColor) {
        this.mEntries = entries;
        sort();
        this.calcXSize(entries);
        this.calcYSize(entries);
        this.mBarColor = barColor;
    }

    /**
     * Unlike {@link #BarDataSet(ArrayList, int)}, graph shows from {@code startX} to {@code endX} Entries.
     * @param entries
     * @param barColor
     * @param startX
     * @param endX
     */
    public BarDataSet(@NonNull ArrayList<BarEntry> entries, @ColorInt int barColor, int startX, int endX) {
        this.mEntries = entries;
        sort();
        fillEmptyEntries(startX, endX);
        this.calcXSize(entries);
        this.calcYSize(entries);
        this.mBarColor = barColor;
    }

    private void fillEmptyEntries(int startX, int endX) {
        if (startX > endX) throw new IllegalArgumentException("start X should be smaller than end X!");

        ArrayList<BarEntry> tempEntries = new ArrayList<>();
        int entryIndex = 0;
        for (int i = startX; i <= endX; i++)  {
            if (entryIndex < mEntries.size() && mEntries.get(entryIndex).getX() == i) {
                tempEntries.add(mEntries.get(entryIndex));
                entryIndex++;
            } else {
                tempEntries.add(new BarEntry(i, 0));
            }
        }

        mEntries.clear();
        mEntries.addAll(tempEntries);
    }

    /**
     * sort list by x value
     */
    private void sort() {
        Collections.sort(mEntries, new Comparator<BarEntry>() {
            @Override
            public int compare(BarEntry o1, BarEntry o2) {
                return o1.getX() > o2.getX() ? 1 : o1.getX() < o2.getX() ? -1 : 0;
            }
        });
    }

    /**
     * calculate min / max x value
     * @param xValues
     */
    private void calcXSize(ArrayList<BarEntry> xValues) {
        for (int i = 0; i < xValues.size(); i++) {
            int xValue = xValues.get(i).getX();
            mMaxX = Math.max(mMaxX, xValue);
            mMinX = Math.min(mMinX, xValue);
        }
    }

    /**
     * calculate min / max y value
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

    public boolean hasEntries() {
        return mEntries != null && !mEntries.isEmpty();
    }

    public float getMinY() {
        return mMinY;
    }

    public float getMaxY() {
        return mMaxY;
    }

    public int getMinX() {
        return mMinX;
    }

    public int getMaxX() {
        return mMaxX;
    }

    public int getEntrySize() {
        return mEntries.size();
    }

    public int getBarColor() {
        return mBarColor;
    }
}
