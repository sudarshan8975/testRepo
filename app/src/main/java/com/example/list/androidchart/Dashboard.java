package com.example.list.androidchart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.list.androidchart.uitily.CallSoapWebService;
import com.example.list.androidchart.uitily.EncryptionModel;
import com.example.list.androidchart.uitily.UitilyInstance;
import com.example.list.androidchart.uitily.UserDao;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.spec.SecretKeySpec;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{
    private  static String TAG="MainActivity";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String retjson = "";
    private static final String METHOD_NAME = "getDashboardData";
    Button loan,deposite,advance;
    String retVal="";
    int cnt=0;
    TextView txt_heading;
    SpeedView speedView,speedViewNPA;
    LineChart linechart,linechart1;
    CardView card_view1,card_view2,card_view3,card_view4;
    PieChart pieChart;
    String usrcode="";
    PrivateKey var1=null;
    String var5="",var3="";
    SecretKeySpec var2=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        speedView = (SpeedView) findViewById(R.id.speedView);
        speedViewNPA = (SpeedView) findViewById(R.id.speedViewNPA);
        loan = (Button) findViewById(R.id.loan);
        deposite = (Button) findViewById(R.id.deposite);
        advance = (Button) findViewById(R.id.advance);
        pieChart = findViewById(R.id.pichart);
        loan.setOnClickListener(this);
        deposite.setOnClickListener(this);
        advance.setOnClickListener(this);
        linechart = findViewById(R.id.linechart);
        txt_heading= (TextView) findViewById(R.id.txt_heading);
        Bundle bObj = getIntent().getExtras();
        var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
        var3 = (String)getIntent().getSerializableExtra("VAR3");
        try {
            if (bObj != null) {
                Log.e("bObj---", "bObj-if");
                if(bObj.containsKey("USERID")){
                    Log.e("bObj---", "bObj-if1");
                    usrcode=bObj.getString("USERID");
                    UserDao.setUserId(usrcode);
                }
                else{

                    usrcode=UserDao.getUserId();
                    Log.e("bObj---", "bObj-else0"+usrcode);
                }


            }
            else{
                Log.e("bObj---", "bObj-else1");
                usrcode=UserDao.getUserId();
                Log.e("bObj---", "bObj-else2"+usrcode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("bObj---", "bObj-exception"+usrcode);
            //Log.e("DASHBOARD", "" + e);
        }

        CallWebService webservice=new CallWebService();
        webservice.execute();
    }

    @Override
    public void onClick(View view) {
        Intent in=null;
        switch(view.getId())
        {

            case R.id.loan:
                Bundle b=new Bundle();
                in = new Intent(Dashboard.this, Loan.class);
                b.putString("retjson",retjson);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                in.putExtras(b);
                startActivity(in);
                finish();
                break;
            case R.id.deposite:
                in = new Intent(Dashboard.this, DepositeDetailsChart.class);

                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                finish();
                break;

            case R.id.advance:
                in = new Intent(Dashboard.this, AdvanceDetailsChart.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                finish();
                break;
        }
    }

    public void onBackPressed() {
        showshareAlert(getString(R.string.alrt_exit));
    }

    public void showshareAlert(final String str)
    {
        CustomDialogClass alert=new CustomDialogClass(Dashboard.this, str) {
            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.custom_dialog_box);
                Button btn = (Button) findViewById(R.id.btn_cancel);
                TextView txt_message=(TextView)findViewById(R.id.txt_dia);
                txt_message.setText(str);
                btn.setOnClickListener(this);
                btn.setText("Cancel");
                Button btnok = (Button) findViewById(R.id.btn_ok);
                btnok.setOnClickListener(this);
                btnok.setText("Ok");
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        this.dismiss();
                        finish();
                        break;

                    case R.id.btn_cancel:
                        this.dismiss();
                        break;
                    default:
                        break;
                }
                dismiss();
            }
        };
        alert.show();
    }


    class CallWebService extends AsyncTask<Void, Void, Void>
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Dashboard.this);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";

        boolean isWSCalled = false;
        protected void onPreExecute()
        {
            cnt=0;
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                jsonObj.put("USERCODE",usrcode);
                jsonObj.put("METHODCODE","2");
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                String keyStr=CryptoClass.Function2();
                var2=CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);

                request.addProperty("value1", CryptoClass.Function5(valuesToEncrypt[0], var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);
                if(androidHttpTransport!=null)
                    System.out.println("=============== androidHttpTransport is not null ");
                else
                    System.out.println("=============== androidHttpTransport is  null ");

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                int i = envelope.bodyIn.toString().trim().indexOf("=");
                var5 = var5.substring(i + 1, var5.length() - 3);

            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            loadProBarObj.dismiss();
           // String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
            String str=CryptoClass.Function6(var5,var2);
            Log.e("sudarshan--xml_data[0] ",str);
            retjson=str;
            try{
                JSONObject jobj=new JSONObject(str);
                if(jobj.getString("RESPCODE").equalsIgnoreCase("0")){
                    String userName=jobj.getString("USERNAME");
                    Log.e("userName--",userName);
                    txt_heading.setText("Welcome "+userName);
                    setGraphs();
                    //drawChart();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }// end onPostExecute

    }// end CallLoginWebService


   /* class CallWebService extends CallSoapWebService
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Dashboard.this);
        @Override
        protected void onPreExecute()
        {
            cnt=0;
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                jsonObj.put("USERCODE",usrcode);
                jsonObj.put("METHODCODE","2");
                CallSoapWebService.jsonObjStr=jsonObj.toString();
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            loadProBarObj.dismiss();
            Log.e("sudarshan--xml_data[0] ",result);
            retjson=result;
            try{
                JSONObject jobj=new JSONObject(result);
                if(jobj.getString("RESPCODE").equalsIgnoreCase("0")){
                    String userName=jobj.getString("USERNAME");
                    Log.e("userName--",userName);
                    txt_heading.setText("Welcome "+userName);
                    setGraphs();
                    //drawChart();
                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }// end onPostExecute

    }// end CallLoginWebService

*/
    public void setGraphs(){

        drawSpeedViewnpa();

        drawSpeedView();

        drawChart();

        drawDepositeLineChart();

        drawLoanLineChart();

    }

    public void drawSpeedViewnpa(){
        try {
            JSONObject job = new JSONObject(retjson);
            int data=Integer.parseInt(job.getString("NPARATIO"));
            speedViewNPA.setMaxSpeed(20);
            speedViewNPA.setTickNumber(11);
            speedViewNPA.speedTo(data);
            speedViewNPA.setWithTremble(false);
            speedViewNPA.setUnit("% NPA");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void drawSpeedView(){
        try {
            JSONObject job = new JSONObject(retjson);
            int data=Integer.parseInt(job.getString("CDRATIO"));
            speedView.setMaxSpeed(20);
            speedView.setTickNumber(11);
            speedView.speedTo(data);
            speedView.setWithTremble(false);
            speedView.setUnit("% CDRATIO");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void drawChart() {
        try{


            float totamount=0;
            ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
            ArrayList<Integer> colors = new ArrayList<>();
            JSONObject job = new JSONObject(retjson);
            JSONArray jarr=job.getJSONArray("YESTERDAYLONDATA");
            for(int i=0;i<jarr.length();i++){

                JSONObject jobj=jarr.getJSONObject(i);
                float value=Float.parseFloat(jobj.getString("BALANCE"));
                Log.e("value","value-- "+value);
                if(jobj.getString("BALANCE").indexOf("-")==-1){
                    yvalues.add(new PieEntry(value, jobj.getString("NPASTAUS"),i));
                }
                else{
                    yvalues.add(new PieEntry(value*-1, jobj.getString("NPASTAUS"), i));
                }

            }

            pieChart.setUsePercentValues(true);



            PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.loanlbl));
            PieData data = new PieData(dataSet);

            data.setValueFormatter(new PercentFormatter());
            Legend l = pieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setWordWrapEnabled(true);
            l.setDrawInside(false);
            l.setYOffset(6f);
            Description description = new Description();
            description.setText(getString(R.string.pie_chart));
            pieChart.setDescription(description);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setTransparentCircleRadius(60f);
            pieChart.setHoleRadius(60f);



            colors.add(Color.rgb(46, 204, 113));
            colors.add(Color.rgb(241, 196, 15));
            colors.add(Color.rgb(231, 76, 60));
            colors.add(Color.rgb(52, 152, 219));
            colors.add(Color.rgb(179, 100, 53));
            colors.add(Color.rgb(255, 140, 157));
            colors.add(Color.rgb(192, 255, 140));

            dataSet.setColors(colors);
            data.setValueTextSize(8f);
            data.setValueTextColor(Color.BLACK);
            int colorBlack = Color.parseColor("#000000");
            pieChart.setEntryLabelColor(colorBlack);
            pieChart.setEntryLabelTextSize(8f);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieChart.setData(data);
            pieChart.invalidate();


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void drawDepositeLineChart() {

        try{

            long yesvalue=0,daybefore=0;
            Log.e("sudarshan--- ",retjson);
            JSONObject job = new JSONObject(retjson);
            JSONArray jarr = job.getJSONArray("DEPOSITE");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);

                Log.e("5289--- ",jobj.getString("YESDEPOSIT"));
                Log.e("7896389--- ",jobj.getString("BEFYESDEPOSIT"));

                yesvalue =Long.parseLong(jobj.getString("YESDEPOSIT"));

                daybefore =Long.parseLong(jobj.getString("BEFYESDEPOSIT"));
            }
            Log.e("yesvalue--- ",""+yesvalue);
            Log.e("daybefore--- ",""+daybefore);
            int a=(int)yesvalue/1000;
            int b=(int)daybefore/1000;
            Log.e("yesvalue--- a",""+a);
            Log.e("daybefore---b ",""+b);
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, a));
            entries.add(new Entry(1, b));
            //entries.add(new Entry(2, 2));
            //entries.add(new Entry(3, 4));

            LineDataSet dataSet = new LineDataSet(entries, "Deposit (Amount In Rs.)");
            dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            //****
            // Controlling X axis
            XAxis xAxis = linechart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value
            final String[] months = new String[]{"Yesterday", "Day Bef.Yesterday"};//, "Mar", "Apr"

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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void drawLoanLineChart(){

        try{

            float yesvalue=0.0f,daybefore=0.0f;
            Log.e("sudarshan--- ",retjson);
            JSONObject job = new JSONObject(retjson);
            JSONArray jarr = job.getJSONArray("ADVANCE");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);

                Log.e("5289--- ",jobj.getString("YESADVANCE"));
                Log.e("7896389--- ",jobj.getString("BEFYESADVANCE"));

                yesvalue =Float.parseFloat(jobj.getString("YESADVANCE"));
                yesvalue=yesvalue/1000;

                daybefore =Float.parseFloat(jobj.getString("BEFYESADVANCE"));
                daybefore=daybefore/1000;
            }
            int bal1= (int) yesvalue;
            int bal2= (int) daybefore;

            linechart1 = findViewById(R.id.linechart1);

            ArrayList<Entry> entries1 = new ArrayList<>();
            entries1.add(new Entry(0, bal1));
            entries1.add(new Entry(1, bal2));
            //entries.add(new Entry(2, 2));
            //entries.add(new Entry(3, 4));

            LineDataSet dataSet1 = new LineDataSet(entries1, "Advances (Amount In Rs.)");
            dataSet1.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            dataSet1.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            //****
            // Controlling X axis
            XAxis xAxis1 = linechart1.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value
            final String[] months1 = new String[]{"Yesterday", "Today"};//, "Mar", "Apr"

            IAxisValueFormatter formatter1 = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return months1[(int) value];
                }
            };
            xAxis1.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis1.setValueFormatter(formatter1);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight1 = linechart1.getAxisRight();
            yAxisRight1.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft1= linechart1.getAxisLeft();
            yAxisLeft1.setGranularity(1f);

            // Setting Data
            LineData data4 = new LineData(dataSet1);
            linechart1.setData(data4);
            linechart1.animateX(2500);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
