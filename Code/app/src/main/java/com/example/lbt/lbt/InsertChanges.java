package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.support.annotation.Nullable;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.xml.transform.Result;

public class InsertChanges extends Activity {
    String type;
    String uORi;
    String myId;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Integer> timelist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("UUUUUUUUUUUUUUUU");
        Intent myIntent = getIntent();
        type = myIntent.getStringExtra("type");
        uORi = myIntent.getStringExtra("uORi");
        list = myIntent.getStringArrayListExtra("list");
        myId = myIntent.getStringExtra("myId");
        System.out.println("UUUUUUUUUUUUUUUU1");
        if(type.equalsIgnoreCase("busSchedule")) {
            timelist = myIntent.getIntegerArrayListExtra("timelist");
        }
        SelectDBUpdate db = new SelectDBUpdate();
        db.execute();
    }

    class SelectDBUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection conn;
                Statement stmt = null;
                ResultSet rs = null;
                int z = 0;
                String sql = "";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                stmt = conn.createStatement();
                if(type.equalsIgnoreCase("station")) {
                    String sql1 = "Select zone_id from zone where name = '" + list.get(list.size() - 1) + "'";

                    rs = stmt.executeQuery(sql1);
                    if (rs.next()) {
                        z = rs.getInt("zone_id");
                    }

                    StringBuilder s = new StringBuilder();
                    if (uORi.equalsIgnoreCase("i")) {
                        s.append("INSERT INTO station (name,description,longitude,lat, zoneid, date_created) VALUES(");
                        for (int k = 0; k < 2; k++) {
                            s.append("'" + list.get(k) + "',");
                        }
                        for (int k = 2; k < list.size() - 1; k++) {
                            s.append(list.get(k) + ",");
                        }
                        s.append(z + ", sysdate())");
                    }
                    else{

                        s.append("UPDATE station SET name = '" + list.get(0) +"',description = '" + list.get(1) +"',longitude = "+ list.get(2) +" ,lat ="+ list.get(3)+", zoneid =");
                        s.append(z + ", date_updated = sysdate() where station_id = " + myId);

//                        String sql2 = "Select nid, source, destination from neighbourhood where source= " + myId + " or destination= " + myId;
//                        rs = stmt.executeQuery(sql2);
//                        while (rs.next()) {
//                            Statement stmt2 = null ;
//                            stmt2 = conn.createStatement();
//                            if(rs.getInt("source") == Integer.parseInt(myId)){
//                                System.out.println("YESYES");
//                                String sql3 = "Select longitude, lat from station where station_id= " + rs.getInt("destination") ;
//                                ResultSet rs1 = stmt2.executeQuery(sql3);
//                                System.out.println("YESYES1");
//                                if(rs1.next()){
//                                    System.out.println("YESYES2 " + list.get(3) + " " +  list.get(2));
//                                    double flat =  rs1.getDouble("lat");
//                                    double flong =  rs1.getDouble("longitude");
//                                    String querie = "UPDATE neighbourhood set weight = " + InsertChanges.getDistance(Double.parseDouble(list.get(3)),Double.parseDouble(list.get(2)),flat,flong,"driving") + " where nid = " + rs.getInt("nid") ;
//                                    System.out.println("YESYES3");
//                                    stmt.executeUpdate(querie);
//                                }
//                            }
//                            else if(rs.getInt("destination") == Integer.parseInt(myId)){
//                                String sql3 = "Select longitude, lat from station where station_id= " + rs.getInt("source") ;
//                                ResultSet rs1 = stmt2.executeQuery(sql3);
//                                if(rs1.next()){
//                                    double flat =  rs1.getDouble("lat");
//                                    double flong =  rs1.getDouble("longitude");
//                                    String querie = "UPDATE neighbourhood set weight = " + InsertChanges.getDistance(flat,flong,Double.parseDouble(list.get(3)),Double.parseDouble(list.get(2)),"driving") + "where destination = " + rs.getInt("destination") + "and source = " + rs.getInt("souce");
//                                    stmt.executeUpdate(querie);
//                                }
//
//                            }
//
//                        }
                        sql = s.toString();
                    }
                }else if(type.equalsIgnoreCase("busSchedule")) {
                    int fstation =0,tostation =0,busid =0,flat=0,flong=0,tolat=0,tolong=0;
                    System.out.println("PPPPPPPPPPPPPPPPPPPP2 " + list );
                    String sql1 = "Select * from station where name = '" + list.get(0) + "'";
                    rs = stmt.executeQuery(sql1);
                    if (rs.next()) {
                        fstation = rs.getInt("station_id");
                        flat = rs.getInt("lat");
                        flong = rs.getInt("longitude");
                    }
                    String sql2 = "Select * from station where name = '" + list.get(1) + "'";
                    rs = stmt.executeQuery(sql2);
                    if (rs.next()) {
                        tostation = rs.getInt("station_id");
                        tolat = rs.getInt("lat");
                        tolong = rs.getInt("longitude");
                    }
                    String fhour = timelist.get(0).toString() + ":" +  timelist.get(1).toString() + ":00";
                    String tohour = timelist.get(2).toString() + ":" +  timelist.get(3).toString() + ":00";
                    String sql3 = "Select bus_id from bus where busNbr = '" + list.get(2) + "'";
                    rs = stmt.executeQuery(sql3);
                    if (rs.next()) {
                        busid = rs.getInt("bus_id");
                    }
                    StringBuilder s = new StringBuilder();
                    if(uORi.equalsIgnoreCase("i")) {
                        s.append("INSERT INTO schedule (fstation,tostation,fromhour,tohour, busid,price, date_created) VALUES(" + fstation + "," + tostation + ",'" + fhour + "','" + tohour + "',");
                        s.append(busid + "," + list.get(list.size() -1) + ", sysdate())");
                    }
                    else{
                        s.append("UPDATE schedule SET fstation = " + fstation + ",tostation = " + tostation + ",fromhour = '" + fhour + "',tohour = '" + tohour + "', busid= " + busid);
                        s.append(",price = " + list.get(list.size() -1) + " , date_updated = sysdate() WHERE schedule_id = " + myId );
                    }
                    sql = s.toString();
                    String sql6;
                    String sql5 = "Select nid from neighbourhood where source = " + fstation + " and destination = " + tostation;
                    rs = stmt.executeQuery(sql5);
                    if(!rs.next()){
                        sql6 = "INSERT INTO neighbourhood(source,destination,weight) VALUES ("+fstation + "," + tostation + ","  +  InsertChanges.getDistance(flat,flong,tolat,tolong,"driving") + ")";
                        int rs1 = stmt.executeUpdate(sql6);
                    }
                }
                else if(type.equalsIgnoreCase("bus")) {
                    String sql1 = "Select employee_id from employee where fullname = '" + list.get(list.size() - 1) + "'";
                    rs = stmt.executeQuery(sql1);
                    if (rs.next()) {
                        z = rs.getInt("employee_id");
                    }
                    StringBuilder s = new StringBuilder();

                    if(uORi.equalsIgnoreCase("i")) {
                        s.append("INSERT INTO bus (regNbr,busNbr,nbrPassenger,busdriver,date_created) VALUES(");
                        s.append("'" + list.get(0) + "',");
                        s.append(list.get(1) + "," +  list.get(2) + "," + z + ", sysdate() )");
                    }
                    else{
                        s.append("UPDATE bus SET regNbr = '" + list.get(0) + "' ,busNbr = " + list.get(1) + ",nbrPassenger = " + list.get(2) + ",busdriver = " + z + " , date_updated = sysdate() WHERE bus_id = " + myId  );
                    }
                    sql = s.toString();

                }
                stmt.executeUpdate(sql);
                conn.close();
            } catch (Exception e) {
                System.out.println("lOGINDB EXCEPTION" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent myIntent = new Intent(InsertChanges.this, UpdateActivity.class);
            myIntent.putExtra("type",type);
            startActivity(myIntent);
        }
    }

    static int count =0;
    public static String getDistance(final double lat1, final double lon1, final double lat2, final double lon2, final String leg){

        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=" + leg);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] =distance.getString("value");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if( parsedDistance[0] !=null) {
            count = 0;
            return parsedDistance[0];
        }
        else{
            return getDistance(lat1, lon1,  lat2, lon2, "walking");
        }
    }



}