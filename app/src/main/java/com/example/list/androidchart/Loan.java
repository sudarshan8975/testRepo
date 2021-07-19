package com.example.list.androidchart;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

public class Loan extends AppCompatActivity {
    PieChart pichart;
    BarChart barchart;
    private static String retjson = "";
    ArrayList<String> ydata=new ArrayList<>();
    ArrayList<String> xdata=new ArrayList<String>();
    ArrayList barEntriesArrayList = new ArrayList<>();
    final ArrayList<String> xAxisLabel = new ArrayList<>();
    BarDataSet barDataSet;
    BarData barData;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static final String METHOD_NAME = "getBranchwiseLoans";
    private static final String METHOD_NAME1 = "getLoanData";
    String retVal="",npastatus="";

    PrivateKey var1=null;
    String var5="",var3="";
    SecretKeySpec var2=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
        pichart=(PieChart)findViewById(R.id.pichart);
        barchart=(BarChart)findViewById(R.id.barchart);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            retjson = extras.getString("retjson");
        }

        var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
        var3 = (String)getIntent().getSerializableExtra("VAR3");

        barchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                int x=(int)e.getX();
                int y=(int)e.getY();
                Bundle b=new Bundle();
                Log.e("sud--x--",x+"---"+xAxisLabel.get(x));
                Log.e("sud--y--",y+"");
                Intent in= new Intent(Loan.this, LoanDetailsChart.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                in.putExtra("retjson", retjson);
                b.putString("npaStatus",xAxisLabel.get(x));
                in.putExtras(b);
                startActivity(in);
                finish();
            }

            @Override
            public void onNothingSelected()
            {

            }
        });

        pichart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                try {
                    int pos1 = e.toString().indexOf("y: ");
                    String balances = e.toString().substring(pos1 + 3);
                    for (int i = 0; i < xdata.size(); i++) {
                        if (xdata.get(i).equalsIgnoreCase(balances)) {
                            pos1 = i;
                            break;
                        }
                    }
                    npastatus = ydata.get(pos1);
                    CallWebService cws=new CallWebService();
                    cws.execute();
                }
                catch(Exception e1){
                    e1.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //CallWebServiceGetLoanData webdata=new CallWebServiceGetLoanData();
       // webdata.execute();
        drawBarChart();
        //drawChart();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(Loan.this, Dashboard.class);
        in.putExtra("VAR1", var1);
        in.putExtra("VAR3", var3);
        startActivity(in);
        finish();
    }

    class CallWebServiceGetLoanData extends AsyncTask<Void, Void, Void>
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Loan.this);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";

        boolean isWSCalled = false;
        protected void onPreExecute()
        {
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                jsonObj.put("NAPSTATUS","");
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
            NAMESPACE =getString(R.string.namespace);
            URL =getString(R.string.url);
            SOAP_ACTION =getString(R.string.soap_action);

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        180000);
                Log.e("suddd","androidHttpTransport"+androidHttpTransport);
                if (androidHttpTransport != null)
                    System.out
                            .println("=============== androidHttpTransport is not null ");
                else
                    System.out
                            .println("=============== androidHttpTransport is  null ");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                retVal = envelope.bodyIn.toString().trim();
                retVal = retVal.substring(retVal.indexOf("=") + 1,
                        retVal.length() - 3);
                Log.e("suddd","suddddd11111");
                isWSCalled = true;
            }// end try
            catch (SocketTimeoutException e)
            {
                e.printStackTrace();
            }
            catch(ConnectException e){
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Exception" + e);
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            loadProBarObj.dismiss();
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
            Log.e("sudarshan--xml_data[0] ",xml_data[0]);
            //retjson=xml_data[0];
            try{
                JSONObject jobj=new JSONObject(xml_data[0]);
                if(jobj.getString("RESPCODE").equalsIgnoreCase("0")){
                    /*Bundle b=new Bundle();
                    Intent in=new Intent(Loan.this,LoanDetailsChart.class);
                    b.putString("RETVAL",xml_data[0]);
                    startActivity(in);
                    finish();*/

                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }// end onPostExecute

    }


    class CallWebService extends AsyncTask<Void, Void, Void>
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Loan.this);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";

        boolean isWSCalled = false;
        protected void onPreExecute()
        {
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                jsonObj.put("NAPSTATUS",npastatus);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            valuesToEncrypt[0] = jsonObj.toString();
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
            String str=CryptoClass.Function6(var5,var2);
            Log.e("sudarshan--xml_data[0] ",str);
            retjson=str;
            try{
                JSONObject jobj=new JSONObject(str);
                if(jobj.getString("RESPCODE").equalsIgnoreCase("0")){
                   // setGraphs();
                   // drawChart();
                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }// end onPostExecute

    }

    public void drawBarChart(){
        try{

            /*barEntriesArrayList.add(new BarEntry(1f, 1));
            barEntriesArrayList.add(new BarEntry(2f, 2));
            barEntriesArrayList.add(new BarEntry(3f, 3));
            barEntriesArrayList.add(new BarEntry(4f, 4));
            barEntriesArrayList.add(new BarEntry(5f, 5));
            barEntriesArrayList.add(new BarEntry(6f, 6));*/

            JSONObject job = new JSONObject(retjson);
            JSONArray jarr=job.getJSONArray("YESTERDAYLONDATA");
            for(int i=0;i<jarr.length();i++){
                JSONObject jobj=jarr.getJSONObject(i);
                float value=Float.parseFloat(jobj.getString("BALANCE"));
                Log.e("value","value-- "+value);
                if(jobj.getString("BALANCE").indexOf("-")==-1){
                    barEntriesArrayList.add(new BarEntry(i, value,jobj.getString("NPASTAUS")));
                    xAxisLabel.add(jobj.getString("NPASTAUS"));
                }
                else{
                    barEntriesArrayList.add(new BarEntry(i, value*-1,jobj.getString("NPASTAUS")));
                    xAxisLabel.add(jobj.getString("NPASTAUS"));
                }

            }

            barDataSet = new BarDataSet(barEntriesArrayList, "Loan Status");
            barData = new BarData(barDataSet);

            barchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));


            barchart.setData(barData);
            barchart.getAxisRight().setEnabled(false);
            barchart.setScaleMinima(1.7f, 0f);
            barchart.setPinchZoom(false);
            barchart.setDoubleTapToZoomEnabled(false);
            //barchart.getXAxis().setAxisMinimum(0f);
            //barchart.getAxisLeft().setStartAtZero(false);
            //barchart.getAxisRight().setStartAtZero(false);
            //barchart.getAxisLeft().setDrawGridLines(false);
            barchart.getXAxis().setDrawGridLines(false);
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barchart.animateXY(2000, 2000);
            barDataSet.setValueTextSize(10f);
            //barchart.getDescription().setEnabled(false);
        }
        catch(Exception e){
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
                    ydata.add(i,jobj.getString("NPASTAUS"));
                    xdata.add(i,""+value);
                    yvalues.add(new PieEntry(value, jobj.getString("NPASTAUS"),i));
                }
                else{
                    yvalues.add(new PieEntry(value*-1, jobj.getString("NPASTAUS"), i));
                    ydata.add(i,jobj.getString("NPASTAUS"));
                    xdata.add(i,""+(value*-1));
                }

            }

            pichart.setUsePercentValues(true);



            PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.loanlbl));
            PieData data = new PieData(dataSet);

            data.setValueFormatter(new PercentFormatter());
            Legend l = pichart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setWordWrapEnabled(true);
            l.setDrawInside(false);
            l.setYOffset(6f);
            Description description = new Description();
            description.setText(getString(R.string.pie_chart));
            pichart.setDescription(description);
            pichart.setDrawHoleEnabled(true);
            pichart.setTransparentCircleRadius(60f);
            pichart.setHoleRadius(60f);



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
            pichart.setEntryLabelColor(colorBlack);
            pichart.setEntryLabelTextSize(8f);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pichart.setData(data);
            pichart.invalidate();


        }catch(Exception e){
            e.printStackTrace();
        }

    }


}
