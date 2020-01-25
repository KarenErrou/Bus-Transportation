package com.example.lbt.lbt;
import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import java.sql.*;
import java.util.*;

public class UpdateActivity extends AppCompatActivity {
    static final ArrayList<String> headline = new ArrayList<>();
    static final ArrayList<String> idList = new ArrayList<>();
    static final ArrayList<String> reportername = new ArrayList<>();
    static final ArrayList<String> date = new ArrayList<>();
    static ArrayList<String> updateId = new ArrayList<>();
    static String type;
    static ArrayList<NewItem> results = new ArrayList<NewItem>();
    static CustomListAdapter c;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatelist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        type = myIntent.getStringExtra("type");
        for(int i = 0 ; i< results.size();i++) {
            idList.remove(i);
            headline.remove(i);
            reportername.remove(i);
            date.remove(i);
        }
        for(int i = 0 ; i< results.size();i++) {
            results.remove(i);
        }
        final ListView lv1 = (ListView) findViewById(R.id.custom_list);
        c = new CustomListAdapter(this, results);
        lv1.setAdapter(c);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent myIntent = new Intent(UpdateActivity.this, UpdateForm.class);
                myIntent.putExtra("type", type);
                myIntent.putExtra("uORi", "u");
                myIntent.putExtra("myId" , idList.get(position));
                startActivity(myIntent);
            }
        });
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UpdateActivity.this, UpdateForm.class);
                myIntent.putExtra("type", type);
                myIntent.putExtra("uORi", "i");
                startActivity(myIntent);
            }
        });
        SelectDBUpdate db = new SelectDBUpdate();
        db.execute();
    }

    static class SelectDBUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
                try {
                    Connection conn;
                    Statement stmt = null;
                    ResultSet rs = null;
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                    if(type.equalsIgnoreCase("station")) {
                        String sql = "SELECT station_id,name,description,zoneid FROM station";
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            idList.add(rs.getString("station_id"));
                            headline.add(rs.getString("name"));
                            reportername.add("Description: " + rs.getString("description"));
                            int zone = rs.getInt("zoneid");
                            String sql1 = "SELECT name FROM zone where zone_id = '" + zone + "'";
                            Statement stmt1 = conn.createStatement();
                            ResultSet rs1 = stmt1.executeQuery(sql1);
                            if (rs1.next()) {
                                date.add("Zone name: " + rs1.getString("name"));
                            }
                        }
                    }
                    else if(type.equalsIgnoreCase("busSchedule")){
                        String sql = "SELECT schedule_id,fstation,tostation,fromhour,tohour,busid FROM schedule";
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        String fstation = "",toStation = "";
                        while (rs.next()) {
                            idList.add(rs.getString("schedule_id"));
                            String sql1 = "SELECT busNbr FROM bus where bus_id = " + rs.getString("busid") ;
                            Statement stmt1 = conn.createStatement();
                            ResultSet rs1 = stmt1.executeQuery(sql1);
                            if (rs1.next()) {
                                headline.add(rs1.getString("busNbr"));
                            }
                            String sql2 = "SELECT station_id,name FROM station " ;
                            Statement stmt2 = conn.createStatement();
                            ResultSet rs2 = stmt1.executeQuery(sql2);

                            while (rs2.next()) {
                                if(rs2.getString("station_id").equalsIgnoreCase(rs.getString("fstation")))
                                    fstation =rs2.getString("name");
                                else if(rs2.getString("station_id").equalsIgnoreCase(rs.getString("tostation")))
                                    toStation = rs2.getString("name");
                            }
                            reportername.add("From " + fstation + " to "  + toStation);
                            date.add("Hours from " + rs.getString("fromhour") + " to " + rs.getString("tohour"));
                        }
                    }
                    else if(type.equalsIgnoreCase("bus")) {
                        String sql = "SELECT bus_id,busNbr,busdriver,nbrPassenger FROM bus";
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            idList.add(rs.getString("bus_id"));
                            headline.add(rs.getString("busNbr"));
                            String sql1 = "SELECT fullname FROM employee where employee_id = " + rs.getString("busdriver") ;
                            Statement stmt1 = conn.createStatement();
                            ResultSet rs1 = stmt1.executeQuery(sql1);
                            if (rs1.next()) {
                                reportername.add("Bus Driver name: " + rs1.getString("fullname"));
                            }
                            Integer zone = (Integer) rs.getInt("nbrPassenger");
                            date.add("Number of passenger: " + zone.toString());
                        }
                    }
                    conn.close();
                } catch (Exception e) {
                    System.out.println("lOGINDB EXCEPTION" + e);
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < headline.size(); i++) {
                NewItem newsData = new NewItem();
                newsData.setHeadline(headline.get(i));
                newsData.setReporterName(reportername.get(i));
                newsData.setDate(date.get(i));
                results.add(newsData);
            }
            c.notifyDataSetChanged();
        }
    }
   }
