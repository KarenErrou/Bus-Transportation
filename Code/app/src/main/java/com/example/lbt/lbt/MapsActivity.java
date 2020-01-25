package com.example.lbt.lbt;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    public ArrayList<Float> latlong = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SelectDB db = new SelectDB();
        db.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(9.0f);
        mMap.setMaxZoomPreference(14.0f);
        // Add a marker in Sydney and move the camera
        LatLng lebanon = new LatLng(33.8547, 35.8623);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lebanon));
    }

    class SelectDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for(int i =0; i <latlong.size();i++)
            latlong.remove(i);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection conn;
                Statement stmt = null;
                ResultSet rs = null;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                String sql = "SELECT longitude,lat FROM station";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    latlong.add(rs.getFloat("lat"));
                    latlong.add(rs.getFloat("longitude"));
                }
                conn.close();
            } catch (Exception e) {
                System.out.println("lOGINDB EXCEPTION" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for(int i=0;i<latlong.size();i++){
                LatLng lebanon1 = new LatLng(latlong.get(i), latlong.get(++i));
                mMap.addMarker(new MarkerOptions().position(lebanon1).title("Marker"));
            }
        }
    }
}


