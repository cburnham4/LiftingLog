package tracker.lift_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import HelperFiles.DateConverter;
import tracker.lift_log.ListViewHelpers.PastCardViewAdapter;
import tracker.lift_log.ListViewHelpers.PastDay;
import tracker.lift_log.ListViewHelpers.Set;


public class PastDates extends Activity{
    private RecyclerView rv_pastdates;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LiftDatabaseHelper dbHelper;
    private DateConverter dateConverter;

    private ArrayList<PastDay> pastDates;
    private ArrayList<String> dates;

    private AdsHelper adsHelper;
    private int lid;

    /*
        Todo be able to delete it 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_dates);

        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);
        //USE DATABASE
        dbHelper = new LiftDatabaseHelper(getBaseContext());

        dateConverter = new DateConverter();

        this.getDates();
        this.generateData();



        rv_pastdates = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rv_pastdates.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PastCardViewAdapter(pastDates);
        rv_pastdates.setAdapter(mAdapter);

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

    public void getDates(){
        dates = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql  = "SELECT distinct date_Created FROM Sets WHERE lid = +"+lid+ " ORDER BY date_Created desc";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            dates.add(c.getString(0));
            c.moveToNext();
        }
    }

    private void generateData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        pastDates = new ArrayList<>();
        for(int i=0; i <dates.size();i++){
            ArrayList<Set> sets = new ArrayList<>();
            /* TODO
             redo query
             */
            String sql  = "SELECT weight, reps FROM Sets WHERE lid = +"+lid+ " and date_Created = '"+dates.get(i)+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            String pastLift ="";
            while (c.isAfterLast() == false) {
                sets.add(new Set(0, lid, c.getInt(0), c.getInt(1), dates.get(i)));
                c.moveToNext();
            }
            PastDay pastDay = new PastDay(sets, dateConverter.convertDateToText(dates.get(i)));

            pastDates.add(pastDay);

        }

    }
}
