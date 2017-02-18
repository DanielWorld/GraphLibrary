package com.danielworld.graph.model;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarData {

    protected List<BarDataSet> mBarDataList;

//    private double mMinY = Double.MAX_VALUE;
    private float mMinY = 0;   // 0 은 무조건 보여줘야 함..
    private float mMaxY = Float.MIN_VALUE;

    private int mMaxEntrySize = 0;  // BarDataSet 중 가장 많은 entry 수
    private int mXGap = 0;      // 하나의 Entry x 값 공간 크기


    public BarData(BarDataSet... dataSets) {
        this.mBarDataList = Arrays.asList(dataSets);

        for (BarDataSet barDataSet : mBarDataList) {
            mMinY = Math.min(mMinY, barDataSet.getMinY());
            mMaxY = Math.max(mMaxY, barDataSet.getMaxY());
            mMaxEntrySize = Math.max(mMaxEntrySize, barDataSet.getEntrySize());
        }
    }

    public List<BarDataSet> getBarDataList() {
        return mBarDataList;
    }

    public float getMinY() {
        return mMinY;
    }

    public float getMaxY() {
        return mMaxY;
    }

    public int getMaxEntrySize() {
        return mMaxEntrySize;
    }

    /**
     * 그래프를 설정할 size 설정 <br>
     *     <p>1. 각각 그래프 x 갯수, 및 최소/최대 y 값을 이용해서 설정</p>
     * @param width
     * @param height
     */
    public void setContainerSize(float width, float height) {
       if (width <= 0 || height <= 0 || getMaxEntrySize() <= 0) return;

        mXGap = (int) (width / getMaxEntrySize());   // x range 범위

        // 1. 해당 mXGap 을 이용해서 x range 설정하기
        // 2. maxY 와 height 로 해당 Entry 의 실제 그래프 y좌표 설정하기
        for (BarDataSet barDataSet : mBarDataList) {
            int tempXGap = 0;
            float tempYGap = 1;
            for (BarEntry barEntry : barDataSet.getEntries()) {
                if (mMaxY > 0) {
                    tempYGap = height / mMaxY;
                }
                barEntry.setEntryYCoordinate(tempYGap * barEntry.getY());

                barEntry.setEntryXRange(tempXGap, tempXGap + mXGap);
                tempXGap += mXGap;
            }
        }
    }

    public int getXGap() {
        return mXGap;
    }
}
