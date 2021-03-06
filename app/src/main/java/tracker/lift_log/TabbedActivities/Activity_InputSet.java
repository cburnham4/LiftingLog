package tracker.lift_log.TabbedActivities;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.Database.SQLQueryHelper;
import tracker.lift_log.ListViewHelpers.Set;
import tracker.lift_log.ListViewHelpers.SetsAdapter;
import tracker.lift_log.R;


public class Activity_InputSet extends Activity{
    private LiftDatabaseHelper dbHelper;
    private SQLQueryHelper sqlQueryHelper;
    private SQLiteDatabase writableDB;
    private ArrayList<Set> sets;

    private ListView lv_sets;
    private SetsAdapter setsAdapter;

    private AdsHelper adsHelper;
    private int lid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sets_layout);

        Intent recievedIntent = getIntent();
        lid = recievedIntent.getIntExtra("LID", 0);

        dbHelper = new LiftDatabaseHelper(this);
        writableDB = dbHelper.getWritableDatabase();
        sqlQueryHelper = new SQLQueryHelper(this);

        lv_sets =(ListView) findViewById(R.id.setsList);



        loadCurrentDay(lid);

        setsAdapter = new SetsAdapter(this, sets);
        lv_sets.setAdapter(setsAdapter);

        Button addSet =(Button) findViewById(R.id.AddSet);
        Button addRep = (Button) findViewById(R.id.addRep);
        Button subRep = (Button) findViewById(R.id.subRep);
        Button subWeight = (Button) findViewById(R.id.subWeight);
        Button btn_cancel = (Button) findViewById(R.id.btn_clear_values);
        final Button addWeight = (Button) findViewById(R.id.addWeight);
        final EditText repCount = (EditText) findViewById(R.id.Reps);
        final EditText weightCount = (EditText) findViewById(R.id.Weight);



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repCount.setText("0");
                weightCount.setText("0");
            }
        });

        addRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep+1 +"");
            }
        });
        subRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                if(rep !=0){
                    repCount.setText(rep - 1 + "");
                }
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
                if(weight > 4){
                    weightCount.setText(weight - 5 + "");
                }

            }
        });

        addSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(weightCount.getText().toString()!= "" && repCount.getText().toString()!= ""){
                    int weight = Integer.parseInt(weightCount.getText().toString());
                    int reps = Integer.parseInt(repCount.getText().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String formattedDate = dateFormat.format(date);
                    //PUT SET INTO SETS
                    ContentValues values = new ContentValues();
                    values.put("weight", weight);
                    values.put("reps", reps);
                    values.put("date_Created", formattedDate);
                    values.put("lid", lid);
                    writableDB.insert("Sets",null, values);
                    //-----------------------
                    int sid = sqlQueryHelper.getLastSid();
                    sets.add(new Set(sid, lid, weight, reps, formattedDate));

                    setCalculatedMAX(sid, lid, weight, reps);;
                    setsAdapter.notifyDataSetChanged();
                }
            }
        });
        registerForContextMenu(lv_sets);

        adsHelper = new AdsHelper(getWindow().findViewById(android.R.id.content), getResources().getString(R.string.banner_ad_on_inputset),this);
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

    private void loadCurrentDay(int lid){
        sets= new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        String sql  = "SELECT weight, reps, sid FROM Sets WHERE lid = +"+lid+ " and date_Created = '"+formattedDate+"'";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            Log.i("SETSANDREPS", "Weight: " + c.getString(0) + " Reps: " + c.getString(1) + " sets: " + c.getInt(2));

            sets.add(new Set(c.getInt(2), this.lid, c.getInt(0), c.getInt(1), formattedDate));
            c.moveToNext();
        }
    }
    private void setCalculatedMAX(int sid, int lid, int weight, int reps){
        /*
            max calculations taken from http://www.weightrainer.net/training/coefficients.html
         */
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
            case 6:
                m = weight *1.173;
                break;
            case 7:
                m = weight * 1.211;
                break;
            case 8:
                m = weight * 1.251;
                break;
            case 9:
                m = weight * 1.294;
                break;
            default:
                m = weight*1.341;
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
        Log.i("MAX", m + "");
    }

    private double getCalculatedMax(int lid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(maxWeight) "
                + "From Max "
                + "Where lid = " + lid + ""
                , null);
        c.moveToFirst();
        int max = c.getInt(0);
        return max;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_current, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.deleteCurrent:
                //Toast.makeText(this, "Deleted: " + adapter.getItem(info.position), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(sets.get(info.position));
        }
        return true;
    }
    private void deleteFromDatabase(Set set){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int sid = set.getSid();
        db.delete("Sets", "sid = " + sid, null);
        db.delete("Max","sid = "+sid,null);

        sets.remove(set);

        setsAdapter.notifyDataSetChanged();
    }


}
