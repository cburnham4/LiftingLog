package tracker.lift_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import HelperFiles.DateConverter;

public class Activity_Graph extends Activity{
    String maxDate;
    ArrayList<Date> dates;

    private AdsHelper adsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SET UP CLASS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_progress);

        LiftDatabase dbHelper = new LiftDatabase(getBaseContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Intent recievedIntent = getIntent();
        final int lid = recievedIntent.getIntExtra("LID", 0);

        DateConverter dc = new DateConverter();
        //------------------------------------//
        //CALL PRIVATE METHODS TO CREATE GRAPH
        ArrayList<Integer> maxes = null;
        try {
            maxes = runSQLQuery(lid, db);
        } catch (ParseException e) {
            Log.i("PARSE ERROR", "FUCKED UP");
        }
        DataPoint[] dps = createDataPoints(maxes);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dps);
        if (maxes.size()>0){
            this.createGraph(series, maxes.get(0));
        }
        //this.createGraph(series, maxes.get(0));


        TextView maxView = (TextView) findViewById(R.id.Max);
        TextView occuredOnView = (TextView) findViewById(R.id.Occured_On);
        double max = getCalculatedMax(lid,db);
        maxView.setText("Max: "+max);
        if(maxDate!=null){
            occuredOnView.setText("Occured on: "+ dc.convertDateToText(maxDate));
        }

        adsHelper = new AdsHelper(getWindow().findViewById(android.R.id.content), getResources().getString(R.string.banner_ad_on_pastlifts),this);
        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);


    }

    private ArrayList<Integer> runSQLQuery(int lid, SQLiteDatabase db) throws ParseException {
        String sql = "SELECT MAX(maxWeight), date_Lifted FROM Max WHERE lid = "+lid +" GROUP BY date_Lifted ORDER BY date_Lifted";
        Cursor c = db.rawQuery(sql, null);
        ArrayList<Integer> maxes = new ArrayList<Integer>();
        dates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        c.moveToFirst();
        while (c.isAfterLast() == false){
            maxes.add(c.getInt(0));
            dates.add(dateFormat.parse(c.getString(1)));
            c.moveToNext();
        }
        return maxes;
    }

    /*todo add in for different dates */

    private DataPoint[] createDataPoints(ArrayList<Integer> maxes){
        DataPoint[] dps = new DataPoint[maxes.size()];//maxes.size()
        Log.i("MAX SIZE", maxes.size() + "");
        for(int i = 0; i<maxes.size(); i++){//maxes.size()
            Log.i("Date", dates.get(i)+"");
            dps[i] = new DataPoint(dates.get(i), maxes.get(i));
        }
        return dps;
    }

    private void createGraph(LineGraphSeries<DataPoint> series, int maxIfOne){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        if(dates.size()!= 0){
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            if(dates.size() == 1){
                PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(dates.get(0), maxIfOne),
                });

                graph.addSeries(series3);
                series3.setShape(PointsGraphSeries.Shape.TRIANGLE);

                graph.getViewport().setMinX(dates.get(0).getTime()-5*24*60*60*1000);
                graph.getViewport().setMaxX(dates.get(dates.size()-1).getTime()+5*24*60*60*1000);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(maxIfOne-10);
                graph.getViewport().setMaxY(maxIfOne + 10);
                graph.setTitle("Max Weight Over Time");
            }else{
                graph.getViewport().setMinX(dates.get(0).getTime()-.5*24*60*60*1000);
                graph.getViewport().setMaxX(dates.get(dates.size()-1).getTime());
                graph.setTitle("Max Weight Over Time");

                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setThickness(4);

                graph.getViewport().setScalable(true);
                graph.getViewport().setScrollable(true);
                graph.addSeries(series);
            }


        }

    }

    private double getCalculatedMax(int lid, SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT MAX(maxWeight), date_Lifted "
                + "From Max "
                + "Where lid = " + lid + ""
                , null);
        c.moveToFirst();
        int max = c.getInt(0);
        maxDate = c.getString(1);
        Log.i("MAX: ", max + "");
        return max;
    }
}
