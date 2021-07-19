package com.example.list.androidchart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class LinechartTwo extends Activity {

    LineChart linechart,linechart1;
    TextView txt_heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linecharttwo);


        linechart = findViewById(R.id.linechart);
        /*txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_heading.setText("Advances Details");*/
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 8000000));
        entries.add(new Entry(1, 8300000));
        //entries.add(new Entry(2, 2));
        //entries.add(new Entry(3, 4));

        LineDataSet dataSet = new LineDataSet(entries, "Advances");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //****
        // Controlling X axis
        XAxis xAxis = linechart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        final String[] months = new String[]{"Yesterday", "Today"};//, "Mar", "Apr"

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        //***
        // Controlling right side of y axis
        YAxis yAxisRight = linechart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = linechart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        linechart.setData(data);
        linechart.animateX(2500);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(LinechartTwo.this, MainActivity.class);
        startActivity(in);
        finish();
    }
}
