package com.example.lbt.lbt;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SchedulesListAdapter  extends BaseAdapter{
    private Context mContext;
    private List<Schedules> mScheduleList;

    public SchedulesListAdapter(Context mContext, List<Schedules> mScheduleList) {
        this.mContext = mContext;
        this.mScheduleList = mScheduleList;
    }

    @Override
    public int getCount() {
        return mScheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(mContext,R.layout.schedules_list_view,null);
        TextView price  = (TextView) v.findViewById(R.id.price);
        TextView time  = (TextView) v.findViewById(R.id.time);
        TextView stations  = (TextView) v.findViewById(R.id.stations);
        TextView bus  = (TextView) v.findViewById(R.id.bus);

        price.setText(mScheduleList.get(position).getPrice() + " L.L ");
        time.setText(mScheduleList.get(position).getFromTime() + " - " + mScheduleList.get(position).getToTime()
                // +" ( " + mScheduleList.get(position).getTimeCalc() + " ) "
        );
        stations.setText(mScheduleList.get(position).getFromStation() + " --> " + mScheduleList.get(position).getToStation());
        bus.setText(mScheduleList.get(position).getBusNbr());

        v.setTag(mScheduleList.get(position).getPrice());

        return v;
    }


}
