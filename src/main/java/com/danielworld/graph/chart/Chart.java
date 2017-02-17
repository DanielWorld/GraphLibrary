package com.danielworld.graph.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.danielworld.graph.ChartData;
import com.danielworld.graph.R;

import java.util.ArrayList;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-17.
 */

public abstract class Chart extends ViewGroup implements ChartData {

    private final String TAG = "BBGraph";

    private @ColorInt int mBackgroundColor = 0;
    protected int mXCount;

    protected ArrayList<String> xData;

    Paint mLinePaint = new Paint();
    Paint mCirclePaint = new Paint();
    Paint mXAxisTab = new Paint();



    private int mCanvasWidth;
    private int mCanvasHeight;

    public Chart(Context context) {
        this(context, null);
    }

    public Chart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        // Daniel (2017-02-17 18:00:20): Manage attributes
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Chart, defStyleAttr, 0);

        mBackgroundColor = typedArray.getColor(R.styleable.Chart_BackgroundColor,
                getResources().getColor(android.R.color.transparent));      // Background color

        mXCount = typedArray.getInteger(R.styleable.Chart_xCount, 1);       // X-coordinate count

    }

    Rect r = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        mCanvasWidth = canvas.getWidth();
        mCanvasHeight = canvas.getHeight();

        Log.i(TAG, "Canvas size : " + mCanvasWidth + " / " + mCanvasHeight);

        // Daniel (2017-02-17 17:41:11): draw background color
        canvas.drawColor(mBackgroundColor);

        mXAxisTab.setColor(Color.WHITE);
        mXAxisTab.setTextSize(50.0f);
        mXAxisTab.setTypeface(Typeface.DEFAULT_BOLD);
        mXAxisTab.setTextAlign(Paint.Align.CENTER);

        if (xData != null && !xData.isEmpty()) {
            int width = (mCanvasWidth / xData.size());
            Log.i(TAG, "width / " + xData.size() + "  = " + width);
            int x = width / 3;

            for (String xTab : xData) {
//                mXAxisTab.getTextBounds(xTab, 0, xTab.length(), r);

                canvas.drawText(xTab, x, mCanvasHeight - 100, mXAxisTab);
                x += width;
            }
        }

        // Daniel (2017-02-17 18:45:14): draw circle
        mCirclePaint.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setStrokeWidth(5.0f);

        canvas.drawCircle(150, 432, 15.0f, mCirclePaint);

        canvas.drawCircle(300, 221, 15.0f, mCirclePaint);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
