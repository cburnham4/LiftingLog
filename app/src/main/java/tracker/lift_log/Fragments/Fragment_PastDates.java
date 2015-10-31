package tracker.lift_log.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import HelperFiles.DateConverter;
import HelperFiles.Item;
import tracker.lift_log.LiftDatabase;
import tracker.lift_log.ListViewHelpers.PastCardViewAdapter;
import tracker.lift_log.ListViewHelpers.PastDay;
import tracker.lift_log.PastDates;
import tracker.lift_log.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PastDates extends Fragment {
    private RecyclerView rv_pastdates;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LiftDatabase dbHelper;
    private DateConverter dateConverter;

    private ArrayList<PastDay> pastDates;

    public Fragment_PastDates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_dates, container, false);

        pastDates = new ArrayList<>();

        dateConverter = new DateConverter();

        rv_pastdates = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rv_pastdates.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PastCardViewAdapter(pastDates);
        rv_pastdates.setAdapter(mAdapter);

        return view;
    }

    private void generateData(){
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

    }


}
