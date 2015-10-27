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
 * Created by cvburnha on 10/26/2015.
 */
public class LiftsAdapter extends ArrayAdapter<Lift> {

    private static class ViewHolder {
        TextView lift;
    }

    public LiftsAdapter(Context context, ArrayList<Lift> lifts) {
        super(context, R.layout.item_day_lift, lifts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Lift lift  = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_day_lift, parent, false);
            viewHolder.lift = (TextView) convertView.findViewById(R.id.tv_day_lift_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.lift.setText(lift.getLift());
        // Return the completed view to render on screen
        return convertView;
    }

}
