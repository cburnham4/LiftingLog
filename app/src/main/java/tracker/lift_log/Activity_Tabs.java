package tracker.lift_log;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Activity_Tabs extends Activity{
    LiftDatabase dbHelper;
    //Ad variables
    //End Ad variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        dbHelper = new LiftDatabase(getBaseContext());
        Intent recievedIntent = getIntent();
        final int lid = recievedIntent.getIntExtra("LID", 0);
        String liftName = getLiftName(lid);

        TextView currentLift = (TextView)findViewById(R.id.currentLift);
        currentLift.setText("Current Lift: "+liftName);

        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

        TabSpec spec1 =tabHost.newTabSpec("tab1");
        Intent intent1 = new Intent(this, SetsActivity.class);
        intent1.putExtra("LID", lid);
        spec1.setContent(intent1);//
        spec1.setIndicator("Current Day");
        tabHost.addTab(spec1);

        TabSpec spec2 =tabHost.newTabSpec("tab2");
        Intent intent2 = new Intent(this, PastDates.class);
        intent2.putExtra("LID", lid);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec2.setContent(intent2);//
        spec2.setIndicator("Past Days");
        tabHost.addTab(spec2);

        TabSpec spec3 =tabHost.newTabSpec("tab3");
        Intent intent3 = new Intent(this, Activity_Graph.class);
        intent3.putExtra("LID", lid);
        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec3.setContent(intent3);//
        spec3.setIndicator("Progress Graph");
        tabHost.addTab(spec3);


    }

    private String getLiftName(int lid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"liftname"};
        Cursor c = db.query("Lifts", projection, "lid ="+ lid, null,  null,null,null);
        c.moveToFirst();
        if (c.isAfterLast() == false){
            return c.getString(0);
        }
        return "";
    }

}