package bapul.graphexample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.danielworld.graph.chart.LineChart;
import com.danielworld.graph.model.BarData;
import com.danielworld.graph.model.BarDataSet;
import com.danielworld.graph.model.BarEntry;
import com.danielworld.graph.model.ValueFormatter;
import com.danielworld.graph.util.ChartDateUtil;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;
    EditText maxEditText, gradientStart, gradientEnd, dottedLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String a = ChartDateUtil.getDate(ChartDateUtil.getMillisFromDate(2017, 2,18), "M/d");

        lineChart = (LineChart) findViewById(R.id.GraphChart);

        maxEditText = (EditText) findViewById(R.id.maxEditText);
        gradientStart = (EditText) findViewById(R.id.gradientStart);
        gradientEnd = (EditText) findViewById(R.id.gradientEnd);
        dottedLine = (EditText) findViewById(R.id.dottedLine);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int max;
                try {
                     max = Integer.parseInt(maxEditText.getText().toString());
                     max = (max == 0) ? 1 : max;
                } catch (Exception e){
                    max = 150;
                }

                ArrayList<BarEntry> entries1 = new ArrayList<>();
                entries1.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()), random.nextInt(max)));
                entries1.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) - 3, random.nextInt(max)));
                entries1.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) - 4, random.nextInt(max)));

                ArrayList<BarEntry> entries2 = new ArrayList<>();
                entries2.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) - 1, random.nextInt(max)));
                entries2.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) + 1, random.nextInt(max)));
                entries2.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) + 2, random.nextInt(max)));
                entries2.add(new BarEntry(ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) + 3, random.nextInt(max)));

                BarDataSet barDataSet1 = new BarDataSet(
                        entries1,
                        Color.parseColor("#adfcff"),
                        ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) - 4,
                        ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) + 3);

                BarDataSet barDataSet2 = new BarDataSet(
                        entries2,
                        Color.parseColor("#ceffca"),
                        ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) - 4,
                        ChartDateUtil.getDanielDayIndex(System.currentTimeMillis()) + 3);

                BarData barData = new BarData(barDataSet1, barDataSet2);

                lineChart.setBackgroundGradient(
                        Color.parseColor("#"+gradientStart.getText().toString()),
                        Color.parseColor("#"+gradientEnd.getText().toString())
                );

                lineChart.setDottedLineColor(
                        Color.parseColor("#"+dottedLine.getText().toString())
                );

                lineChart.setTodayLabel("오늘");
                lineChart.setXValueFormatter(new ValueFormatter() {
                    @Override
                    public String getValueFormatter(int x) {
                        return ChartDateUtil.getDate(ChartDateUtil.getTimeFromDanielDayIndex(x), "M/d");
                    }
                });

                lineChart.setData(barData);
            }
        });
    }
}
