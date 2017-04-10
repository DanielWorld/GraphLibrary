package com.danielworld.graph.model;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarData {

    protected List<BarDataSet> mBarDataList;

    private float mMinY = 0;
    private float mMaxY = Float.MIN_VALUE;

    private int mMinX = Integer.MAX_VALUE;
    private int mMaxX = Integer.MIN_VALUE;

    private int mMaxEntrySize = 0;  // the max entry size among BarDataSet
    private int mXGap = 0;      // the width size of space in one Entry


    public BarData(BarDataSet... dataSets) {
        this.mBarDataList = Arrays.asList(dataSets);

        for (BarDataSet barDataSet : mBarDataList) {
            mMinY = Math.min(mMinY, barDataSet.getMinY());
            mMaxY = Math.max(mMaxY, barDataSet.getMaxY());

            mMinX = Math.min(mMinX, barDataSet.getMinX());
            mMaxX = Math.max(mMaxX, barDataSet.getMaxX());

            mMaxEntrySize = Math.max(mMaxEntrySize, barDataSet.getEntrySize());
        }
    }

    public List<BarDataSet> getBarDataList() {
        return mBarDataList;
    }

    public boolean hasBarDataList() {
        return mBarDataList != null && !mBarDataList.isEmpty();
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

    public int getMaxEntrySize() {
        return mMaxEntrySize;
    }

    /**
     * set size to calculate graph size <br>
     * @param width
     * @param height
     */
    public void setContainerSize(float width, float height) {
       if (width <= 0 || height <= 0 || getMaxEntrySize() <= 0) return;

        mXGap = (int) (width / getMaxEntrySize());   // x range 범위

        // 1. set x range using mXGap
        // 2. set y coordinates using maxY and height
        for (BarDataSet barDataSet : mBarDataList) {
            int tempXGap = 0;
            float tempYGap = 1;
            for (BarEntry barEntry : barDataSet.getEntries()) {
                if (mMaxY > 0) {
                    tempYGap = height / mMaxY;
                }

                if (barEntry.getY() > 0)
                    barEntry.setEntryYCoordinate(tempYGap * barEntry.getY());
                else
                    barEntry.setEntryYCoordinate(0);

                barEntry.setEntryXRange(tempXGap, tempXGap + mXGap);
                tempXGap += mXGap;
            }
        }
    }

    public int getXGap() {
        return mXGap;
    }
}
