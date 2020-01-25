package com.example.lbt.lbt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class ChooseTrip extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final long serialVersionUID = 1L;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private Button btn1,btn2;
    private Button continueButton;
    private DatePicker datePicker;
    private ArrayList<String> listItems= new  ArrayList<String> ();
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapter1;
    public ArrayAdapter<String> adapter2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trip);

        Intent intent = getIntent();

        final String category = getIntent().getStringExtra("category");


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.nav_Trips:
                        Intent intent2 = new Intent(ChooseTrip.this,MainTickets.class);
                        startActivityForResult(intent2,0);
                        break;
                    case R.id.nav_departures:
                        break;
                    case R.id.nav_map:
                        Intent intent3 = new Intent(ChooseTrip.this,MapsActivity.class);
                        startActivityForResult(intent3,0);
                        break;

                    case R.id.nav_info:
                        Intent intent5 = new Intent(ChooseTrip.this,MainInfo.class);
                        startActivityForResult(intent5,0);
                        break;
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ticket");
        btn1=(Button) findViewById(R.id.button);
        btn2=(Button)findViewById(R.id.button2);
        datePicker = (DatePicker) findViewById(R.id.dateOfTrip);
        final NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker);

        np.setMinValue(2);
        np.setMaxValue(5);
        int day = datePicker.getDayOfMonth();
        System.out.println("==========1======chooseTrip"+day);
        int month = datePicker.getMonth() + 1;
        System.out.println("=========2=======chooseTrip"+month);
        int year = datePicker.getYear();
        System.out.println("=========3=======chooseTrip"+year);


//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date d = new Date(year, month, day);
        final String dateTrip = year+"/"+month+"/"+day;

        continueButton = (Button) findViewById(R.id.continueButton);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseTrip.this,TripActivity.class);
                intent.putExtra("category",category);
                startActivityForResult(intent,0);
            }
        });




        OnStart();


        final Spinner spin=(Spinner)findViewById(R.id.spinner2);
        spin.setOnItemSelectedListener(this);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,  listItems);
        spin.setAdapter(adapter);



        final Spinner spin1=(Spinner)findViewById(R.id.spinner3);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup1);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(ChooseTrip.this, StatisticsActivity.class);
                //startActivityForResult(intent, 0);
                radioGroup.setVisibility(View.VISIBLE);
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                continueButton.setVisibility(View.VISIBLE);
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    RadioButton radio ;
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radio = (RadioButton) findViewById(selectedId);
                    LinearLayout Ll = (LinearLayout) findViewById(R.id.layout1);
                    LinearLayout L2 = (LinearLayout) findViewById(R.id.layout2);
                    if(radio.getText().equals("Daily")){
                        Ll.setVisibility(View.VISIBLE);
                        L2.setVisibility(View.GONE);
                    }else if (radio.getText().equals("Monthly")){
                        Ll.setVisibility(View.GONE);
                        L2.setVisibility(View.VISIBLE);
                    }

                }


            }
        });
        final EditText nbrZones= (EditText)findViewById(R.id.nbrZones) ;
        nbrZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin.setSelection(0);
                spin1.setVisibility(View.GONE);
                nbrZones.getText().clear();
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(ChooseTrip.this,StatisticsActivity.class);
//                startActivityForResult(intent,0);
                System.out.println("===========onclick"+nbrZones.getText());
                    RadioButton radio;
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radio = (RadioButton) findViewById(selectedId);
                    // find the radiobutton by returned id
                    Intent intent = new Intent(ChooseTrip.this, PaymentActivity.class);
                    intent.putExtra("type","zone");
                    intent.putExtra("radioZone", radio.getText());
                    intent.putExtra("category", category);
                    intent.putExtra("dateOfTrip", dateTrip);
                    if(radio.getText().equals("Daily")){
                        System.out.println("==========Daily=============");
                        intent.putExtra("trip", "Daily Zone");


                        System.out.println("================chooseTrip" + dateTrip);
                        intent.putExtra("firstZone", spin.getSelectedItem().toString());


                        System.out.println("=======================" + spin.getSelectedItem().toString());

                        if (spin1.getVisibility() == View.VISIBLE) {
                            intent.putExtra("secZone", spin1.getSelectedItem().toString());
                        }
                    }else if(radio.getText().equals("Monthly")){
                        System.out.println("==========Monthly=============");
                        np.getValue();
                        System.out.println("==========Monthly============="+np.getValue());
                        intent.putExtra("trip", "Monthly Zone");

                    }
                    startActivityForResult(intent, 0);
                }

        });


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                System.out.println("==========2==========" + item.toString());
                final EditText nbrZones= (EditText)findViewById(R.id.nbrZones) ;
                final String value=nbrZones.getText().toString();
                System.out.println("==========value==========" + value);
                if (value.equals("1")) {
                    System.out.println("==========1111==========");
                    System.out.println("==========Choose Zone done==========");
                } else if (value.equals("2")) {
                    System.out.println("==========2222==========");
                    spin1.setVisibility(View.VISIBLE);
                    spin1.setOnItemSelectedListener(ChooseTrip.this);
                    ArrayList<String> listItem2= new  ArrayList<String> ();
                    listItem2.addAll(listItems);
                    listItem2.remove(item);
                    adapter1 = new ArrayAdapter<String>(ChooseTrip.this, android.R.layout.simple_spinner_dropdown_item, listItem2);
                    spin1.setAdapter(adapter1);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }
    protected void OnStart(){
        System.out.println("-------------------------------1");
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }
    private class BackTask extends AsyncTask<Void,Void,Void>{

        ArrayList<String> list;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){
            System.out.println("-------------------------------2");
            InputStream in = null;
            String result="";
            try{
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                System.out.println("=======================connect====================");
                String sql = "SELECT name FROM zone";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    String zoneName = rs.getString("name");
                    System.out.println(zoneName + "\n");

                    list.add(zoneName);
                    System.out.println("-------------------------------3"+list.toString());
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            listItems.add("");
            listItems.addAll(list);
            adapter.notifyDataSetChanged();
            System.out.println("-------------------------------4"+list.toString());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
