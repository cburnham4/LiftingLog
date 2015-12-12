package tracker.lift_log.TabbedActivities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.Helpers.DateConverter;
import tracker.lift_log.R;

public class Activity_Graph extends Activity{
    String maxDate;

    private GraphView graph;

    LineGraphSeries <DataPoint> oneMonthSeries;
    LineGraphSeries <DataPoint> threeMonthSeries;
    LineGraphSeries <DataPoint> sixMonthSeries;
    LineGraphSeries <DataPoint> oneYearSeries;
    LineGraphSeries <DataPoint> allTimeSeries;

    LiftDatabaseHelper liftDatabaseHelper;
    private int lid;

    private AdsHelper adsHelper;

    /* todo add message for not enough data */

    /* todo Change to new graph library */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SET UP CLASS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_progress);

        liftDatabaseHelper = new LiftDatabaseHelper(getBaseContext());
        SQLiteDatabase db = liftDatabaseHelper.getReadableDatabase();

        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);

        DateConverter dc = new DateConverter();
        graph = (GraphView) findViewById(R.id.graph);
        //------------------------------------//
        //CALL PRIVATE METHODS TO CREATE GRAPH
        ArrayList<Integer> maxes = null;
        try {
            this.getDataPoints();
        } catch (ParseException e) {
            e.printStackTrace();
        }


//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dps);
//        if (maxes.size()>0){
//            this.createGraph(series, maxes.get(0));
//        }

        this.setUpTextViews();


        TextView maxView = (TextView) findViewById(R.id.Max);
        TextView occuredOnView = (TextView) findViewById(R.id.Occured_On);
        double max = getCalculatedMax();
        maxView.setText("Max: "+max);
        if(maxDate!=null){
            occuredOnView.setText("Occured on: "+ dc.convertDateToText(maxDate));
        }

        this.createGraph(oneMonthSeries, 0);

//        adsHelper = new AdsHelper(getWindow().findViewById(android.R.id.content), getResources().getString(R.string.banner_ad_on_pastlifts),this);
//        adsHelper.setUpAds();
//        int delay = 1000; // delay for 1 sec.
//        int period = getResources().getInteger(R.integer.ad_refresh_rate);
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                adsHelper.refreshAd();  // display the data
//            }
//        }, delay, period);


    }


    private void getDataPoints() throws ParseException {
        SQLiteDatabase db = liftDatabaseHelper.getReadableDatabase();

        /* Run the sql command */
        String sql = "SELECT MAX(maxWeight), date_Lifted FROM Max WHERE lid = "+lid +" GROUP BY date_Lifted ORDER BY date_Lifted";
        Cursor c = db.rawQuery(sql, null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        oneMonthSeries = new LineGraphSeries<>();
        threeMonthSeries = new LineGraphSeries<>();
        sixMonthSeries = new LineGraphSeries<>();
        oneYearSeries = new LineGraphSeries<>();
        allTimeSeries = new LineGraphSeries<>();

        c.moveToFirst();

        /* Get the current date */
        Date currentDate = new Date();

        double currentTime = currentDate.getTime();
        Log.e("CURRENT TIME ", " current time = " + currentTime);
        /* Milliseconds in a day */
        double timeInDay = 24 * 60 * 60 * 1000;
        double timeInMonth = timeInDay * 30.5;
        double timeIn3Month = timeInMonth *3;
        double timeIn6Month = timeIn3Month * 2;
        double timeInYear = timeIn6Month * 2;

        for(int i = 0; !c.isAfterLast() ; i++){
            Date date = dateFormat.parse(c.getString(1));
            double timeDiff  = currentTime - date.getTime();
            DataPoint dataPoint =  new DataPoint(date, c.getInt(0));
            allTimeSeries.appendData(dataPoint, true, c.getCount());
            Log.e("CURRENT TIME ", " date time = " + date.getTime());
            Log.e("DIFF TIME ", " diff time = "+timeDiff);
            if(timeDiff < timeInYear){
                oneYearSeries.appendData(dataPoint, true, c.getCount());
                if(timeDiff < timeIn6Month){
                    sixMonthSeries.appendData(dataPoint, true, c.getCount());
                    if(timeDiff < timeIn3Month){
                        threeMonthSeries.appendData(dataPoint, true, c.getCount());
                        if(timeDiff < timeInMonth){
                            oneMonthSeries.appendData(dataPoint, true, c.getCount());
                            Log.e("TIME DIFF", "Days dif = " + timeDiff/timeInDay);
                        }
                    }
                }
            }

            c.moveToNext();
        }

        c.close();
    }

    private void setUpTextViews(){
        final TextView tv_1m = (TextView) findViewById(R.id.tv_1m);
        final TextView tv_3m = (TextView) findViewById(R.id.tv_3m);
        final TextView tv_6m = (TextView) findViewById(R.id.tv_6m);
        final TextView tv_1y = (TextView) findViewById(R.id.tv_1y);
        final TextView tv_all = (TextView) findViewById(R.id.tv_all);


        tv_1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGraph(oneMonthSeries, 0);
                tv_1m.setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_3m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGraph(threeMonthSeries, 0);
                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* todo remove masxifOne */
                createGraph(sixMonthSeries, 0);

                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_1y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createGraph(oneYearSeries, 0);

                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
                tv_all.setBackgroundColor(0);
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* todo remove masxifOne */
                createGraph(allTimeSeries, 0);

                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
            }
        });
    }

    /* todo just change the x min and x max for times */
    private void createGraph(LineGraphSeries<DataPoint> series, int maxIfOne){


        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        if(!series.isEmpty()){
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);

//            if(series.) == 1){
//                PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(new DataPoint[] {
//                        new DataPoint(dates.get(0), maxIfOne),
//                });
//
//                graph.addSeries(series3);
//                series3.setShape(PointsGraphSeries.Shape.TRIANGLE);
//
//                graph.getViewport().setMinX(dates.get(0).getTime()-5*24*60*60*1000);
//                graph.getViewport().setMaxX(dates.get(dates.size()-1).getTime()+5*24*60*60*1000);
//                graph.getViewport().setYAxisBoundsManual(true);
//                graph.getViewport().setMinY(maxIfOne-10);
//                graph.getViewport().setMaxY(maxIfOne + 10);
//                graph.setTitle("Max Weight Over Time");
            //}else{
                graph.getViewport().setMinX(series.getLowestValueX()-.5*24*60*60*1000);
                graph.getViewport().setMaxX(series.getHighestValueX());
                graph.setTitle("Max Weight Over Time");

                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setThickness(4);

                graph.getViewport().setScalable(true);
                graph.getViewport().setScrollable(true);
                graph.addSeries(series);
            //}


        }

    }

    private double getCalculatedMax() {
        SQLiteDatabase db = liftDatabaseHelper.getReadableDatabase();
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
