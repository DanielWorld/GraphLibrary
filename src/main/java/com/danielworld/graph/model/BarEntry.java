package com.danielworld.graph.model;

/**
 * Graph 하나의 구체적인 Entry 정보
 * <p>x, y 값, x, y 범위, Entry 데이터를 지님</p>
 * <br><br>
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarEntry extends Entry {
    private Range entryXRange;
    private float entryYCoordinate;    // 그래프 상의 실제 y 좌표값 (TODO: y값이 아님!!)

    public BarEntry(int x, float y) {
        super(x, y);
    }

    /**
     * 해당 Entry x 의 범위 설정
     * @param x
     * @param y
     */
    public void setEntryXRange(float x, float y) {
        if (entryXRange == null) {
            entryXRange = new Range(x, y);
        }
        else {
            entryXRange.setFrom(x);
            entryXRange.setTo(y);
        }
    }

    public Range getEntryXRange() {
        if (entryXRange == null) entryXRange = new Range(0, 0);
        return entryXRange;
    }

    /**
     * 그래프 상의 실제 y 좌표값 설정
     * @param yCoordinate
     */
    public void setEntryYCoordinate(float yCoordinate) {
        this.entryYCoordinate = yCoordinate;
    }

    public float getEntryYCoordinate() {
        return entryYCoordinate;
    }

    /**
     * Entry x 사이 좌표 centerX 값
     * @return
     */
    public float getEntryCenterX() {
        if (entryXRange == null) entryXRange = new Range(0, 0);
        return (entryXRange.getFrom() + entryXRange.getTo()) / 2;
    }
}
