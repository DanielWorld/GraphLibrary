package com.danielworld.graph.model;

/**
 * One Entry's specific information in graph
 * <p>value x, value y, range coordinates x, coordinates y, Entry data</p>
 * <br><br>
 * Copyright (c) 2014-2017 op7773hons@gmail.com
 * Created by Daniel Park on 2017-02-18.
 */

public class BarEntry extends Entry {
    private Range entryXRange;
    private float entryYCoordinate;    // real y coordinates in Graph size (TODO: not y value!)

    public BarEntry(int x, float y) {
        super(x, y);
    }

    /**
     * set Entry x coordinates range
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
     * set y coordinates in Graph
     * @param yCoordinate
     */
    public void setEntryYCoordinate(float yCoordinate) {
        this.entryYCoordinate = yCoordinate;
    }

    public float getEntryYCoordinate() {
        return entryYCoordinate;
    }

    /**
     * get center x from {@link #entryXRange}
     * @return
     */
    public float getEntryCenterX() {
        if (entryXRange == null) entryXRange = new Range(0, 0);
        return (entryXRange.getFrom() + entryXRange.getTo()) / 2;
    }
}
