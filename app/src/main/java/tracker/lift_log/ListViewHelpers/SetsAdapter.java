package tracker.lift_log.ListViewHelpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tracker.lift_log.R;

/**
 * Created by cvburnha on 10/30/2015.
 */
public class SetsAdapter extends ArrayAdapter<Set> {

    private static class ViewHolder {
        TextView weight;
        TextView reps;
        TextView date;
    }

    public SetsAdapter(Context context, ArrayList<Set> sets) {
        super(context, R.layout.item_set, sets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Set set  = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_set, parent, false);
            viewHolder.weight = (TextView) convertView.findViewById(R.id.tv_itemweight);
            viewHolder.reps = (TextView) convertView.findViewById(R.id.tv_itemreps);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tv_itemdate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object

        viewHolder.weight.setText(set.getWeight()+"");
        viewHolder.reps.setText(set.getReps()+"");
        viewHolder.date.setText(set.getDate()+"");

        // Return the completed view to render on screen
        return convertView;
    }

}
