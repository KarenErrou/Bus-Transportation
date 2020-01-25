package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import java.sql.*;
import java.util.*;

public class UpdateForm extends AppCompatActivity {
    String type;
    String uORi;
    String myId;
    int sz = 0;
    int sz1 = 0;
    int sz2 = 0;
    TimePicker fhour;
    TimePicker tohour;
    ArrayList<String> list = new ArrayList<>();
    public String[] editTexts = {};
    private ArrayList<String> containTexts = new ArrayList<String>();
    String spinner = null;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayList<String> listfStation = new ArrayList<String>();
    private ArrayList<String> listToStation = new ArrayList<String>();
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapterfStation;
    public ArrayAdapter<String> adapterToStation;

    public void onCreate(Bundle savedInstanceState) {

        Button submit;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateform);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        type = myIntent.getStringExtra("type");
        uORi = myIntent.getStringExtra("uORi");
        myId = myIntent.getStringExtra("myId");
        LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayout);
        System.out.println("OOOOOOOOOOOOO1 " + type);
        if (type.equalsIgnoreCase("station")) {
            editTexts = new String[]{"name", "description", "longitude", "lat"};
            System.out.println("OOOOOOOOOOOOO2");
            for (int i = 0; i < editTexts.length; i++) {
                TextView t = new TextView(this);
                t.setText(editTexts[i]);
                rl.addView(t);
                EditText e = new EditText(this);
                e.setId(i);
                rl.addView(e);
            }
            TextView t = new TextView(this);
            t.setText("zoneid");
            rl.addView(t);
            Spinner spin = new Spinner(this);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listItems);
            spin.setAdapter(adapter);
            spin.setId(R.id.sp);
            rl.addView(spin);
        }
        else if (type.equalsIgnoreCase("bus")) {
            editTexts = new String[]{"busReg","busNbr", "nbrPassenger"};
            System.out.println("OOOOOOOO3");
            for (int i = 0; i < editTexts.length; i++) {
                TextView t = new TextView(this);
                t.setText(editTexts[i]);
                rl.addView(t);
                EditText e = new EditText(this);
                e.setId(i);
                rl.addView(e);
            }
            System.out.println("OOOOOOOO4");
            TextView t = new TextView(this);
            t.setText("busdriver");
            rl.addView(t);
            Spinner spin = new Spinner(this);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listItems);
            spin.setAdapter(adapter);
            System.out.println("OOOOOOOO5");
            spin.setId(R.id.sp);
            rl.addView(spin);
            System.out.println("OOOOOOOO6");
        }
        else if (type.equalsIgnoreCase("busSchedule")) {
            TextView t0 = new TextView(this);
            t0.setText("Price");
            rl.addView(t0);
            EditText e = new EditText(this);
            e.setId(0);
            rl.addView(e);
            TextView t = new TextView(this);
            t.setText("From Station");
            Spinner fStation = new Spinner(this);
            adapterfStation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listfStation);
            fStation.setAdapter(adapterfStation);
            fStation.setId(R.id.sp);
            TextView t1 = new TextView(this);
            t1.setText("To Station");
            Spinner toStation = new Spinner(this);
            adapterToStation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listToStation);
            toStation.setAdapter(adapterToStation);
            toStation.setId(R.id.sp1);
            if(uORi.equalsIgnoreCase("u")){
                fStation.setEnabled(false);
                toStation.setEnabled(false);
            }
            TextView t2 = new TextView(this);
            t2.setText("Bus Number");
            Spinner spin = new Spinner(this);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listItems);
            spin.setAdapter(adapter);
            spin.setId(R.id.sp2);
            TextView t3 = new TextView(this);
            t3.setText("From hour");
            fhour = new TimePicker(this);
            fhour.setIs24HourView(true);
            fhour.setId(R.id.sp3);
            TextView t4 = new TextView(this);
            t4.setText("To hour");
            tohour = new TimePicker(this);
            tohour.setIs24HourView(true);
            tohour.setId(R.id.sp4);
            rl.addView(t);
            rl.addView(fStation);
            rl.addView(t1);
            rl.addView(toStation);
            rl.addView(t2);
            rl.addView(spin);
            rl.addView(t3);
            rl.addView(fhour);
            rl.addView(t4);
            rl.addView(tohour);
        }
        submit = new Button(this);
        submit.setText("Submit");
        rl.addView(submit);


        BackTask db = new BackTask();
        db.execute();

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Spinner s2 = (Spinner) findViewById(R.id.sp);
                StringBuilder s = new StringBuilder();
                if(type.equalsIgnoreCase("busSchedule")){
                    Spinner s3 = (Spinner) findViewById(R.id.sp1);
                    Spinner busNbr = (Spinner) findViewById(R.id.sp2);
                    if(s2.getSelectedItem().toString().equalsIgnoreCase(s3.getSelectedItem().toString())){
                        Toast.makeText(UpdateForm.this, "From and To Station are similar", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        TimePicker fromhour = (TimePicker) findViewById(R.id.sp3);
                        TimePicker tohour = (TimePicker) findViewById(R.id.sp4);
                        if((fromhour.getHour() == tohour.getHour()) && (fromhour.getMinute() == tohour.getMinute())){
                            Toast.makeText(UpdateForm.this, "From and To Hour are similar", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(fromhour.getHour() == 24 || fromhour.getHour() == 1 || fromhour.getHour() == 2 || fromhour.getHour() == 3 || fromhour.getHour() == 4 || fromhour.getHour() == 5){
                                Toast.makeText(UpdateForm.this, "From hour is closed at that hour", Toast.LENGTH_SHORT).show();
                            }
                            else if(tohour.getHour() == 24 || tohour.getHour() == 1 || tohour.getHour() == 2 || tohour.getHour() == 3 || tohour.getHour() == 4 || tohour.getHour() == 5 || tohour.getHour() == 6){
                                Toast.makeText(UpdateForm.this, "To hour is closed at that hour", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                EditText t = (EditText) findViewById(0);
                                ArrayList<Integer> timelist = new ArrayList<>();
                                list.add(s2.getSelectedItem().toString());
                                list.add(s3.getSelectedItem().toString());
                                timelist.add(fromhour.getHour());
                                timelist.add(fromhour.getMinute());
                                timelist.add(tohour.getHour());
                                timelist.add(tohour.getMinute());
                                list.add(busNbr.getSelectedItem().toString());
                                list.add(t.getText().toString());
                                Intent myIntent = new Intent(UpdateForm.this, InsertChanges.class);
                                myIntent.putExtra("type", type);
                                myIntent.putExtra("uORi", uORi);
                                myIntent.putExtra("list", list);
                                myIntent.putExtra("timelist", timelist);
                                myIntent.putExtra("myId", myId);
                                System.out.println("PPPPPPPPPPPPPPPPPP0 " + list);
                                startActivity(myIntent);

                            }
                        }
                    }
                }
                else{
                    for (int j = 0; j < editTexts.length; j++) {
                        TextView tt = (TextView) findViewById(j);
                        if (tt.getText().length() == 0 || tt.getText().toString().matches("")) {
                            Toast.makeText(UpdateForm.this, "You did not enter all the information", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            list.add(tt.getText().toString());
                        }
                    }
                    if (list.size() == editTexts.length) {
                        list.add(s2.getSelectedItem().toString());
                        Intent myIntent = new Intent(UpdateForm.this, InsertChanges.class);
                        myIntent.putExtra("type", type);
                        myIntent.putExtra("uORi", uORi);
                        myIntent.putExtra("list", list);
                        myIntent.putExtra("myId", myId);
                        startActivity(myIntent);
                    }
                }
            }
        });
    }

    class BackTask extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;
        ArrayList<String> list1;
        ArrayList<String> list2;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int frhour =0 , frmins =0;
        int tohours = 0 , tomins =0;

        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
            list1 = new ArrayList<>();
            list2 = new ArrayList<>();
        }

        protected Void doInBackground(Void... params) {
            String result = "";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                if (type.equalsIgnoreCase("station")) {
                    String zoneSelected = "";
                    if (uORi.equalsIgnoreCase("u")) {
                        String sql = "SELECT name,description,longitude,lat,zoneid FROM station WHERE station_id =" + myId ;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        int zone = 0;
                        if (rs.next()) {
                            containTexts.add(rs.getString("name"));
                            containTexts.add(rs.getString("description"));
                            containTexts.add(rs.getString("longitude"));
                            containTexts.add(rs.getString("lat"));
                            zone = rs.getInt("zoneid");
                        }
                        String sql2 = "SELECT name FROM zone where zone_id = " + zone;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql2);
                        if (rs.next()) {
                            zoneSelected = rs.getString("name");
                        }
                    }
                    System.out.println("PPPP1");
                    String sql = "SELECT name FROM zone";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        String zoneName = rs.getString("name");
                        list2.add(zoneName);
                        System.out.println("OOOOOOOOOOOOOOOOO4 " + uORi  + " -> " + zoneSelected + zoneName);

                        if (zoneSelected.equalsIgnoreCase(zoneName)) {
                            System.out.println("OOOOOOOOOOOOOOOOO4 " + uORi  + " -> " + zoneSelected + zoneName);
                            sz = list.size() - 1;
                        }
                    }
                }
                else if (type.equalsIgnoreCase("bus")) {
                    String busdriver = "";
                    if (uORi.equalsIgnoreCase("u")) {
                        String sql = "SELECT regNbr,busNbr,busdriver,nbrPassenger FROM bus WHERE bus_id = " + myId ;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        int zone = 0;
                        if (rs.next()) {
                            containTexts.add(rs.getString("regNbr"));
                            containTexts.add(rs.getString("busNbr"));
                            containTexts.add(rs.getString("nbrPassenger"));
                            zone = rs.getInt("busdriver");
                        }
                        String sql2 = "SELECT fullname FROM employee where employee_id = " + zone;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql2);
                        if (rs.next()) {
                            busdriver = rs.getString("fullname");
                        }
                    }
                    String sql = "SELECT fullname FROM employee where position IN (select typeEmployee_id FROM typeofemployee where description='driver')";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        String zoneName = rs.getString("fullname");
                        list2.add(zoneName);
                        if (busdriver.equalsIgnoreCase(zoneName)) {
                            sz = list2.size() - 1;
                        }
                    }
                }
                else if (type.equalsIgnoreCase("busSchedule")) {
                    int fstation = 0;
                    int tostation = 0;
                    int busid = 0 ;

                    if (uORi.equalsIgnoreCase("u")) {
                        String sql = "SELECT fstation,tostation,busid,price,HOUR(fromhour) AS fhour ,MINUTE(fromhour) AS fmins,HOUR(tohour) AS tohour ,MINUTE(tohour) AS tomins FROM schedule WHERE schedule_id = " + myId ;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        if (rs.next()) {
                            EditText t = (EditText) findViewById(0);
                            t.setText(rs.getString("price"));
                            fstation = rs.getInt("fstation");
                            tostation = rs.getInt("tostation");
                            busid = rs.getInt("busid");
                            frhour = rs.getInt("fhour");
                            frmins = rs.getInt("fmins");
                            tohours = rs.getInt("tohour");
                            tomins = rs.getInt("tomins");
                        }
                    }
                    System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" + fstation + " " + tostation + " " + busid);
                    String sql = "SELECT station_id,name FROM station";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        String zoneName = rs.getString("name");
                        list.add(zoneName);
                        list1.add(zoneName);
                        if (fstation == rs.getInt("station_id")) {
                            sz = list.size() - 1;
                            list1.remove(list1.size()-1);
                        }
                        else if (tostation == rs.getInt("station_id")) {
                            sz1 = list1.size() - 1;
                            list.remove(list.size()-1);
                        }
                    }
                    String sql1 = "SELECT bus_id,busNbr FROM bus";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql1);
                    while (rs.next()) {
                        String zoneName = rs.getString("busNbr");
                        list2.add(zoneName);
                        if (busid == rs.getInt("bus_id")) {
                            sz2 = list2.size() - 1;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        protected void onPostExecute(Void result) {
            containTexts.addAll(containTexts);
            listfStation.addAll(list);
            listToStation.addAll(list1);
            listItems.addAll(list2);
            adapter.notifyDataSetChanged();
            if(type.equalsIgnoreCase("busSchedule") ) {
                adapterfStation.notifyDataSetChanged();
                adapterToStation.notifyDataSetChanged();
            }
            System.out.println("OOOPPPPPPPPP");
            if (uORi.equalsIgnoreCase("u")) {
                for (int i = 0; i < editTexts.length; i++) {
                    EditText e = (EditText) findViewById(i);
                    e.setText(containTexts.get(i));
                }
                Spinner s = (Spinner) findViewById(R.id.sp);
                s.setSelection(sz);

                if(type.equalsIgnoreCase("busSchedule") && uORi.equalsIgnoreCase("u")){
                    Spinner s1 = (Spinner) findViewById(R.id.sp1);
                    s1.setSelection(sz1);
                    Spinner s2 = (Spinner) findViewById(R.id.sp2);
                    s2.setSelection(sz2);
                    fhour.setHour(frhour);
                    fhour.setMinute(frmins);
                    tohour.setHour(tohours);
                    tohour.setMinute(tomins);
                }
            }
        }
    }
}
