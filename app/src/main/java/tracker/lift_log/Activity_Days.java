package tracker.lift_log;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import HelperFiles.SQLQueryHelper;
import tracker.lift_log.CustomDialogs.EditDayLiftDialog;
import tracker.lift_log.ListViewHelpers.Day;
import tracker.lift_log.ListViewHelpers.DaysAdapter;

public class Activity_Days extends FragmentActivity {
    private ArrayList<Day> arrayOfDays;

	private LiftDatabase dbHelper; //The db helper that links to the sqlite db
    SQLiteDatabase writableDB; //Database

    private DaysAdapter daysAdapter; //adapter used for list view

    private Button btn_addDay;
    private ListView lv_days;
    TextView tv;
    SQLQueryHelper SQLHelper;

    private AdsHelper adsHelper;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_days_layout);

        adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.banner_ad_on_days),this);
        adsHelper.runAds();

        this.instantiateDBHelpers();

        //GET VIEWS
		lv_days = (ListView)findViewById(R.id.days);
        btn_addDay = (Button) findViewById(R.id.addLift);
        tv = (TextView) findViewById(R.id.inputLift);

        arrayOfDays = new ArrayList<>();

        this.getDaysFromDataBase();

        Log.e("SIZE OF ARRAY", "+ " + arrayOfDays.size());
        daysAdapter = new DaysAdapter(this, arrayOfDays);

        lv_days.setAdapter(daysAdapter);

        btn_addDay.setOnClickListener(onSave);
        registerForContextMenu(lv_days);
        
        lv_days.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(Activity_Days.this, Activity_Lifts.class);//CHANGE TO
                //adapter.notifyDataSetChanged();
                Day day = daysAdapter.getItem(position);
                //int did = getDid(name);
                int did = day.getDid();
                String name = day.getDay();
                intent.putExtra("ITEM_CLICKED", did);
                intent.putExtra("DAY_NAME", name);
                startActivity(intent);
            }
        });


    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        public void onClick(View v) {
            String newDay = tv.getText().toString();
            if (!newDay.isEmpty()){
                ContentValues values = new ContentValues();
                values.put("day", newDay);
                writableDB.insert("Days", null, values);

                arrayOfDays.add(new Day(SQLHelper.getLastDid(), newDay));
                daysAdapter.notifyDataSetChanged();

                tv.setText("");
            }

        }
    };

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
                displayDialog(daysAdapter.getItem(info.position));
                Toast.makeText(this, "Edit : " + daysAdapter.getItem(info.position).getDay(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "Deleted: " + daysAdapter.getItem(info.position).getDay(), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(daysAdapter.getItem(info.position));
                break;
        }
        return true;
    }

    private void displayDialog(Day day){
        EditDayLiftDialog editDayLiftDialog = new EditDayLiftDialog();
        final Day d = day;
        editDayLiftDialog.setName(day.getDay());

        editDayLiftDialog.setCallback(new EditDayLiftDialog.EditDayLiftListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, String newName) {
                updateDB(d, newName);
            }
        });

        editDayLiftDialog.show(this.getSupportFragmentManager(), "Edit_Day");
    }
    private void updateDB(Day day, String newName){
        ContentValues newValues = new ContentValues();
        newValues.put("day", newName);
        writableDB.update("Days", newValues, "did= " + day.getDid(), null);
        day.setDay(newName);
        daysAdapter.notifyDataSetChanged();
    }

    private void deleteFromDatabase(Day day){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int did = day.getDid();
        /* Delete from db where did */
        db.delete("Days", "did = " + did, null);
        db.delete("Lifts", "did = " + did, null);

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
        dbHelper = new LiftDatabase(getBaseContext());
        SQLHelper = new SQLQueryHelper(getBaseContext());
        writableDB = dbHelper.getWritableDatabase();
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
