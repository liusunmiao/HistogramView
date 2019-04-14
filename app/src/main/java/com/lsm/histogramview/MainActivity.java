package com.lsm.histogramview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HistogramView histogramView;
    private List<Integer> values;
    private List<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        histogramView = findViewById(R.id.histogram_view);
        values = new ArrayList<>();
        colors = new ArrayList<>();
        values.add(16);
        values.add(25);
        values.add(44);
        values.add(11);
        values.add(22);
        values.add(17);
        values.add(35);

        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.GREEN);
        colors.add(Color.GRAY);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.LTGRAY);

        histogramView.setColumnInfo(values, colors, 7);
    }
}
