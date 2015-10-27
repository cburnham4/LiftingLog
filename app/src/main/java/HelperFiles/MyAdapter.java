package HelperFiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tracker.lift_log.R;

public class MyAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> data;
    private static LayoutInflater inflater = null;

    public MyAdapter(Context context, ArrayList<String> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.item_set, null);
        TextView wReps = (TextView) vi.findViewById(R.id.weightReps);
        TextView dateView = (TextView) vi.findViewById(R.id.dateText);      
        wReps.setText(data.get(position)+"          ");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd          "); 
    	Date date = new Date();
        dateView.setText(dateFormat.format(date));
        return vi;
    }
}