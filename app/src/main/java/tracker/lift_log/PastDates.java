package tracker.lift_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import HelperFiles.DateConverter;
import HelperFiles.Item;
import HelperFiles.PastDateAdapter;


public class PastDates extends Activity{
    private int lid;
    LiftDatabase dbHelper;
    ArrayList<String> aod;
    ArrayList<String> aol;
    DateConverter dateConverter;
    private AdsHelper adsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_dates);
        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);
        //USE DATABASE
        dbHelper = new LiftDatabase(getBaseContext());
        getDates();


        dateConverter = new DateConverter();

        PastDateAdapter adapter = new PastDateAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.listViewPast);
        listView.setAdapter(adapter);

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
        aod = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql  = "SELECT distinct date_Created FROM Sets WHERE lid = +"+lid+ " ORDER BY date_Created desc";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            aod.add(c.getString(0));
            c.moveToNext();
        }
    }

    private ArrayList<Item> generateData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Item> items = new ArrayList<Item>();
        for(int i=0; i <aod.size();i++){
            String sql  = "SELECT weight, reps FROM Sets WHERE lid = +"+lid+ " and date_Created = '"+aod.get(i)+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            String pastLift ="";
            while (c.isAfterLast() == false) {
                pastLift += "Reps: " +c.getString(1) +" Weight: "+c.getString(0)+"\n";
                c.moveToNext();
            }
            String date = dateConverter.convertDateToText(aod.get(i));
            items.add(new Item(date,pastLift));

        }
        return items;
    }
}
