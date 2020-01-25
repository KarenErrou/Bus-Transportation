package com.example.lbt.lbt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class TripsFragmentExtended extends Fragment {
    private static final String TAG = "TripsFragmentExtended";
    public static LineChart lineChart;
    public static int count = 0;
    public static ArrayList<Entry> lineEntries = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        final View view = inflater.inflate(R.layout.trips_fragment_extended, container, false);
        super.onCreate(saveInstanceState);

        Bundle arguments = getArguments();
        String label = arguments.getString("label");
        String fromDate = arguments.getString("fromDate");
        String toDate = arguments.getString("toDate");
        String spinValue = arguments.getString("spinValue");

        lineChart = (LineChart) view.findViewById(R.id.chart1);
        OnStart();

        return view;
    }
    protected void OnStart(){
        System.out.println("-------------------------------1");
        super.onStart();
        SecondChart bt=new SecondChart();
        bt.execute();
    }

    private class SecondChart extends AsyncTask<Void,Void,Void> {

        ArrayList<Entry> line;
        ArrayList<String> labels;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        protected void onPreExecute(){
            super.onPreExecute();
            line=new ArrayList<>();
            labels=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){
            if(count==0) {
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");

                    Bundle arguments = getArguments();
                    String label = arguments.getString("label");
                    String fromDate = arguments.getString("fromDate");
                    String toDate = arguments.getString("toDate");
                    String spinValue = arguments.getString("spinValue");
                    String sql="select t.Trip id " +
                            "       ,tr.name name " +
                            "       ,count(t.ticket_id) count " +
                            "       ,sum(t.TotalPrice) totalPrice " +
                            "       ,t.date dateTicket" +
                            "    from ticket t ,trip tr" +
                            "       where t.date between '" + fromDate + "' and '" + toDate + "'" +
                            "    and t.Trip=tr.trip_id " +
                            "      and tr.name='" + label + "'" +
                            "       group by t.date ";

                    System.out.println(sql);

                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);

                    int i=1;
                    while (rs.next()) {

                        String date = rs.getString("dateTicket");
                        System.out.println(date + "\n");
                        labels.add(date);
                        if(spinValue.equals("Number of Tickets Sold")) {

                            int countTicket = rs.getInt("count");
                            System.out.println(countTicket + "\n");
                            System.out.println("--------counter------------"+i + "\n");
                            line.add(new Entry(i,countTicket));

                            i++;
                        }else if(spinValue.equals("Revenue")) {
                            int price = rs.getInt("totalPrice");
                            line.add(new Entry(i,price));
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
            lineEntries.addAll(line);
            final LineDataSet lineDataSet = new LineDataSet(lineEntries, "Data Set1");

            LineData lineData = new LineData();
            if (!lineEntries.isEmpty()) {
                lineData.addDataSet(lineDataSet);
            }

//            System.out.println("-------------------------------4"+labels.toString());
//            XAxis xAxis = lineChart.getXAxis();
//            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//            xAxis.setDrawGridLines(false);
//            xAxis.setValueFormatter(new IAxisValueFormatter() {
//                @Override
//                public String getFormattedValue(float value, AxisBase axis) {
//                    return labels.get((int)value);
//                }
//            });
            lineChart.setData(lineData);

            System.out.println("-------------------------------4"+lineEntries.toString());

            lineChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    lineChart.setVisibility(getView().GONE);
//
//                    CategoryFragment fragment = new CategoryFragment();
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.main, fragment , "CategoryFragment");
//                    ft.commit();
                }
            });
        }

    }
}
