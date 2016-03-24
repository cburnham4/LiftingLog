package tracker.lift_log.MainActivities;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.CustomDialogs.AddDayDialog;
import tracker.lift_log.CustomDialogs.EditDayLiftDialog;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.Database.SQLQueryHelper;
import tracker.lift_log.ListViewHelpers.Day;
import tracker.lift_log.ListViewHelpers.DaysAdapter;
import tracker.lift_log.R;

public class Activity_Days extends AppCompatActivity {
    private ArrayList<Day> arrayOfDays;

	private LiftDatabaseHelper dbHelper; //The db helper that links to the sqlite db
    SQLiteDatabase writableDB; //Database

    private DaysAdapter daysAdapter; //adapter used for list view

    private ListView lv_days;

    SQLQueryHelper SQLHelper;

    private AdsHelper adsHelper;

    private Toolbar toolbar;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifts_days_layout);


        this.setTitle("Muscle Groups");
        this.setUpToolbar();

        this.instantiateDBHelpers();
        this.findViews();

        arrayOfDays = new ArrayList<>();
        this.getDaysFromDataBase();
        daysAdapter = new DaysAdapter(this, arrayOfDays);
        lv_days.setAdapter(daysAdapter);

       // btn_addDay.setOnClickListener(onSave);
        registerForContextMenu(lv_days);
        
        lv_days.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Intent intent = new Intent(Activity_Days.this, Activity_Lifts.class);
                Day day = daysAdapter.getItem(position);
                int did = day.getDid();
                String name = day.getDay();
                intent.putExtra("ITEM_CLICKED", did);
                intent.putExtra("DAY_NAME", name);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                showAddItem();
            }
        });

        //adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.banner_ad_on_days),this);
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
    /**
     * Set up the context menu
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_days_lifts, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit:
                displayEditDialog(daysAdapter.getItem(info.position));
                Toast.makeText(this, "Edit : " + daysAdapter.getItem(info.position).getDay(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "Deleted: " + daysAdapter.getItem(info.position).getDay(), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(daysAdapter.getItem(info.position));
                break;
        }
        return true;
    }
    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.icon114);
    }

    public void showAddItem(){
        AddDayDialog addDayLiftDialog = new AddDayDialog();
        addDayLiftDialog.setCallback(new AddDayDialog.AddDayListener() {
            @Override
            public void onDialogPositiveClick(String newName) {
                if (!newName.isEmpty()){
                /* Insert the new day into the database */
                    ContentValues values = new ContentValues();
                    values.put("day", newName);
                    writableDB.insert("Days", null, values);

                /* Add the new day to the listview */
                    arrayOfDays.add(new Day(SQLHelper.getLastDid(), newName));
                    daysAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "No text was provided", Toast.LENGTH_SHORT).show();
                }

            }
        });
        addDayLiftDialog.show(this.getSupportFragmentManager(), "Add_Muscle");
    }

    private void displayEditDialog(Day day){
        EditDayLiftDialog editDayLiftDialog = new EditDayLiftDialog();
        final Day d = day;

        /* Set the name for the dialog to use */
        editDayLiftDialog.setName(day.getDay());

        /* Set the callback for when the user presses Finish */
        editDayLiftDialog.setCallback(new EditDayLiftDialog.EditDayLiftListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, String newName) {
                updateDB(d, newName);
            }
        });

        editDayLiftDialog.show(this.getSupportFragmentManager(), "Edit_Day");
    }

    private void updateDB(Day day, String newName){
        /*Update the day in the Database */
        ContentValues newValues = new ContentValues();
        newValues.put("day", newName);
        writableDB.update("Days", newValues, "did= " + day.getDid(), null);
        /*Update the day on the listview */
        day.setDay(newName);
        daysAdapter.notifyDataSetChanged();
    }

    private void deleteFromDatabase(Day day){
        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        int did = day.getDid();
        /* Delete from db where did */
        writableDB.delete("Days", "did = " + did, null);
        writableDB.delete("Lifts", "did = " + did, null);

        arrayOfDays.remove(day);
        daysAdapter.notifyDataSetChanged();
    }

	private void getDaysFromDataBase(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {"did","day"};
		Cursor c = db.query("Days",projection,null,null,null,null,null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
            arrayOfDays.add(new Day(c.getInt(0),c.getString(1)));
			c.moveToNext();
		}
	}

    private void instantiateDBHelpers(){
        dbHelper = new LiftDatabaseHelper(getBaseContext());
        SQLHelper = new SQLQueryHelper(getBaseContext());
        writableDB = dbHelper.getWritableDatabase();
    }

    private void findViews(){
        lv_days = (ListView)findViewById(R.id.lv_days_lifts);
    }

//    @Override
//    public void onPause() {
//        adsHelper.onPause();
//        super.onPause();
//    }
//
//    public void onResume(){
//        adsHelper.onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        adsHelper.onDestroy();
//        super.onDestroy();
//    }
}
