package com.example.lbt.lbt;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class LoginPage extends ListActivity {

    String[] itemsClient = {"Departures", "Ticket", "Map", "Info"};
    String[] itemsAdmin = { "Update Stations", "Update Bus Schedule", "Update Bus", "Check Statistics", "Add Employee"};
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        Intent intent = getIntent();

        if (LoginDB.post.equalsIgnoreCase("client")) {
            items = itemsClient;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, items);
            setListAdapter(adapter);
        } else if (LoginDB.post.equalsIgnoreCase("employee")) {
            items = itemsAdmin;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, items);
            setListAdapter(adapter);
        }

    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        if (items == itemsClient) {
            if (position == 0) {
                Intent intent = new Intent(this,DeparturesActivity.class);
                startActivityForResult(intent,0);
            }
            else if(position == 1){
                Intent intent = new Intent(this,MainTickets.class);
                startActivityForResult(intent,0);
            }
            else if(position == 2){
                Intent intent = new Intent(this,MapsActivity.class);
                startActivityForResult(intent,0);
            }
            else if(position == 3){
                Intent intent = new Intent(this,MainInfo.class);
                startActivityForResult(intent,0);
            }

        }
        else if(items == itemsAdmin){
            if (position == 0) {
                //update stations
                Intent myIntent = new Intent(this, UpdateActivity.class);
                myIntent.putExtra("type","station");
                startActivity(myIntent);
            }
            else if(position == 1){
                //update Bus Schedule
                Intent myIntent = new Intent(this, UpdateActivity.class);
                myIntent.putExtra("type","busSchedule");
                startActivity(myIntent);
            }
            else if(position == 2){
                //update bus
                Intent myIntent = new Intent(this, UpdateActivity.class);
                myIntent.putExtra("type","bus");
                startActivity(myIntent);
            }
            else if(position == 3){
                Intent intent = new Intent(this,StatisticsActivity.class);
                startActivityForResult(intent,0);
            }
            else if(position == 4){

            }
        }

    }
}
