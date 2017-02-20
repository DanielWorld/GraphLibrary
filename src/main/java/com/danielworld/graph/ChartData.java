package com.danielworld.graph;

import android.support.annotation.ColorInt;

import com.danielworld.graph.model.BarData;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-17.
 */

public interface ChartData {

    void setData(BarData barData);

    void setBackgroundGradient(@ColorInt int startColor, @ColorInt int endColor);

    void setDottedLineColor(@ColorInt int dottedLineColor);
}
