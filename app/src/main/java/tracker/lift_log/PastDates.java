package tracker.lift_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import HelperFiles.DateConverter;
import HelperFiles.Item;
import HelperFiles.PastDateAdapter;


public class PastDates extends Activity implements AdListener{
    private int lid;
    LiftDatabase dbHelper;
    ArrayList<String> aod;
    ArrayList<String> aol;
    DateConverter dateConverter;
    private ViewGroup adViewContainer;
    private com.amazon.device.ads.AdLayout amazonAdView;
    private com.google.android.gms.ads.AdView admobAdView;
    private boolean amazonAdEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_dates);
        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);
        this.setUpAds();
        //USE DATABASE
        dbHelper = new LiftDatabase(getBaseContext());
        getDates();


        dateConverter = new DateConverter();

        PastDateAdapter adapter = new PastDateAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.listViewPast);
        listView.setAdapter(adapter);

        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                refreshAd();  // display the data
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
    private void setUpAds(){
        AdRegistration.setAppKey(getString(R.string.amazon_ad_id));
        amazonAdView = new com.amazon.device.ads.AdLayout(this, com.amazon.device.ads.AdSize.SIZE_320x50);
        amazonAdView.setListener(this);
        //AdRegistration.enableTesting(true);
        admobAdView = new com.google.android.gms.ads.AdView(this);
        admobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobAdView.setAdUnitId(getString(R.string.banner_ad_on_pastlifts));

        // Initialize view container
        adViewContainer = (ViewGroup)findViewById(R.id.AdLayoutPastSet);
        amazonAdEnabled = true;
        adViewContainer.addView(amazonAdView);

        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }


    public void refreshAd()
    {
        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }

    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (!amazonAdEnabled)
        {
            amazonAdEnabled = true;
            adViewContainer.removeView(admobAdView);
            adViewContainer.addView(amazonAdView);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        // Call AdMob SDK for backfill
        if (amazonAdEnabled)
        {
            amazonAdEnabled = false;
            adViewContainer.removeView(amazonAdView);
            adViewContainer.addView(admobAdView);
        }
//        AdRequest.Builder.addTestDevice("04CD51A7A1F806B7F55CADD6A3B84E92");
        admobAdView.loadAd((new com.google.android.gms.ads.AdRequest.Builder()).build());
    }

    @Override
    public void onAdExpanded(Ad ad) {

    }

    @Override
    public void onAdCollapsed(Ad ad) {

    }

    @Override
    public void onAdDismissed(Ad ad) {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.amazonAdView.destroy();
    }

    public void onPause(){
        super.onPause();
        this.amazonAdView.destroy();
    }

    public void onResume(){
        super.onResume();
        this.setUpAds();
    }
}
