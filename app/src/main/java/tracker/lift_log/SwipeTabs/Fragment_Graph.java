package tracker.lift_log.SwipeTabs;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import tracker.lift_log.Helpers.DateConverter;
import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Graph extends Fragment {
    private int lid;
    private LiftDatabaseHelper dbHelper;

    SQLiteDatabase readableDatabase;

    private GraphView graph;
    DateConverter dc;

    private String maxDate;

    private AdsHelper adsHelper;
    private DataPoint[] dataPoints;

    double max;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        lid = args.getInt("lid", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        adsHelper = new AdsHelper(view, getResources().getString(R.string.banner_ad_on_days),this.getActivity());
        //adsHelper.runAds();
        dbHelper = new LiftDatabaseHelper(getContext());
        readableDatabase = dbHelper.getReadableDatabase();

        dc = new DateConverter();

        graph = (GraphView) view.findViewById(R.id.graph);
        max = this.getCalculatedMax();

        try {
            this.runSQLQuery();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        if (dataPoints.length>0){
            this.createGraph(series, (int) max);
        }

        TextView maxView = (TextView) view.findViewById(R.id.Max);
        TextView occuredOnView = (TextView) view.findViewById(R.id.Occured_On);

        maxView.setText("Max: "+ (int) max);
        if(maxDate!=null){
            occuredOnView.setText("Occured on: " + dc.convertDateToText(maxDate));
        }

        return view;
    }

    private void runSQLQuery() throws ParseException {
        String sql = "SELECT MAX(maxWeight), date_Lifted FROM Max WHERE lid = "+lid +" GROUP BY date_Lifted ORDER BY date_Lifted";
        Cursor c = readableDatabase.rawQuery(sql, null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dataPoints = new DataPoint[c.getCount()];
        c.moveToFirst();
        for (int i = 0 ; c.isAfterLast() == false; i++){
            dataPoints[i] = new DataPoint(dateFormat.parse(c.getString(1)), c.getInt(0));
            c.moveToNext();
        }
    }

    private void createGraph(LineGraphSeries<DataPoint> series, int maxIfOne){

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getContext()));
        if(dataPoints.length!= 0){
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            if(dataPoints.length == 1){
                PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(dataPoints);

                graph.addSeries(series3);
                series3.setShape(PointsGraphSeries.Shape.TRIANGLE);

                graph.getViewport().setMinX(dataPoints[0].getX()-5*24*60*60*1000);
                graph.getViewport().setMaxX(dataPoints[0].getX()+5*24*60*60*1000);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(maxIfOne-10);
                graph.getViewport().setMaxY(maxIfOne + 10);
                graph.setTitle("Max Weight Over Time");
            }else{
                graph.getViewport().setMinX(dataPoints[0].getX() - 5*24*60*60*1000);
                graph.getViewport().setMaxX(dataPoints[dataPoints.length-1].getX()+5*24*60*60*1000);
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
    private double getCalculatedMax(){
        Cursor c = readableDatabase.rawQuery("SELECT MAX(maxWeight), date_Lifted "
                + "From Max "
                + "Where lid = "+lid+""
                , null);
        c.moveToFirst();
         max = c.getDouble(0);
        maxDate = c.getString(1);
        return max;
    }

    @Override
    public void onPause() {
        adsHelper.onPause();
        super.onPause();
    }

    public void onResume(){
        adsHelper.onResume();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        adsHelper.onDestroy();
        super.onDestroy();
    }


}
