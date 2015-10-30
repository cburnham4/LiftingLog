package tracker.lift_log;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import HelperFiles.SQLQueryHelper;

import tracker.lift_log.CustomDialogs.AddLiftDialog;
import tracker.lift_log.CustomDialogs.EditDayLiftDialog;
import tracker.lift_log.ListViewHelpers.Lift;
import tracker.lift_log.ListViewHelpers.LiftsAdapter;

public class Activity_Lifts extends AppCompatActivity {
    //INSTANCE VARIABLES
    private ArrayList<Lift> arrayOfLifts;

    private SQLQueryHelper SQLHelper;
	private LiftDatabase dbHelper;
    private SQLiteDatabase writableDB;

    private LiftsAdapter liftAdapter;
    private ListView lv_lifts;

    private AdsHelper adsHelper;
    private int did;
    private TextView inputLift;
    
    private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lifts_days_layout);
        //GET DID
		Intent recievedIntent = getIntent();
		this.did = recievedIntent.getIntExtra("ITEM_CLICKED", 0);

        this.setUpToolbar();

        adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.banner_ad_on_lifts),this);
        adsHelper.runAds();

        this.setTitle("Muscle Group: "+recievedIntent.getStringExtra("DAY_NAME"));
        //TextView textView = (TextView) findViewById(R.id.liftName);
        //textView.setText();

        arrayOfLifts = new ArrayList<Lift>();
        
        //lifts  =new ArrayList<String>();
		lv_lifts = (ListView)findViewById(R.id.lv_days_lifts);

        // Gets the data repository in write mode
        this.instantiateDBHelpers();

        this.getLiftsFromDataBase(did);

        liftAdapter = new LiftsAdapter(this, arrayOfLifts);

		lv_lifts.setAdapter(liftAdapter);

        
        lv_lifts.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(Activity_Lifts.this, Progress.class);
                Lift lift = liftAdapter.getItem(position);
                intent.putExtra("LID", lift.getLid());
                startActivity(intent);
            }
        });

        registerForContextMenu(lv_lifts);
        
	}
    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Lifts.this, Activity_Days.class);
                startActivity(intent);
            }
        });
        //toolbar.setLogo(R.drawable.icon114);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_days_lifts, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit:
                displayDialog(liftAdapter.getItem(info.position));
                Toast.makeText(this, "Edit: " + liftAdapter.getItem(info.position).getLift(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "Deleted: " + liftAdapter.getItem(info.position).getLift(), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(liftAdapter.getItem(info.position));
        }
        return true;
    }

    private void displayDialog(Lift lift){
        EditDayLiftDialog editDayLiftDialog = new EditDayLiftDialog();
        final Lift l = lift;
        editDayLiftDialog.setName(l.getLift());

        editDayLiftDialog.setCallback(new EditDayLiftDialog.EditDayLiftListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, String newName) {
                updateDB(l, newName);
            }
        });

        editDayLiftDialog.show(this.getSupportFragmentManager(), "Edit_Lift");
    }

    private void updateDB(Lift lift, String newName){
        ContentValues newValues = new ContentValues();
        newValues.put("liftname", newName);
        writableDB.update("Lifts", newValues, "lid= " + lift.getLid(), null);
        lift.setLift(newName);
        liftAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tb_day_lift, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                AddLiftDialog addDayLiftDialog = new AddLiftDialog();
                addDayLiftDialog.setCallback(new AddLiftDialog.AddLiftListener() {
                    @Override
                    public void onDialogPositiveClick(String newName) {
                        if (!newName.isEmpty()){
                /* Insert the new day into the database */
                            ContentValues values = new ContentValues();
                            values.put("liftname", newName);
                            values.put("did", did);
                            writableDB.insert("Lifts", null, values);
                            arrayOfLifts.add(new Lift(SQLHelper.getLastLid(), did, newName));
                            liftAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No text was provided", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                addDayLiftDialog.show(this.getSupportFragmentManager(), "Add_Muscle");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteFromDatabase(Lift lift){
        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        int lid = lift.getLid();
        writableDB.delete("Lifts","lid = "+lid,null);
        arrayOfLifts.remove(lift);
        liftAdapter.notifyDataSetChanged();
    }
	
	
	private void getLiftsFromDataBase(int did){
		//lifts = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {"lid","liftname"};
		Cursor c = db.query("Lifts", projection, "did= " + did, null, null, null, null);
		c.moveToFirst();

		while (c!= null && c.isAfterLast() == false) {
            arrayOfLifts.add(new Lift(c.getInt(0), this.did, c.getString(1)));
			c.moveToNext();
		}
	}


    private void instantiateDBHelpers(){
        dbHelper = new LiftDatabase(getBaseContext());
        writableDB = dbHelper.getWritableDatabase();
        SQLHelper = new SQLQueryHelper(getBaseContext());
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
