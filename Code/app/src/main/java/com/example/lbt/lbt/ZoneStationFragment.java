package com.example.lbt.lbt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ZoneStationFragment extends Fragment {
    private static final String TAG = "ZoneStationFragment";
    public static int count = 0;
    public static PieChart pieChart;
    public static ArrayList<PieEntry> pieEntries = new ArrayList<>();
    public View onCreateView(LayoutInflater inflater , ViewGroup container,  Bundle saveInstanceState){
        View view =inflater.inflate(R.layout.zone_station_fragment,container,false);


        super.onCreate(saveInstanceState);
        final String spinValue = getArguments().getString("spinValueToFrag");
        final String fromDate = getArguments().getString("fromDateToFrag");
        final String toDate = getArguments().getString("toDateToFrag");

        System.out.println("==============" + spinValue + "==============" + fromDate + "============" + toDate);

        pieChart= (PieChart) view.findViewById(R.id.chart1);
        OnStart();

        System.out.println("-------------------------------1");
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setDrawCenterText(true);
        pieChart.setDrawEntryLabels(true);

        // pieEntries.add(new PieEntry(2,"Student"));
        //pieEntries.add(new PieEntry(2,"Adult"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Data Set1");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);
        return view;
    }

    protected void OnStart(){
        System.out.println("-------------------------------1");
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }
    private class BackTask extends AsyncTask<Void,Void,Void> {

        ArrayList<PieEntry> pie;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        protected void onPreExecute(){
            super.onPreExecute();
            pie=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){
            System.out.println("-------------------------------2");
            if(count==0) {
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                    System.out.println("=======================connect====================");

                    String spinValue = getArguments().getString("spinValueToFrag");
                    String fromDate = getArguments().getString("fromDateToFrag");
                    String toDate = getArguments().getString("toDateToFrag");

                    System.out.println("=======1=======" + spinValue + "========1======" + fromDate + "===1=========" + toDate);
                    String subsql="select t.station id" +
                            "             ,c.name stationName" +
                            "             ,(select name from zone z where z.zone_id = c.zoneid) zoneName" +
                            "             ,sum(t.TotalPrice) sumPrice " +
                            "             ,count(t.ticket_id) countTicket" +
                            "        from ticket t ,station c"  +
                            "            where t.date between '"+ fromDate +"' and '"+ toDate +"'" +
                            "                 and t.station=c.station_id" +
                            "                      group by t.station";
                    String sql = "select D.zoneName zoneName " +
                            "           ,sum(D.countTicket) countTicket " +
                            "           ,sum(D.sumPrice) sumPrice " +
                            "from" +
                            "(" +
                            subsql+
                            " )D" +
                            "               group by D.zoneName ";

                    System.out.println(sql);
                    System.out.println("=======================1====================");
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        System.out.println("=======================2====================");
                        String catName = rs.getString("zoneName");
                        System.out.println(catName + "\n");
                        if(spinValue.equals("Number of Tickets Sold")) {
                            int countTicket = rs.getInt("countTicket");
                            System.out.println(countTicket + "\n");
                            pie.add(new PieEntry(countTicket,catName));
                        }else if(spinValue.equals("Revenue")) {
                            int price = rs.getInt("sumPrice");
                            pie.add(new PieEntry(price,catName));
                        }

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               count=1;
           }
            return null;
        }
        protected void onPostExecute(Void result){
            pieEntries.addAll(pie);
            pieChart.notifyDataSetChanged();
            System.out.println("-------------------------------4"+pieEntries.toString());


        }

    }
}
