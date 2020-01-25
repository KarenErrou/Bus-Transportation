package com.example.lbt.lbt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainInfo extends AppCompatActivity

{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

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
                        Intent intent2 = new Intent(MainInfo.this,MainTickets.class);
                        startActivityForResult(intent2,0);
                        break;
                    case R.id.nav_departures:
                        Intent intent4 = new Intent(MainInfo.this,DeparturesActivity.class);
                        startActivityForResult(intent4,0);
                        break;
                    case R.id.nav_map:
                        Intent intent3 = new Intent(MainInfo.this,MapsActivity.class);
                        startActivityForResult(intent3,0);
                        break;
                    case R.id.nav_info:
                        Intent intent5 = new Intent(MainInfo.this,MainInfo.class);
                        startActivityForResult(intent5,0);
                        break;
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Info");
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

}
