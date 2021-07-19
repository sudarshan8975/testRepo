package com.example.list.androidchart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;

public class SpeedMeter extends Activity {
TextView txt_heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedmeter);

        SpeedView speedView = (SpeedView) findViewById(R.id.speedView);
        /*txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_heading.setText("NPA");*/
// change MAX speed to 320
        speedView.setMaxSpeed(20);
        speedView.setTickNumber(10);
        speedView.speedTo(5);
        speedView.setWithTremble(false);
        speedView.setUnit("% NPA");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(SpeedMeter.this, MainActivity.class);
        startActivity(in);
        finish();
    }
    }
