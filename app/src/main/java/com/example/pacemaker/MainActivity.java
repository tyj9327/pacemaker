package com.example.pacemaker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView titleToolbar;
    private LineChart lineChart;
    private HorizontalBarChart horizontalBarChart;
    private FrameLayout barchart_top;
    private FrameLayout barchart_bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        settingToolbar();
        settingLineChart();

        barchart_top.setLayoutParams(new FrameLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        titleToolbar = findViewById(R.id.title_toolbar);
        lineChart = findViewById(R.id.line_chart);
        barchart_bottom = findViewById(R.id.barchart_back);
        barchart_top = findViewById(R.id.barchart_top);

    }

    private void settingToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.home_white);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.background_start));
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.menubar));

    }

    private void settingLineChart() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 5));
        entries.add(new Entry(4, 7));
        entries.add(new Entry(5, 10));
        entries.add(new Entry(6, 13));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성값");
        lineDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        lineDataSet.setCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        lineDataSet.setCircleColorHole(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        LineData lineData = new LineData(lineDataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("Description");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);

        lineChart.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private void settingHorizontalBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        BarEntry entry = new BarEntry(1, 1.7f);
        entries.add(1, entry);

        BarDataSet barDataSet = new BarDataSet(entries, "label");
        barDataSet.setBarShadowColor(ContextCompat.getColor(getApplicationContext(), R.color.background_end));

        BarData barData = new BarData(barDataSet);



//        BarChart.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
