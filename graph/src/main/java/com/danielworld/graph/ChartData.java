package com.danielworld.graph;

import android.support.annotation.ColorInt;

import com.danielworld.graph.model.BarData;
import com.danielworld.graph.model.ValueFormatter;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-17.
 */

public interface ChartData {

    /**
     * set data
     * @param barData
     */
    void setData(BarData barData);

    /**
     * set today's label
     * @param today
     */
    void setTodayLabel(String today);

    /**
     * set x value formatter <br>
     *     <p>you can define custom x label to represent axis x as you want</p>
     * @param vf
     */
    void setXValueFormatter(ValueFormatter vf);

    /**
     * set background color with gradient
     */
    void setBackgroundGradient(@ColorInt int startColor, @ColorInt int endColor);

    /**
     * set horizontal dotted line color
     * @param dottedLineColor
     */
    void setDottedLineColor(@ColorInt int dottedLineColor);
}
