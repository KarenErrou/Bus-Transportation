package com.example.lbt.lbt;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class CustomUpdateList extends BaseAdapter {
        private ArrayList<NewItem> listData;
        private LayoutInflater layoutInflater;

        public CustomUpdateList(Context aContext, ArrayList<NewItem> listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(aContext);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.update, null);
                holder = new ViewHolder();
                holder.headlineView = (TextView) convertView.findViewById(R.id.title);
                holder.reporterNameView = (TextView) convertView.findViewById(R.id.reporter);
                holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.headlineView.setText(listData.get(position).getHeadline());
            holder.reporterNameView.setText("By, " + listData.get(position).getReporterName());
            holder.reportedDateView.setText(listData.get(position).getDate());
            return convertView;
        }

        static class ViewHolder {
            TextView headlineView;
            TextView reporterNameView;
            TextView reportedDateView;
        }
    }
