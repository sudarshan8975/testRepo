package com.example.list.androidchart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class PichartDemo extends Activity {

    TextView txt_heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pichartdemo);
        /*txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_heading.setText("Loans");*/
        PieChart pieChart = findViewById(R.id.pichart);
        pieChart.setUsePercentValues(true);

        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
        yvalues.add(new PieEntry(100f, "Standered ", 0));
        yvalues.add(new PieEntry(20f, "Sub Standered", 1));
        yvalues.add(new PieEntry(10f, "DA 1", 2));
        yvalues.add(new PieEntry(8f, "DA 2", 3));
        yvalues.add(new PieEntry(3f, "DA 3", 4));
        yvalues.add(new PieEntry(1f, "Suite Filed", 5));

        PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.loanlbl));
        PieData data = new PieData(dataSet);

        data.setValueFormatter(new PercentFormatter());
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
        l.setYOffset(6f);
        Description description = new Description();
        description.setText(getString(R.string.pie_chart));
        pieChart.setDescription(description);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setHoleRadius(60f);
        //dataSet.setColors(ColorTemplate.MATERIAL_COLORS);//MATERIAL_COLORS);VORDIPLOM_COLORS
      /*  Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)*/
        ArrayList<Integer> colors=new ArrayList<>();
       /* colors.add(Color.rgb(193, 37, 82));
        colors.add(Color.rgb(255, 102, 0));
        colors.add(Color.rgb(245, 199, 0));
        colors.add(Color.rgb(106, 150, 31));
        colors.add(Color.rgb(179, 100, 53));
        colors.add(Color.rgb(192, 255, 140));
        colors.add( Color.rgb(255, 247, 140));*/
        colors.add(Color.rgb( 46, 204, 113));
        colors.add(Color.rgb(241, 196, 15));
        colors.add(Color.rgb(231, 76, 60));
        colors.add(Color.rgb(52, 152, 219));
        colors.add(Color.rgb(179, 100, 53));
        colors.add(Color.rgb(255, 140, 157));
        colors.add(Color.rgb(192, 255, 140));

        dataSet.setColors(colors);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        int colorBlack = Color.parseColor("#000000");
        pieChart.setEntryLabelColor(colorBlack);
        pieChart.setEntryLabelTextSize(10f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieChart.setData(data);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(PichartDemo.this, Dashboard.class);
        startActivity(in);
        finish();
    }
}

