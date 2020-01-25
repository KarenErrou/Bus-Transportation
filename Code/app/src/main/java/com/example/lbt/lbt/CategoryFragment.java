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

public class CategoryFragment extends Fragment {
    private static final String TAG = "CategoryFragment";
    public static int count = 0;

    public static PieChart pieChart;
    public static  ArrayList<PieEntry> pieEntries = new ArrayList<>();
    public static PieEntry pieEntry;

    public View onCreateView(LayoutInflater inflater ,  ViewGroup container, Bundle saveInstanceState){
        final View view =inflater.inflate(R.layout.category_fragment,container,false);
        super.onCreate(saveInstanceState);


        final String spinValue = getArguments().getString("spinValueToFrag");
        final String fromDate = getArguments().getString("fromDateToFrag");
        final String toDate = getArguments().getString("toDateToFrag");

        pieChart= (PieChart) view.findViewById(R.id.chart1);
        pieChart.setVisibility(View.VISIBLE);

        OnStart();

        pieChart.setDrawCenterText(true);
        pieChart.setDrawEntryLabels(true);
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, "Data Set1");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);

//        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//        @Override
//        public void onValueSelected(Entry e, Highlight h) {
//            PieEntry pe = (PieEntry) e;
//            pieChart.setVisibility(View.GONE);
//            changeFragment(pe.getLabel(),spinValue,fromDate,toDate);
//        }
//        @Override
//        public void onNothingSelected() {}
//    });
        return view;
}

//    public void changeFragment(String label,String spinValue,String fromDate,String toDate){
//        CategoryFragmentExtended fragment = new CategoryFragmentExtended();
//
//        Bundle arguments = new Bundle();
//        arguments.putString( "label" , label);
//        arguments.putString( "spinValue" , spinValue);
//        arguments.putString( "fromDate" , fromDate);
//        arguments.putString( "toDate" , toDate);
//
//        fragment.setArguments(arguments);
//
//        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.main, fragment , "CategoryFragmentExtended");
//        ft.commit();
//
//    }
    protected void OnStart(){
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
                if(count==0) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");

                        String spinValue = getArguments().getString("spinValueToFrag");
                        String fromDate = getArguments().getString("fromDateToFrag");
                        String toDate = getArguments().getString("toDateToFrag");

                        String sql = "select  t.category id " +
                            "      ,(select name from category c where t.category=c.category_id) name " +
                            "      ,sum(t.TotalPrice) sumPrice " +
                            "      ,count(t.ticket_id) countTicket" +
                            "    from ticket t " +
                            "      where t.date between '"+ fromDate +"' and '"+ toDate +"'" +
                            "        group by t.category ";

                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            String catName = rs.getString("name");

                            if(spinValue.equals("Number of Tickets Sold")) {
                                int countTicket = rs.getInt("countTicket");
                                pieEntry = new PieEntry(countTicket,catName);
                                pie.add(pieEntry);
                            }else if(spinValue.equals("Revenue")) {
                                int price = rs.getInt("sumPrice");
                                pieEntry = new PieEntry(price,catName);
                                pie.add(pieEntry);
                            }

                        }
                    conn.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                count=1;
                }
            return null;
        }
        protected void onPostExecute(Void result){
            System.out.println("----------------------"+pie.toString());

            System.out.println("----------------------"+pieEntries.toString());

                pieEntries.addAll(pie);
                pieChart.notifyDataSetChanged();


        }

    }
}
