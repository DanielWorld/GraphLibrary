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
    private int mEntryLineWidth;
    private int mTopPadding;
    private int mLeftPadding;
    private int mRightPadding;
    private int mBottomPadding;
    private int mLabelTextSize;
    private int mHighLightTextSize;
    private int mHighLightBackgroundRadius;

    protected BarData mBarData;

    Rect mCanvasSize = new Rect();          // 실제 Canvas 사이즈
    RectF mGraphSize = new RectF();         // Canvas 에서 padding 을 더해서 나온 Graph 사이즈

    // Daniel (2017-02-20 14:51:47): 배경 관련
    Paint mBackgroundPaint = new Paint();
    LinearGradient mBackgroundGradient;

    // Daniel (2017-02-18 17:22:13): 점선 관련
    Paint mDottedLine = new Paint();
    Path mDottedLInePath = new Path();
    DashPathEffect mDottedLinePathEffect = new DashPathEffect(new float[]{5, 30}, 0);

    // 원과 원사이 라인
    Paint mEntryLine = new Paint();

    // 원
    Paint mCirclePaint = new Paint();

    // X축 라벨
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

        mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.Chart_labelTextSize, 10);
        mHighLightTextSize = typedArray.getDimensionPixelSize(R.styleable.Chart_highLightTextSize, 9);
        mHighLightBackgroundRadius = typedArray.getDimensionPixelOffset(R.styleable.Chart_highLightBackgroundRadius, 6);

        setOnTouchListener(mTouchListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        // 캔버스 크기 구하기
        canvas.getClipBounds(mCanvasSize);

        // 실제 그래프 사이즈 구하기
        calcGraphSize();

        // 평행 점선 Paint 설정
        initDottedLine();

        // Entry 사이 line Paint 설정
        initEntryLine();

        // Entry 원형 설정
        initEntryCircle();

        // x 라벨 Paint 설정
        initXLabel();

        // HighLight Paint 설정
        initHighLight();

        // start at 0,0 and go to 0,max to use a vertical
        // gradient the full height of the screen.
        if (mBackgroundColor == 0) {
            // Daniel (2017-02-20 15:03:48): Gradient 배경색 설정
            if (mBackgroundGradient == null)
                mBackgroundGradient = new LinearGradient(0, 0, 0, mCanvasSize.height(), mBackgroundGradientColorStart, mBackgroundGradientColorEnd, Shader.TileMode.MIRROR);

            mBackgroundPaint.setShader(mBackgroundGradient);
            canvas.drawPaint(mBackgroundPaint);
        }
        else {
            // Daniel (2017-02-17 17:41:11): 배경색 설정
            canvas.drawColor(mBackgroundColor);
        }

        if (mBarData != null) {
            mBarData.setContainerSize(mGraphSize.width(), mGraphSize.height());

            // 0. HighLight 존재할 경우 배경 부분 그려줘야 함
            drawHighLightBackground(canvas);

            // 1. 해당 Max y값과 Min y(== 0) 을 기준으로 가로로 점선 긋기 (먼저 완료 필수)
            drawDottedLines(canvas, (int) mBarData.getMaxY());

            // 2. 원형과 원형사이 선 그리기
//            drawLinesBetweenEntry(canvas, mBarData.getBarDataList());

            for (BarDataSet barDataSet : mBarData.getBarDataList()) {

                // 2. 원형과 원형사이 선 그리기
                drawLinesBetweenEntry(canvas, barDataSet);

                // 2-1. 큰 원형 그리기
                // 2-2. 작은 원형 그리기
                drawCircleEntries(canvas, barDataSet);

                // 3. x축 라벨 그리기
                drawXAxisLabel(canvas, barDataSet);
            }

            // 4. HighLight 된 그리기
            drawHighLightEntries(canvas, mBarData.getBarDataList());
        }
    }

    // 실제 그래프 사이즈 설정
    private void calcGraphSize() {
        mGraphSize.left = mLeftPadding;
        mGraphSize.top = mTopPadding;
        mGraphSize.bottom = mCanvasSize.height() - mBottomPadding;
        mGraphSize.right = mCanvasSize.width() - mRightPadding;
    }

    // 점선 설정
    private void initDottedLine() {
        mDottedLine.setColor(mDottedLineColor);
        mDottedLine.setStyle(Paint.Style.STROKE);
        mDottedLine.setStrokeWidth(3);
        mDottedLine.setPathEffect(mDottedLinePathEffect);
    }

    // Entry 사이의 Line 설정
    private void initEntryLine() {
        mEntryLine.setColor(Color.parseColor("#332983"));
        mEntryLine.setStyle(Paint.Style.STROKE);
        mEntryLine.setStrokeWidth(mEntryLineWidth);
    }

    // Entry 원형 설정
    private void initEntryCircle() {
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    // x 값 라벨 설정
    private void initXLabel() {
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mLabelTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextBackgroundPaint.setColor(Color.WHITE);
        mTextBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    // HighLight 설정
    private void initHighLight() {
        mHighLightBackgroundPaint.setColor(Color.parseColor("#1Affffff"));
        mHighLightBackgroundPaint.setStyle(Paint.Style.FILL);

        mHighLightTextPaint.setTextSize(mHighLightTextSize);
        mHighLightTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mHighLightTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    // HighLight 부분 배경 그리기
    private void drawHighLightBackground(Canvas canvas) {
        if (mHighLightXRange == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(
                    mGraphSize.left + mHighLightXRange.getFrom(),
                    0,
                    mGraphSize.left + mHighLightXRange.getTo(),
                    mCanvasSize.height(),
                    mHighLightBackgroundRadius, mHighLightBackgroundRadius,
                    mHighLightBackgroundPaint);
        } else {
            canvas.drawRect(
                    mGraphSize.left + mHighLightXRange.getFrom(),
                    0,
                    mGraphSize.left + mHighLightXRange.getTo(),
                    mCanvasSize.height(),
                    mHighLightBackgroundPaint);
        }
    }

    // 평행 점선 그리기
    private void drawDottedLines(Canvas canvas, int maxY) {
        int dottedLineCount;
//        if (maxY % 4 == 0) {
//            // maxY 는 4의 배수 -> 0을 포함한 라인 총 5개
//            dottedLineCount = 5;
//        } else
        if (maxY % 3 == 0) {
            // maxY 는 3의 배수 -> 0을 포함한 라인 총 4개
            dottedLineCount = 4;
        } else if (maxY % 2 == 0) {
            // maxY 는 2의 배수 -> 0을 포함한 라인 총 3개
            dottedLineCount = 3;
        } else {
            dottedLineCount = 2; // -> 0과 최대값만 그림 총 2개
        }
        Log.e(TAG, "dottedLineCount = " + dottedLineCount);
        Log.d(TAG, "======================================");

        // 실제 height 에서 (dottedLineCount - 1) 만큼 나눔
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

    // Entry 사이에 라인 그리기
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

    // Entry 사이에 라인 그리기
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

    // 원형 Entry 그리기
    private void drawCircleEntries(Canvas canvas, BarDataSet barDataSet) {
        mCirclePaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < barDataSet.getEntries().size(); i++) {

            if (mHighLightXRange != null &&
                    barDataSet.getEntries().get(i).getEntryXRange() != null &&
                    barDataSet.getEntries().get(i).getEntryXRange().contains(mHighLightXRange.getTo())) {
                    // Daniel (2017-02-20 16:46:06): if this entry is selected, then do nothing!
            } else {
                // 1. 큰 원형 먼저 그리기
                mCirclePaint.setColor(barDataSet.getBarColor());
                canvas.drawCircle(
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                        mCircleRadius,
                        mCirclePaint
                );

                // 2. 작은 원형 그리기
                mCirclePaint.setColor(Color.WHITE);
                canvas.drawCircle(
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
                        mCircleRadius / 2,
                        mCirclePaint
                );
            }
        }
    }

    Rect textBounds = new Rect();
    // x축 라벨 그리기
    private void drawXAxisLabel(Canvas canvas, BarDataSet barDataSet) {
        for (int i = 0; i < barDataSet.getEntries().size(); i++) {
            if (ChartDateUtil.getDate(System.currentTimeMillis(), "M/d")
                    .equals(barDataSet.getEntries().get(i).getX())) {
                String newTitle = "오늘";

                mTextPaint.setColor(Color.BLACK);
                mTextPaint.getTextBounds(newTitle, 0, newTitle.length(), textBounds);

//                Log.w(TAG, "text bounds width = " + textBounds.width());
//                Log.v(TAG, "text bounds height = " + textBounds.height());
//                Log.d(TAG, "================================================");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() - (textBounds.width() / 1.1f),
                            mCanvasSize.height() - (mBottomPadding / 3) - (textBounds.height() / 0.7f),
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() + (textBounds.width() / 1.1f),
                            mCanvasSize.height() - (mBottomPadding / 3) + (textBounds.height() / 1.5f),
                            textBounds.height(), textBounds.height(),
                            mTextBackgroundPaint);
                } else {
                    canvas.drawRect(
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() - (textBounds.width() / 1.1f),
                            mCanvasSize.height() - (mBottomPadding / 3) - (textBounds.height() / 0.7f),
                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX() + (textBounds.width() / 1.1f),
                            mCanvasSize.height() - (mBottomPadding / 3) + (textBounds.height() / 1.5f),
                            mTextBackgroundPaint);
                }

                canvas.drawText(
                        newTitle,
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mCanvasSize.height() - (mBottomPadding / 3),
                        mTextPaint);

            } else {
                mTextPaint.setColor(Color.WHITE);

                canvas.drawText(
                        barDataSet.getEntries().get(i).getX(),
                        mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
                        mCanvasSize.height() - (mBottomPadding / 3),
                        mTextPaint);
            }
        }
    }

    Rect highLightTextBounds = new Rect();
    // HighLight Entry 그리기
    private void drawHighLightEntries(Canvas canvas, List<BarDataSet> barDataSetList) {
        // Daniel (2017-02-20 15:58:51): This is useful logic! Don't remove it!
//        for (BarDataSet barDataSet : barDataSetList) {
//            for (int i = 0; i < barDataSet.getEntries().size(); i++) {
//
//                if (mHighLightXRange != null &&
//                        barDataSet.getEntries().get(i).getEntryXRange() != null &&
//                        barDataSet.getEntries().get(i).getEntryXRange().contains(mHighLightXRange.getTo())) {
//                    // 1. 큰 원형 먼저 그리기
//                    mCirclePaint.setColor(Color.WHITE);
//                    canvas.drawCircle(
//                            mGraphSize.left + barDataSet.getEntries().get(i).getEntryCenterX(),
//                            mGraphSize.bottom - barDataSet.getEntries().get(i).getEntryYCoordinate(),
//                            mCircleRadius,
//                            mCirclePaint
//                    );
//
//                    // 2. HighLight text 그리기
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
                    // TODO: 현재는 무조건 BarDataSet 이 2개라는 가정하에 진행...
                    // TODO: 만약 그 이상일 경우 답없음
                    if (barDataSetList.size() == 2) {
                        BarEntry firstEntry = barDataSetList.get(0).getEntries().get(i);
                        BarEntry secondEntry = barDataSetList.get(1).getEntries().get(i);

                        float differenceGap = Math.abs(firstEntry.getEntryYCoordinate() - secondEntry.getEntryYCoordinate());

                        // 2개의 Entry 값이 동일하지는 않지만 반지름 이하의 좌표 차이 밖에 안남
                        if (differenceGap <= mCircleRadius * 1.5f && differenceGap != 0) {
                            // 1번째 Entry 가 2번째 Entry 보다 밑에 있을 경우
                            if (firstEntry.getEntryYCoordinate() > secondEntry.getEntryYCoordinate()) {
                                // 1번째 Entry 를 밑으로 내림
                                firstEntry.setEntryYCoordinate(firstEntry.getEntryYCoordinate() + mCircleRadius);
                                // 2번째 Entry 를 위로 올림
                                secondEntry.setEntryYCoordinate(secondEntry.getEntryYCoordinate() - mCircleRadius);
                            } else {
                                // 2번째 Entry 를 밑으로 내림
                                secondEntry.setEntryYCoordinate(secondEntry.getEntryYCoordinate() + mCircleRadius);
                                // 1번째 Entry 를 위로 올림
                                firstEntry.setEntryYCoordinate(firstEntry.getEntryYCoordinate() - mCircleRadius);
                            }

                        }
                    }

                    // 1. 큰 원형 먼저 그리기
                    mCirclePaint.setColor(Color.WHITE);
                    canvas.drawCircle(
                            mGraphSize.left + barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryCenterX(),
                            mGraphSize.bottom - barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryYCoordinate(),
                            mCircleRadius,
                            mCirclePaint
                    );

                    // 2. HighLight text 그리기
                    String valueY = String.valueOf((int) barDataSetList.get(barDataSetIndex).getEntries().get(i).getY());
                    mHighLightTextPaint.setColor(barDataSetList.get(barDataSetIndex).getBarColor());
                    mHighLightTextPaint.getTextBounds(valueY, 0, valueY.length(), highLightTextBounds);

                    canvas.drawText(
                            valueY,
                            mGraphSize.left + barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryCenterX(),
                            mGraphSize.bottom - barDataSetList.get(barDataSetIndex).getEntries().get(i).getEntryYCoordinate() + highLightTextBounds.height() / 2,
                            mHighLightTextPaint
                    );

                    // Daniel (2017-02-20 16:02:11): 현재 로직에서 HighLight 는 무조건 1번이므로 break 처리한다.
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    // Daniel (2017-02-20 11:58:44): 현재 HighLight 된 x 좌표 Range
    protected Range mHighLightXRange;
    // HighLight 배경
    Paint mHighLightBackgroundPaint = new Paint();
    // HighLight 텍스트
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
                                        Log.d(TAG, "현재 unSelected 된 X column range = " + barEntry.getEntryXRange().toString());
                                    } else {
                                        mHighLightXRange = barEntry.getEntryXRange();
                                        Log.v(TAG, "현재 selected 된 X column range = " + barEntry.getEntryXRange().toString());
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
}
