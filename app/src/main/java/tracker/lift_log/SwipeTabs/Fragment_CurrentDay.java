package tracker.lift_log.SwipeTabs;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tracker.lift_log.Database.SQLQueryHelper;
import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.ListViewHelpers.Set;
import tracker.lift_log.ListViewHelpers.SetsAdapter;
import tracker.lift_log.R;


public class Fragment_CurrentDay extends Fragment {
    private LiftDatabaseHelper dbHelper;
    private SQLQueryHelper sqlQueryHelper;
    private SQLiteDatabase writableDB;
    private ArrayList<Set> sets;

    private TextView tv_max;

    private ListView lv_sets;
    private SetsAdapter setsAdapter;

    private AdsHelper adsHelper;
    private int lid;

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
        View view = inflater.inflate(R.layout.fragment_current_day, container, false);

        adsHelper = new AdsHelper(view, getResources().getString(R.string.banner_ad_on_days),this.getActivity());
        //adsHelper.runAds();

        dbHelper = new LiftDatabaseHelper(getContext());
        writableDB = dbHelper.getWritableDatabase();
        sqlQueryHelper = new SQLQueryHelper(getContext());

        lv_sets =(ListView) view.findViewById(R.id.setsList);



        loadCurrentDay(lid);

        setsAdapter = new SetsAdapter(this.getContext(), sets);
        lv_sets.setAdapter(setsAdapter);

        tv_max = (TextView) view.findViewById(R.id.max);
        tv_max.setText("Calculated Max: " + getCalculatedMax(lid));

        Button addSet =(Button) view.findViewById(R.id.AddSet);
        Button addRep = (Button) view.findViewById(R.id.addRep);
        Button subRep = (Button) view.findViewById(R.id.subRep);
        Button subWeight = (Button) view.findViewById(R.id.subWeight);
        final Button addWeight = (Button) view.findViewById(R.id.addWeight);
        final EditText repCount = (EditText) view.findViewById(R.id.Reps);
        final EditText weightCount = (EditText) view.findViewById(R.id.Weight);


        addRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep+1 +"");
            }
        });
        subRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep - 1 + "");
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


                    setCalculatedMAX(sid, lid, weight, reps);
                    tv_max.setText("Calculated Max: " + getCalculatedMax(lid));
                    setsAdapter.notifyDataSetChanged();
                }
            }
        });
        registerForContextMenu(lv_sets);
        return view;
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
        getActivity().getMenuInflater().inflate(R.menu.menu_current, menu);
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

        tv_max.setText("Calculated Max: "+getCalculatedMax(lid));
        setsAdapter.notifyDataSetChanged();
    }




}
