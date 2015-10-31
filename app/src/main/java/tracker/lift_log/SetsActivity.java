package tracker.lift_log;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SetsActivity extends Activity implements AdListener{
	private LiftDatabase dbHelper;
	private ArrayList<String> Sets;
	private long set = 0;
    //private MyAdapter adapter;
    private int lid;
    private ArrayList<Integer> sids;

    private ViewGroup adViewContainer;
    private com.amazon.device.ads.AdLayout amazonAdView;
    private com.google.android.gms.ads.AdView admobAdView;
    private boolean amazonAdEnabled;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sets_layout);
        //GET DATABASE
		dbHelper = new LiftDatabase(getBaseContext());
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
        this.setUpAds();
        //GET LID
		Intent recievedIntent = getIntent();
		this.lid = recievedIntent.getIntExtra("LID", 0);
        sids = new ArrayList<Integer>();
        loadCurrentDay(lid);

        //FIND VIEWS
        TextView currentLift = (TextView)findViewById(R.id.currentLift);
        ListView setsList =(ListView)findViewById(R.id.setsList);
        Button addSet =(Button)findViewById(R.id.AddSet);
		//adapter =  new MyAdapter(this, Sets);
        final TextView max = (TextView)findViewById(R.id.max);
        //-------------------------------------

		//setsList.setAdapter(adapter);
		//adapter.notifyDataSetChanged();
		max.setText("Calculated Max: "+getCalculatedMax(lid) );

        //GET VIEWS
        Button addRep = (Button) findViewById(R.id.addRep);
        Button subRep = (Button) findViewById(R.id.subRep);
        Button subWeight = (Button) findViewById(R.id.subWeight);
        final Button addWeight = (Button) findViewById(R.id.addWeight);
        final EditText repCount = (EditText) findViewById(R.id.Reps);
        final EditText weightCount = (EditText) findViewById(R.id.Weight);

        addRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep+1 +"");
            }
        });
        subRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep-1 +"");
            }
        });
        addWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int weight = Integer.parseInt(weightCount.getText().toString());
                weightCount.setText(weight+5 +"");
            }
        });
        subWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int weight = Integer.parseInt(weightCount.getText().toString());
                weightCount.setText(weight - 5 + "");
            }
        });

//CHANGE BACK
        addSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(weightCount.getText().toString()!= "" && repCount.getText().toString()!= ""){
                    int weight = Integer.parseInt(weightCount.getText().toString());
                    int reps = Integer.parseInt(repCount.getText().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    //PUT SET INTO SETS
                    ContentValues values = new ContentValues();
                    values.put("weight", weight);
                    values.put("reps", reps);
                    values.put("date_Created", dateFormat.format(date));
                    values.put("lid", lid);
                    set = db.insert("Sets",null, values);
                    //-----------------------
                    Sets.add("Weight: "+weight+" Reps: "+reps);
                    //if(reps <= 6){
                    int sid = getLastSid();
                    sids.add(sid);
                    setCalculatedMAX(sid, lid, weight, reps);
                    max.setText("Calculated Max: "+getCalculatedMax(lid));
                    //adapter.notifyDataSetChanged();
                }
            }
        });
        registerForContextMenu(setsList);

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

    private void setCalculatedMAX(int sid, int lid, int weight, int reps){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        double calculatedMax = getCalculatedMax(lid);
        double m = 0.0;
        switch(reps){
            case 1:
                m = weight;
                break;
            case 2:
                m = weight*1.042;
                break;
            case 3:
                m = weight*1.072;
                break;
            case 4:
                m = weight*1.104;
                break;
            case 5:
                m = weight*1.137;
                break;
            default:
                m = weight*1.173;
        }
        //if(m > calculatedMax){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put("date_Lifted", dateFormat.format(date));
            values.put("maxWeight", m);
            values.put("lid", lid);
            values.put("sid", sid);
            db.insert("Max",null, values);
        //}
        Log.i("MAX", m+"");
    }
	private double getCalculatedMax(int lid){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT MAX(maxWeight) "
				+ "From Max "
				+ "Where lid = "+lid+""
				, null);
		c.moveToFirst();
		int max = c.getInt(0);
		return max;
	}
	private void loadCurrentDay(int lid){
		Sets= new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {"weight","reps"};
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
    	Date date = new Date();
        String formattedDate = dateFormat.format(date);
    	String sql  = "SELECT weight, reps, sid FROM Sets WHERE lid = +"+lid+ " and date_Created = '"+formattedDate+"'";//ADD DATE = DATE
    	Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
            Log.i("SETSANDREPS", "Weight: "+c.getString(0)+" Reps: "+c.getString(1)+" sets: "+ c.getInt(2));

            sids.add(c.getInt(2));
			Sets.add("Weight: "+c.getString(0)+" Reps: "+c.getString(1));
			c.moveToNext();
		}
	}

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_current , menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.deleteCurrent:
                //Toast.makeText(this, "Deleted: " + adapter.getItem(info.position), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(info.position);
        }
        return true;
    }
    private void deleteFromDatabase(int position){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        int sid = sids.get(position);
        db.delete("Sets","sid = "+sid,null);
        db.delete("Max","sid = "+sid,null);
        Log.i("DELETION","Tried to delete set: "+sid+" from position "+position);
        Sets.remove(position);
        sids.remove((Integer) sid);
        TextView max = (TextView)findViewById(R.id.max);
        max.setText("Calculated Max: "+getCalculatedMax(lid));
        //adapter.notifyDataSetChanged();
    }
    private int getLastSid(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Max(sid) FROM Sets";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }
    private void setUpAds(){
        AdRegistration.setAppKey(getString(R.string.amazon_ad_id));
        amazonAdView = new com.amazon.device.ads.AdLayout(this, com.amazon.device.ads.AdSize.SIZE_320x50);
        amazonAdView.setListener(this);
        //AdRegistration.enableTesting(true);
        admobAdView = new com.google.android.gms.ads.AdView(this);
        admobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobAdView.setAdUnitId(getString(R.string.banner_ad_on_inputset));

        // Initialize view container
        adViewContainer = (ViewGroup)findViewById(R.id.AdLayoutAddSet);
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
        Log.e("AD ERROR" , adError.getCode() +": "+ adError.getMessage());
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
