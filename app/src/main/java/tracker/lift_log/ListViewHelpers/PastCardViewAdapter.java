package tracker.lift_log.ListViewHelpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tracker.lift_log.R;

/**
 * Created by cvburnha on 10/31/2015.
 */
public class PastCardViewAdapter extends RecyclerView.Adapter<PastCardViewAdapter.ViewHolder> {
    public String[] mDataset;



    // Provide a suitable constructor (depends on the kind of dataset)
    public PastCardViewAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PastCardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_pastdate, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.tvtinfo_text.setText(mDataset[position].toString());
        viewHolder.tv_date.setText("HELLO");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvtinfo_text;
        public TextView tv_date;

        public ViewHolder(View view) {
            super(view);
            tvtinfo_text = (TextView) view.findViewById(R.id.info_text);
            tv_date = (TextView) view.findViewById(R.id.tv_cardview_date);

        }
    }

}
