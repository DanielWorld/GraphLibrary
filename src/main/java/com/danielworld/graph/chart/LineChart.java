package com.danielworld.graph.chart;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.danielworld.graph.model.BarData;
import com.danielworld.graph.model.ValueFormatter;

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
    public void setData(BarData barData) {
        this.mBarData = barData;
        this.mHighLightXRange = null;
        invalidate();
    }

    @Override
    public void setTodayLabel(String today) {
        this.todayTitle = today;
    }

    @Override
    public void setXValueFormatter(ValueFormatter vf) {
        if (vf != null)
            mValueFormatter = vf;
    }

    @Override
    public void setBackgroundGradient(@ColorInt int startColor, @ColorInt int endColor) {
        mBackgroundColor = 0;
        mBackgroundGradient = null;
        mBackgroundGradientColorStart = startColor;
        mBackgroundGradientColorEnd = endColor;
//        invalidate();
    }

    @Override
    public void setDottedLineColor(@ColorInt int dottedLineColor) {
        mDottedLineColor = dottedLineColor;
//        invalidate();
    }
}
