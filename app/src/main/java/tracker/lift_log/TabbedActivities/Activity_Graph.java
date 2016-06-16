package tracker.lift_log.TabbedActivities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
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

import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.Helpers.DateConverter;
import tracker.lift_log.R;

public class Activity_Graph extends Activity{
    String maxDate;

    private GraphView graph;
    private RelativeLayout rel_graph;
    private TextView tvNoData;

    /* DataPoints and series */
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private ArrayList<DataPoint> dataPoints;

    LiftDatabaseHelper liftDatabaseHelper;
    private int lid;

    private AdsHelper adsHelper;

    TextView[] tvDateSelections;

    /* todo add message for not enough data */

    /* todo Change to new graph library */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SET UP CLASS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_progress);

        rel_graph = (RelativeLayout) findViewById(R.id.rel_graph);
        tvNoData = (TextView) findViewById(R.id.tv_noData);

        liftDatabaseHelper = new LiftDatabaseHelper(getBaseContext());

        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);

        DateConverter dc = new DateConverter();
        graph = (GraphView) findViewById(R.id.graph);
        //------------------------------------//
        //CALL PRIVATE METHODS TO CREATE GRAPH
        try {
            this.getDataPoints();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        this.setupDateSelections();
        TextView maxView = (TextView) findViewById(R.id.Max);
        TextView occuredOnView = (TextView) findViewById(R.id.Occured_On);
        double max = getCalculatedMax();
        maxView.setText("Max: "+max);
        if(maxDate!=null){
            occuredOnView.setText("Occured on: "+ dc.convertDateToText(maxDate));
        }

        this.createGraph();

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

    public class OnDateRangeSelection implements View.OnClickListener{
        private double timeLimit;
        private int tvIndex;
        public OnDateRangeSelection(double timeLimit, int tvIndex) {
            this.timeLimit = timeLimit;
            this.tvIndex = tvIndex;
        }

        @Override
        public void onClick(View v) {
            Date currentDate = new Date();
            final double currentTime = currentDate.getTime();

            ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
            for (DataPoint dataPoint: dataPoints){
                if(currentTime-dataPoint.getX()<= timeLimit){
                    dataPointsLocal.add(dataPoint);
                }
            }

            DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
            lineGraphSeries.resetData(dataPoints1);
            Viewport viewport = graph.getViewport();
            viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
            viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
            viewport.setMinY(lineGraphSeries.getLowestValueY()-5);
            viewport.setMaxY(lineGraphSeries.getHighestValueY()+5);
            /* Set all points textviews to null bg */
            for (TextView tv: tvDateSelections){
                tv.setBackgroundColor(0);
            }
            /* set background for selected item */
            tvDateSelections[tvIndex].setBackgroundColor(getResources().getColor(R.color.tv_graph_background));
        }
    }

    private void getDataPoints() throws ParseException {
        SQLiteDatabase db = liftDatabaseHelper.getReadableDatabase();

        /* Run the sql command */
        String sql = "SELECT MAX(maxWeight), date_Lifted FROM Max WHERE lid = "+lid
                +" GROUP BY date_Lifted ORDER BY date_Lifted";
        Cursor c = db.rawQuery(sql, null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        dataPoints = new ArrayList<>();
        lineGraphSeries = new LineGraphSeries<>();

        c.moveToFirst();

                /* Add the maxes to the line series */
        while(!c.isAfterLast()){
            Date date = dateFormat.parse(c.getString(1));
            DataPoint dataPoint =  new DataPoint(date, c.getInt(0));
            dataPoints.add(dataPoint);
            lineGraphSeries.appendData(dataPoint, true, c.getCount());
            c.moveToNext();
        }

        if(dataPoints.size() == 0){
            tvNoData.setVisibility(View.VISIBLE);
            rel_graph.setVisibility(View.GONE);
        }else{
            tvNoData.setVisibility(View.GONE);
            rel_graph.setVisibility(View.VISIBLE);
        }

        c.close();
        db.close();
    }


    /* todo clean up code */
    public void setupDateSelections(){
        final TextView tv_1m = (TextView) findViewById(R.id.tv_1m);
        final TextView tv_3m = (TextView) findViewById(R.id.tv_3m);
        final TextView tv_6m = (TextView) findViewById(R.id.tv_6m);
        final TextView tv_1y = (TextView) findViewById(R.id.tv_1y);
        final TextView tv_all = (TextView) findViewById(R.id.tv_all);

        tvDateSelections = new TextView[]{tv_1m, tv_3m, tv_6m, tv_1y, tv_all};

        /* Milliseconds in a day */
        double timeInDay = 24 * 60 * 60 * 1000;
        final double timeInMonth = timeInDay * 30.5;
        final double timeIn3Month = timeInMonth *3;
        final double timeIn6Month = timeIn3Month * 2;
        final double timeInYear = timeIn6Month * 2;

        /* todo check if you can break if not true */
        tv_1m.setOnClickListener(new OnDateRangeSelection(timeInMonth, 0));
        tv_3m.setOnClickListener(new OnDateRangeSelection(timeIn3Month, 1));
        tv_6m.setOnClickListener(new OnDateRangeSelection(timeIn6Month, 2));
        tv_1y.setOnClickListener(new OnDateRangeSelection(timeInYear, 3));
        tv_all.setOnClickListener(new OnDateRangeSelection(Double.MAX_VALUE, 4));

    }

    private void createGraph(){
        graph.setTitle("Max Weight Over Time");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        if(!lineGraphSeries.isEmpty()){
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            if(dataPoints.size() == 1){
                DataPoint dataPoint = dataPoints.get(0);
                PointsGraphSeries<DataPoint> seriesSingle = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                        dataPoint
                });

                graph.addSeries(seriesSingle);
                seriesSingle.setShape(PointsGraphSeries.Shape.TRIANGLE);

                Viewport viewport = graph.getViewport();
                viewport.setMinX(dataPoint.getX()-5*24*60*60*1000);
                viewport.setMaxX(dataPoint.getX()+5*24*60*60*1000);

                viewport.setMinY(dataPoint.getY()-10);
                viewport.setMaxY(dataPoint.getY() + 10);

            }else{
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                viewport.setMinY(lineGraphSeries.getLowestValueY()-5);
                viewport.setMaxY(lineGraphSeries.getHighestValueY()+5);


                lineGraphSeries.setDrawDataPoints(true);
                lineGraphSeries.setDataPointsRadius(10);
                lineGraphSeries.setThickness(4);

                //graph.getViewport().setScalable(true);
                //graph.getViewport().setScrollable(true);
                graph.addSeries(lineGraphSeries);
            }


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
