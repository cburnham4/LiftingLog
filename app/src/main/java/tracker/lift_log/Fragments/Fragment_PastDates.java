package tracker.lift_log.Fragments;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tracker.lift_log.ListViewHelpers.PastCardViewAdapter;
import tracker.lift_log.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PastDates extends Fragment {
    private RecyclerView rv_pastdates;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public Fragment_PastDates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_dates, container, false);


        String[] myDataset = { "Alpha", "Beta", "CupCake", "Donut", "Eclair",
                "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwitch",
                "JellyBean", "KitKat", "LollyPop" };

        rv_pastdates = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // getSupportActionBar().setIcon(R.drawable.ic_launcher);

        // getSupportActionBar().setTitle("Android Versions");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv_pastdates.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rv_pastdates.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PastCardViewAdapter(myDataset);
        rv_pastdates.setAdapter(mAdapter);

        return view;
    }


}
