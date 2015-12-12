package tracker.lift_log.SwipeTabs;


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

import tracker.lift_log.Helpers.DateConverter;
import tracker.lift_log.Helpers.AdsHelper;
import tracker.lift_log.Database.LiftDatabaseHelper;
import tracker.lift_log.ListViewHelpers.PastCardViewAdapter;
import tracker.lift_log.ListViewHelpers.PastDay;
import tracker.lift_log.ListViewHelpers.Set;
import tracker.lift_log.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PastDates extends Fragment {
    private RecyclerView rv_pastdates;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LiftDatabaseHelper dbHelper;
    private DateConverter dateConverter;

    private ArrayList<PastDay> pastDates;
    private ArrayList<String> dates;

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
        View view =  inflater.inflate(R.layout.fragment_past_dates, container, false);
        adsHelper = new AdsHelper(view, getResources().getString(R.string.banner_ad_on_days),this.getActivity());
        //adsHelper.runAds();
        dbHelper = new LiftDatabaseHelper(getContext());
        dateConverter = new DateConverter();
        this.getDates();
        this.generateData();


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
        pastDates = new ArrayList<>();
        for(int i=0; i <dates.size();i++){
            ArrayList<Set> sets = new ArrayList<>();

            String sql  = "SELECT weight, reps FROM Sets WHERE lid = +"+lid+ " and date_Created = '"+dates.get(i)+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            String pastLift ="";
            while (c.isAfterLast() == false) {
                sets.add(new Set(0, lid, c.getInt(0), c.getInt(1), dates.get(i)));
                c.moveToNext();
            }
            PastDay pastDay = new PastDay(sets, dateConverter.convertDateToText(dates.get(i)));

            pastDates.add(pastDay);

        }

    }

    public void getDates(){
        dates = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql  = "SELECT distinct date_Created FROM Sets WHERE lid = +"+lid+ " ORDER BY date_Created desc";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            dates.add(c.getString(0));
            c.moveToNext();
        }
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
