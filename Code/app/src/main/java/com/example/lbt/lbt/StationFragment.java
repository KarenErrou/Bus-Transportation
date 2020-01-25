package com.example.lbt.lbt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class StationFragment extends Fragment {
    private static final String TAG = "StationFragment";
    public static int count = 0;

    public static HorizontalBarChart barChart ;
    public static ArrayList<BarEntry> barEntries ;
    public static BarEntry barEntry;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        final View view = inflater.inflate(R.layout.trips_fragment_extended, container, false);
        super.onCreate(saveInstanceState);
        System.out.println("===============christopher====1==");
        Bundle arguments = getArguments();
        String label = arguments.getString("label");
        System.out.println("===============christopher====1=="+label);
        final String fromDate = arguments.getString("fromDate");
        System.out.println("===============christopher====1=="+fromDate);
        final String toDate = arguments.getString("toDate");
        System.out.println("===============christopher====1=="+toDate);

        final String spinValue = arguments.getString("spinValue");
        System.out.println("===============christopher====1=="+spinValue);

        barChart = (HorizontalBarChart) view.findViewById(R.id.chart);

        OnStart();


//        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                System.out.println("===============christopher====2=="+spinValue);
//                PieEntry pe = (PieEntry) e;
//                barChart.setVisibility(View.GONE);
//                System.out.println("===============christopher====2 label=="+pe.getLabel());
//                changeFragment(pe.getLabel(),spinValue,fromDate,toDate);
//            }
//            @Override
//            public void onNothingSelected() {}
//        });

        return view;
    }

    public void changeFragment(String label,String spinValue,String fromDate,String toDate) {
        StationFragmentExtended fragment = new StationFragmentExtended();
        System.out.println("===============christopher====3");
        Bundle arguments = new Bundle();
        arguments.putString("label", label);
        arguments.putString("spinValue", spinValue);
        arguments.putString("fromDate", fromDate);
        arguments.putString("toDate", toDate);

        fragment.setArguments(arguments);

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main4, fragment, "StationFragmentExtended");
        ft.commit();
    }
        protected void OnStart(){
        System.out.println("-------------------------------1");
        super.onStart();
            System.out.println("-------------------------------1111");
        SecondChart bt=new SecondChart();
        bt.execute();
    }

    private class SecondChart extends AsyncTask<Void,Void,Void> {

        ArrayList<BarEntry> bar;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        protected void onPreExecute(){
            super.onPreExecute();

        }
        protected Void doInBackground(Void...params){
            if(count==0) {
                System.out.println("-------------------------------2");
                try {
                    System.out.println("-------------------------------3as");
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    System.out.println("-------------------------------asasa2");
                    conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                    System.out.println("-------------------------------wewewe");

                    Bundle arguments = getArguments();
                    System.out.println("-------------------------------sasas");
                    String label = arguments.getString("label");
                    System.out.println("-------------------------------33"+label);

                    String fromDate = arguments.getString("fromDate");
                    String toDate = arguments.getString("toDate");
                    String spinValue = arguments.getString("spinValue");
                    System.out.println("-------------------------------33");

                    String sql="select t.station id  " +
                            "       ,c.name stationName " +
                            "       ,z.name zoneName  " +
                            "       ,sum(t.TotalPrice) totalPrice " +
                            "       ,count(t.ticket_id) countTicket" +
                            "    from ticket t ,station c,zone z" +
                            "       where t.date between '" + fromDate + "' and '" + toDate + "'" +
                            "    and z.zone_id = c.zoneid and t.station=c.station_id " +
                            "      and z.name = '" + label + "'" +
                            "       group by t.station ";

                    System.out.println(sql);
                    System.out.println("-------------------------------44");
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    barEntries=new ArrayList<>();
                    int i=1;
                    while (rs.next()) {

                        String stationName = rs.getString("stationName");
                        System.out.println(stationName + "\n");
                        //labels.add(stationName);
                        if(spinValue.equals("Number of Tickets Sold")) {

                            int countTicket = rs.getInt("countTicket");
                            System.out.println(countTicket + "\n");
                            System.out.println("--------counter------------"+i + "\n");
                            //barEntry = new BarEntry(countTicket,i);
                            barEntries.add(new BarEntry(countTicket,i));
                            i++;
                            System.out.println("-------------------------------4"+barEntries.toString());
                        }else if(spinValue.equals("Revenue")) {
                            int price = rs.getInt("totalPrice");
                           // barEntry = new BarEntry(price,i);
                            barEntries.add(new BarEntry(price,i));
                            i++;
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
            BarDataSet dataset = new BarDataSet(barEntries, "date set 1");
            BarData data = new BarData(dataset);
            barChart.setData(data);
            System.out.println("----------------sdwedewe---------------4"+barEntries.toString());


        }

    }
}
