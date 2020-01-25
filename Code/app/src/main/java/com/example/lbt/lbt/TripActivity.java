package com.example.lbt.lbt;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.content.ContentValues.TAG;

public class TripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    public static TextView result;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private final static int TIME_PICKER_INTERVAL = 5;
    int hour;
    int min;
    TimePicker timePicker ;

    String category;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip);
        Intent intent = getIntent();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Single Zone");
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);

        category= getIntent().getStringExtra("category");

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;
                searchPlace();
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                searchPlace();
            }
        });
        timePicker= (TimePicker)findViewById(R.id.time);


    }


    public void searchPlace() {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("LB")
                    .build();
            Intent intent =  new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(autocompleteFilter).build(TripActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    double sourceLat;
    double sourceLong;
    double destLat;
    double destLong;
    int idSource;
    int idDestination;
    String sl;
    String dl;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText to = (EditText) findViewById(R.id.to);
        EditText from = (EditText) findViewById(R.id.from);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(TripActivity.this, data);
                Log.i(TAG, "Place: " + place.getName());
                if (PLACE_AUTOCOMPLETE_REQUEST_CODE == 1) {
                    from.setText(place.getName());
                    String x = String.valueOf(place.getLatLng());
                    String[] latlong = x.substring(10, x.length() - 1).split(",");
                    sourceLat = Double.parseDouble(latlong[0]);
                    sl = String.valueOf(sourceLat).substring(0, 2);
                    sourceLong = Double.parseDouble(latlong[1]);

                } else {
                    to.setText(place.getName());
                    String x = String.valueOf(place.getLatLng());
                    String[] latlong = x.substring(10, x.length() - 1).split(",");
                    destLat = Double.parseDouble(latlong[0]);
                    dl = String.valueOf(destLat).substring(0, 2);
                    destLong = Double.parseDouble(latlong[1]);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(TripActivity.this, data);

                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.

            }
        }else if(requestCode == 5){
            switch(resultCode){
                case RESULT_OK:
                    Toast.makeText(TripActivity.this,"No Path Available",Toast.LENGTH_LONG).show();
            }
        }
        Button load = (Button) findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LOLOLO");
                SelectDBUpdate db = new SelectDBUpdate();
                db.execute();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    class SelectDBUpdate extends AsyncTask<Void, Void, Void> {
        @Override

        protected Void doInBackground(Void... voids) {

            try {
                String sql="";
                double minSource = 1000000000000000000000.0;
                double minDestination = 1000000000000000000000.0;
                Connection conn;
                Statement stmt = null;
                ResultSet rs = null;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                stmt = conn.createStatement();
                if (sourceLat > destLat){
                    sql = "SELECT * from station where name NOT LIKE '%2' ";
                }
                else if(sourceLat < destLat) {
                    sql = "SELECT * from station where name NOT LIKE '%1'";
                }
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    if(Integer.parseInt(String.valueOf(rs.getInt("lat")).substring(0, 2)) == Integer.parseInt(sl) || Integer.parseInt(String.valueOf(rs.getInt("lat")).substring(0, 2)) == Integer.parseInt(dl)){
                        double x = Double.parseDouble(getDistance(sourceLat,sourceLong,rs.getDouble("lat"),rs.getDouble("longitude"),"driving"));
                        double y = Double.parseDouble(getDistance(destLat,destLong,rs.getDouble("lat"),rs.getDouble("longitude"),"driving"));
                        if( x < minSource){
                            minSource = x;
                            idSource = rs.getInt("station_id");
                        }
                        if(y < minDestination){
                            minDestination = y;
                            idDestination = rs.getInt("station_id");
                        }
                    }
                }
                TextView t = (TextView) findViewById(R.id.text);
                t.setText(idSource + " > " + idDestination);

                conn.close();
            } catch (Exception e) {
                System.out.println("lOGINDB EXCEPTION" + e);
            }
            return null;
        }


        @Override

        protected void onPostExecute(Void aVoid) {
            int count1=0;
            hour = timePicker.getCurrentHour();
            min = timePicker.getCurrentMinute();

            String time = hour + ":" +min;
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");

            try {
                Intent intent = new Intent(TripActivity.this, DijkstraAlgorithmTest.class);
                intent.putExtra("idSource",idSource);
                intent.putExtra("idDestination",idDestination);
                intent.putExtra("timeTrip",time);
                intent.putExtra("category",category);
                CheckBox checkBox = (CheckBox)findViewById(R.id.direct);
                if(checkBox.isChecked()){
                    intent.putExtra("direct","yes");
                }else{
                    intent.putExtra("direct","no");
                }
                startActivityForResult(intent,5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    int count =0;
    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2,final String leg){

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