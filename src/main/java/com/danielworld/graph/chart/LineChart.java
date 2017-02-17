package com.danielworld.graph.chart;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-17.
 */

public class LineChart extends Chart {
    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(ArrayList<String> testData) {
        xData = testData;
        invalidate();
    }
}
