package com.danielworld.graph.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.danielworld.graph.ChartData;
import com.danielworld.graph.R;
import com.danielworld.graph.model.BarData;
import com.danielworld.graph.model.BarDataSet;
import com.danielworld.graph.model.BarEntry;
import com.danielworld.graph.model.Range;
import com.danielworld.graph.model.ValueFormatter;
import com.danielworld.graph.util.ChartDateUtil;

import java.util.List;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-17.
 */

public abstract class Chart extends ViewGroup implements ChartData {

    private final String TAG = "GraphLibrary";

    protected @ColorInt int mBackgroundColor = 0;
    protected @ColorInt int mBackgroundGradientColorStart = 0;
    protected @ColorInt int mBackgroundGradientColorEnd = 0;
    protected @ColorInt int mDottedLineColor = 0;
    private int mCircleRadius;
    private int mInnerCircleRadius;
    private int mEntryLineWidth;
    private int mTopPadding;
    private int mLeftPadding;
    private int mRightPadding;
    private int mBottomPadding;
    private int mLabelPaddingBottom;
    private int mLabelTextSize;
    private int mHighLightTextSize;
    private int mHighLightBackgroundRadius;
    private int mHighLightTopPadding;
    private int mHighLightBottomPadding;

    protected BarData mBarData;

    protected String todayTitle = "Today";

    Rect mCanvasSize = new Rect();          // real Canvas size
    RectF mGraphSize = new RectF();         // graph size

    // Daniel (2017-02-20 14:51:47): background
    Paint mBackgroundPaint = new Paint();
    LinearGradient mBackgroundGradient;

    // Daniel (2017-02-18 17:22:13): dotted line
    Paint mDottedLine = new Paint();
    Path mDottedLInePath = new Path();
    DashPathEffect mDottedLinePathEffect = new DashPathEffect(new float[]{5, 30}, 0);

    // line between entries
    Paint mEntryLine = new Paint();

    // circle
    Paint mCirclePaint = new Paint();

    // Axis x label
    Paint mTextPaint = new Paint();
    Paint mTextBackgroundPaint = new Paint();

    public Chart(Context context) {
        this(context, null);
    }

    public Chart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Daniel (2016-07-15 18:09:16): below 4.0.4 there is issue with clip mRectanglePath java.lang.UnsupportedOperationException
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        setWillNotDraw(false);

        // Daniel (2017-02-17 18:00:20): Manage attributes
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Chart, defStyleAttr, 0);

        if (typedArray.hasValue(R.styleable.Chart_backgroundColor)) {
            mBackgroundColor = typedArray.getColor(R.styleable.Chart_backgroundColor,
                    getResources().getColor(android.R.color.transparent));      // Background color
        } else {
            mBackgroundGradientColorStart = typedArray.getColor(R.styleable.Chart_backgroundGradientColorStart,
                    getResources().getColor(android.R.color.transparent));
            mBackgroundGradientColorEnd = typedArray.getColor(R.styleable.Chart_backgroundGradientColorEnd,
                    getResources().getColor(android.R.color.transparent));
        }

        mDottedLineColor = typedArray.getColor(R.styleable.Chart_dottedLineColor,
                getResources().getColor(android.R.color.transparent));

        mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.Chart_circleRadius, 6);
        mInnerCircleRadius = typedArray.getDimensionPixelSize(R.styleable.Chart_innerCircleRadius, 3);
        mEntryLineWidth = typedArray.getDimensionPixelSize(R.styleable.Chart_entryLineWidth, 3);

        if (typedArray.hasValue(R.styleable.Chart_graphPadding)) {
            mLeftPadding = mRightPadding = mBottomPadding = mTopPadding
            = typedArray.getDimensionPixelSize(R.styleable.Chart_graphPadding, 0);
        } else {
            mTopPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_graphPaddingTop, 0);
            mLeftPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_graphPaddingLeft, 0);
            mRightPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_graphPaddingRight, 0);
            mBottomPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_graphPaddingBottom, 0);
        }

        mLabelPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.Chart_graphLabelPaddingBottom, 0);

        mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.Chart_labelTextSize, 10);
        mHighLightTextSize = typedArray.getDimensionPixelSize(R.styleable.Chart_highLightTextSize, 9);
        mHighLightBackgroundRadius = typedArray.getDimensionPixelOffset(R.styleable.Chart_highLightBackgroundRadius, 6);

        mHighLightTopPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_highLightTopPadding, 0);
        mHighLightBottomPadding = typedArray.getDimensionPixelSize(R.styleable.Chart_highLightBottomPadding, 0);

        setOnTouchListener(mTouchListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        // get canvas real size
        canvas.getClipBounds(mCanvasSize);

        calcGraphSize();

        initDottedLine();

        // line paint between entries
        initEntryLine();

        // Entry circle
        initEntryCircle();

        // x label Paint
        initXLabel();

        // HighLight Paint
        initHighLight();

        // start at 0,0 and go to 0,max to use a vertical
        // gradient the full height of the screen.
        if (mBackgroundColor == 0) {
            // Daniel (2017-02-20 15:03:48): set Gradient background color
            if (mBackgroundGradient == null)
                mBackgroundGradient = new LinearGradient(0, 0, 0, mCanvasSize.height(), mBackgroundGradientColorStart, mBackgroundGradientColorEnd, Shader.TileMode.MIRROR);

            mBackgroundPaint.setShader(mBackgroundGradient);
            canvas.drawPaint(mBackgroundPaint);
        }
        else {
            // Daniel (2017-02-17 17:41:11): set background color
            canvas.drawColor(mBackgroundColor);
        }

        if (mBarData != null) {
            mBarData.setContainerSize(mGraphSize.width(), mGraphSize.height());

            // 0. if there is HighLight entry, make sure to draw highlight background first
            drawHighLightBackground(canvas);

            // 1. draw horizontal dotted lines
            drawDottedLines(canvas, (int) mBarData.getMaxY());


            for (BarDataSet barDataSet : mBarData.getBarDataList()) {

                // 2. draw lines between entries
                drawLinesBetweenEntry(canvas, barDataSet);

                // 2-1. draw big circle
                // 2-2. draw small circle
                drawCircleEntries(canvas, barDataSet);

                // 3. draw axis x label
                drawXAxisLabel(canvas, barDataSet);
            }

            // 4. draw entry which is HighLighted
            drawHighLightEntries(canvas, mBarData.getBarDataList());
        }
    }

    // set calculated graph size
    private void calcGraphSize() {
        mGraphSize.left = mLeftPadding;
        mGraphSize.top = mTopPadding;
        mGraphSize.bottom = mCanvasSize.height() - mBottomPadding;
        mGraphSize.right = mCanvasSize.width() - mRightPadding;
    }

    // set dotted line paint
    private void initDottedLine() {
        mDottedLine.setColor(mDottedLineColor);
        mDottedLine.setStyle(Paint.Style.STROKE);
        mDottedLine.setStrokeWidth(3);
        mDottedLine.setPathEffect(mDottedLinePathEffect);
    }

    // set entry line paint
    private void initEntryLine() {
        mEntryLine.setColor(Color.parseColor("#332983"));
        mEntryLine.setStyle(Paint.Style.STROKE);
        mEntryLine.setStrokeWidth(mEntryLineWidth);
    }

    // set circle entry paint
    private void initEntryCircle() {
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    // set x label paint
    private void initXLabel() {
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mLabelTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextBackgroundPaint.setColor(Color.WHITE);
        mTextBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    // set HighLight paint
    private void initHighLight() {
        mHighLightBackgroundPaint.setColor(Color.parseColor("#1Affffff"));
        mHighLightBackgroundPaint.setStyle(Paint.Style.FILL);

        mHighLightTextPaint.setTextSize(mHighLightTextSize);
        mHighLightTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mHighLightTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    // draw HighLight background
    private void drawHighLightBackground(Canvas canvas) {
        if (mHighLightXRange == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(
                    mGraphSize.left + mHighLightXRange.getFrom(),
                    mHighLightTopPadding,
                    mGraphSize.left + mHighLightXRange.getTo(),
                    mCanvasSize.height() - mHighLightBottomPadding,
                    mHighLightBackgroundRadius, mHighLightBackgroundRadius,
                    mHighLightBackgroundPaint);
        } else {
            canvas.drawRect(
                    mGraphSize.left + mHighLightXRange.getFrom(),
                    mHighLightTopPadding,
                    mGraphSize.left + mHighLightXRange.getTo(),
                    mCanvasSize.height() - mHighLightBottomPadding,
                    mHighLightBackgroundPaint);
        }
    }

    // draw horizontal dotted lines
    private void drawDottedLines(Canvas canvas, int maxY) {
        int dottedLineCount;
        if (maxY % 3 == 0) {
            // maxY is multiplied by 3 -> line's count is 4 (including 0)
            dottedLineCount = 4;
        } else if (maxY % 2 == 0) {
            // maxY is multiplied by 2 -> line's count is 3 (including 0)
            dottedLineCount = 3;
        } else {
            dottedLineCount = 2; // -> line's count is 2 (including 0 and max Y)
        }
        Log.e(TAG, "dottedLineCount = " + dottedLineCount);
        Log.d(TAG, "======================================");

        // divide height by ((dottedLineCount - 1)
        float dottedLineGap = mGraphSize.height() / (dottedLineCount - 1);
        float startX = mGraphSize.left, startY = mGraphSize.bottom,
                stopX = mGraphSize.right, stopY = mGraphSize.bottom;

        for (int i = 0; i < dottedLineCount; i++) {
            Log.i(TAG, "start x, y = " + startX + " | " + startY);
            Log.w(TAG, "stop x, y = " + stopX + " | " + stopY);
            Log.d(TAG, "========================================");
            mDottedLInePath.reset();
            mDottedLInePath.moveTo(startX, startY);
            mDottedLInePath.lineTo(stopX, stopY);
//            canvas.drawLine(startX, startY, stopX, stopY, mDottedLine);
            canvas.drawPath(mDottedLInePath, mDottedLine);
            startY -= dottedLineGap;
            stopY -= dottedLineGap;
        }
    }

    // draw line between entries
    private void drawLinesBetweenEntry(Canvas canvas, List<BarDataSet> barDataSetList) {
        for (BarDataSet barDataSet : barDataSetList) {
            mEntryLine.setColor(barDataSet.getBarColor());

            for (int i = 0; (i + 1) < barDataSet.getEntries().size(); i++) {
                Log.v(TAG, "drawLine x, y = " + (mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX())
                        + " | " + (mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate()));
                Log.d(TAG, "=================================================================");
                canvas.drawLine(
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                        mGraphSize.left + barDataSet.getEntries().get(i + 1).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i + 1).getEntryYCoordinate(),
                        mEntryLine
                );
            }
        }
    }

    // draw line between entries
    private void drawLinesBetweenEntry(Canvas canvas, BarDataSet barDataSet) {
        mEntryLine.setColor(barDataSet.getBarColor());

        for (int i = 0; (i + 1) < barDataSet.getEntries().size(); i++) {
            Log.v(TAG, "drawLine x, y = " + (mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX())
                    + " | " + (mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate()));
            Log.d(TAG, "=================================================================");
            canvas.drawLine(
                    mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                    mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                    mGraphSize.left + barDataSet.getEntries().get(i + 1).getEntryCenterX(),
                    mGraphSize.bottom - barDataSet.getEntries().get(i + 1).getEntryYCoordinate(),
                    mEntryLine
            );
        }
    }

    // draw circle entry
    private void drawCircleEntries(Canvas canvas, BarDataSet barDataSet) {
        mCirclePaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < barDataSet.getEntries().size(); i++) {

            if (mHighLightXRange != null &&
                    barDataSet.getEntries().get(i).getEntryXRange() != null &&
                    barDataSet.getEntries().get(i).getEntryXRange().contains(mHighLightXRange.getTo())) {
                    // Daniel (2017-02-20 16:46:06): if this entry is selected, then do nothing!
            } else {
                // 1. draw big circle entry
                mCirclePaint.setColor(barDataSet.getBarColor());
                canvas.drawCircle(
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                        mCircleRadius,
                        mCirclePaint
                );

                // 2. draw small circle entry
                mCirclePaint.setColor(Color.WHITE);
                canvas.drawCircle(
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                        mInnerCircleRadius,
                        mCirclePaint
                );
            }
        }
    }

    Rect textBounds = new Rect();

    // draw x axis label
    private void drawXAxisLabel(Canvas canvas, BarDataSet barDataSet) {
        for (int i = 0; i < barDataSet.getEntries().size(); i++) {
            if (ChartDateUtil.getDate(System.currentTimeMillis(), "M/d")
                    .equals(mValueFormatter.getValueFormatter(barDataSet.getEntries().get(i).getX()))) {

                mTextPaint.setColor(Color.BLACK);
                mTextPaint.getTextBounds(todayTitle, 0, todayTitle.length(), textBounds);

//                Log.w(TAG, "text bounds width = " + textBounds.width());
//                Log.v(TAG, "text bounds height = " + textBounds.height());
//                Log.d(TAG, "================================================");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() - (textBounds.width() / 1.1f),
                            mCanvasSize.height() - mLabelPaddingBottom,
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() + (textBounds.width() / 1.1f),
                            mCanvasSize.height() - mLabelPaddingBottom + (textBounds.height()),
                            textBounds.height(), textBounds.height(),
                            mTextBackgroundPaint);
                } else {
                    canvas.drawRect(
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() - (textBounds.width() / 1.1f),
                            mCanvasSize.height() - mLabelPaddingBottom,
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() + (textBounds.width() / 1.1f),
                            mCanvasSize.height() - mLabelPaddingBottom + (textBounds.height()),
                            mTextBackgroundPaint);
                }

                canvas.drawText(
                        todayTitle,
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mCanvasSize.height() - mLabelPaddingBottom,
                        mTextPaint);

            } else {
                mTextPaint.setColor(Color.WHITE);

                canvas.drawText(
                        mValueFormatter.getValueFormatter(barDataSet.getEntries().get(i).getX()),
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mCanvasSize.height() - mLabelPaddingBottom,
                        mTextPaint);
            }
        }
    }

    Rect highLightTextBounds = new Rect();
    // draw HighLight Entry
    private void drawHighLightEntries(Canvas canvas, List<BarDataSet> barDataSetList) {
        // Daniel (2017-02-20 15:58:51): This is useful logic! Don't remove it!
//        for (BarDataSet barDataSet : barDataSetList) {
//            for (int i = 0; i < barDataSet.getEntries().size(); i++) {
//
//                if (mHighLightXRange != null &&
//                        barDataSet.getEntries().get(i).getEntryXRange() != null &&
//                        barDataSet.getEntries().get(i).getEntryXRange().contains(mHighLightXRange.getTo())) {

//                    mCirclePaint.setColor(Color.WHITE);
//                    canvas.drawCircle(
//                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
//                            mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
//                            mCircleRadius,
//                            mCirclePaint
//                    );
//
//                    String valueY = String.valueOf((int) barDataSet.getEntries().get(i).getY());
//                    mHighLightTextPaint.setColor(barDataSet.getBarColor());
//                    mHighLightTextPaint.getTextBounds(valueY, 0, valueY.length(), highLightTextBounds);
//
//                    canvas.drawText(
//                            valueY,
//                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
//                            mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate() + highLightTextBounds.height() / 2,
//                            mHighLightTextPaint
//                    );
//                }
//            }
//        }

        for (int barDataSetIndex = 0; barDataSetIndex < barDataSetList.size(); barDataSetIndex++) {
            for (int i = 0; i < barDataSetList.get(barDataSetIndex).getEntries().size(); i++) {

                if (mHighLightXRange != null &&
                        barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryXRange() != null &&
                        barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryXRange().contains(mHighLightXRange.getTo())) {

                    // Daniel (2017-02-20 16:00:22):
                    // This only works when BarDataSet size is equals to 2
                    if (barDataSetList.size() == 2) {
                        BarEntry firstEntry = barDataSetList.get(0).getEntries().get(i);
                        BarEntry secondEntry = barDataSetList.get(1).getEntries().get(i);

                        float differenceGap = Math.abs(firstEntry.getEntryYCoordinate() - secondEntry.getEntryYCoordinate());

                        // two of Entries value are not equal and their difference is only smaller than mCircleRadius * 1.5f
                        if (differenceGap <= mCircleRadius * 1.5f && differenceGap != 0) {
                            // if first Entry Y coordinates is bigger than second Entry Y coordinates
                            if (firstEntry.getEntryYCoordinate() > secondEntry.getEntryYCoordinate()) {
                                // down first Entry Y coordinates by mCircleRadius
                                firstEntry.setEntryYCoordinate(firstEntry.getEntryYCoordinate() + mCircleRadius);
                                // up second Entry Y coordinates by mCircleRadius
                                secondEntry.setEntryYCoordinate(secondEntry.getEntryYCoordinate() - mCircleRadius);
                            } else {
                                // down second Entry Y coordinates by mCircleRadius
                                secondEntry.setEntryYCoordinate(secondEntry.getEntryYCoordinate() + mCircleRadius);
                                // up first Entry Y coordinates by mCircleRadius
                                firstEntry.setEntryYCoordinate(firstEntry.getEntryYCoordinate() - mCircleRadius);
                            }

                        }
                    }

                    // 1. draw big HighLight circle entry
                    mCirclePaint.setColor(Color.WHITE);
                    canvas.drawCircle(
                            mGraphSize.left + barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryCenterX(),
                            mGraphSize.bottom - barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryYCoordinate(),
                            mCircleRadius,
                            mCirclePaint
                    );

                    // 2. draw HighLight text
                    String valueY = String.valueOf((int) barDataSetList.get(barDataSetIndex).getEntries().get(i).getY());
                    mHighLightTextPaint.setColor(barDataSetList.get(barDataSetIndex).getBarColor());
                    mHighLightTextPaint.getTextBounds(valueY, 0, valueY.length(), highLightTextBounds);

                    canvas.drawText(
                            valueY,
                            mGraphSize.left + barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryCenterX(),
                            mGraphSize.bottom - barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryYCoordinate() + highLightTextBounds.height() / 2,
                            mHighLightTextPaint
                    );

                    // Daniel (2017-02-21 11:17:03): In logic, there is only one HighLight, so break;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    // Daniel (2017-02-20 11:58:44): Current HighLighted x coordinates range
    protected Range mHighLightXRange;
    // HighLight background paint
    Paint mHighLightBackgroundPaint = new Paint();
    // HighLight text paint
    Paint mHighLightTextPaint = new Paint();

    OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    float X = event.getX();
                    float Y = event.getY();

                    Log.i(TAG, "ACTION_DOWN = " + X + " | " + Y);
//                    if (mGraphSize != null) X += mGraphSize.left;
                    if (mBarData != null && mBarData.hasBarDataList()) {
                        BarDataSet barDataSet = mBarData.getBarDataList().get(0);
                        if (barDataSet.hasEntries()) {
                            for (BarEntry barEntry : barDataSet.getEntries()) {
                                // Daniel (2017-02-20 11:57:04):
                                // TODO:
                                if (barEntry.getEntryXRange() != null && mGraphSize != null &&
                                        barEntry.getEntryXRange().contains(X - mGraphSize.left)) {
                                    if (mHighLightXRange != null && mHighLightXRange.getFrom() == barEntry.getEntryXRange().getFrom()) {
                                        mHighLightXRange = null;
                                        Log.d(TAG, "current unSelected X column range = " + barEntry.getEntryXRange().toString());
                                    } else {
                                        mHighLightXRange = barEntry.getEntryXRange();
                                        Log.v(TAG, "current selected X column range = " + barEntry.getEntryXRange().toString());
                                    }
                                    invalidate();
                                    break;
                                }
                            }
                        }
                    }

                    break;
            }
            return false;
        }
    };

    protected ValueFormatter mValueFormatter = new ValueFormatter() {
        @Override
        public String getValueFormatter(int x) {
            return String.valueOf(x);
        }
    };
}
