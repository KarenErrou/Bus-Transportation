package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity  extends Activity {
    public String [] items = {"Credit","Master"};
    public ArrayAdapter<String> adapter;
    public String trip ;
    public String category;
    public EditText paymentAmount;
    String total;
    public Spinner spin;
    public boolean isInsert = false;
    public String secZone;
    public  EditText paymentDate;
    public EditText cardNbr;
    public DatePicker cardExpDate;
    public String dateOfTrip;
    public  String firstZone;
    public ArrayList<String> zones = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        Intent intent = getIntent();
        String type = getIntent().getStringExtra("type");
        if(type.equalsIgnoreCase("zone")) {
            final String radioZone = getIntent().getStringExtra("radioZone");
            category = getIntent().getStringExtra("category");

            trip = getIntent().getStringExtra("trip");
            dateOfTrip = getIntent().getStringExtra("dateOfTrip");

            firstZone = getIntent().getStringExtra("firstZone");

            System.out.println("=================" + dateOfTrip);

            zones.add(firstZone);


            if (intent.hasExtra("secZone")) {
                secZone = getIntent().getStringExtra("secZone");
                zones.add(secZone);
            }



        paymentAmount= (EditText)findViewById(R.id.amount) ;
        cardNbr = (EditText)findViewById(R.id.cardNbr);
        cardExpDate = (DatePicker) findViewById(R.id.cardExpDate);
        paymentDate = (EditText)findViewById(R.id.dateOfTrip);

        spin=(Spinner)findViewById(R.id.spin);
        final Button buy = (Button) findViewById(R.id.buyTicket) ;
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,  items);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);

                //Monthly
                if(radioZone.equals("Monthly")){
                    paymentDate.setEnabled(true);

                }
                //Daily
                if(radioZone.equals("Daily")){
                    paymentDate.setText(dateOfTrip);
                    paymentDate.setEnabled(false);
                }


                isInsert = false;
                OnStart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInsert = true;
                OnStart();
            }
        });
        }else if(type.equalsIgnoreCase("trip")){

            ArrayList<List> schedules = (ArrayList)getIntent().getSerializableExtra("Scehdules");
            System.out.println("DONEEEE"   + schedules);
        }
    }
    protected void OnStart(){

        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }
    private class BackTask extends AsyncTask<Void,Void,Void> {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        protected void onPreExecute(){
            super.onPreExecute();

        }
        protected Void doInBackground(Void...params){
            System.out.println("-------------------------------2");

            try{
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                System.out.println("=======================connect====================");
                String sqlGetPricefromTrip = "SELECT price FROM trip where name ='" + trip + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlGetPricefromTrip);
                int priceTrip = 0;
                while (rs.next()) {
                    priceTrip = rs.getInt("price");
                    System.out.println(priceTrip + "\n");

                }
                String sqlGetPricefromCat = "SELECT price FROM category where name = '" + category +"'";
                stmt1 = conn.createStatement();
                rs1 = stmt1.executeQuery(sqlGetPricefromCat);
                int priceCat =0;
                while (rs1.next()) {
                    priceCat = rs1.getInt("price");
                    System.out.println(priceCat + "\n");

                }


                double totalPay = priceCat + priceTrip;
                Intent intent = getIntent();
                if(intent.hasExtra("secZone")){
                    System.out.println("================cynthia");
                    totalPay=totalPay * 2;
                }
                //APPLY DISCOUNT ON MONTHLY//////////////////////////////////////////////////////////////////////////////////
                total = ""+totalPay;
                System.out.println("================totalPay"+total );
                System.out.println("================totalPay"+totalPay );

                if(isInsert == true){

                    String insertPayment;
                    int day = cardExpDate.getDayOfMonth();
                    int month = cardExpDate.getMonth()+1;
                    int year = cardExpDate.getYear();

                    StringBuffer sbToDate = new StringBuffer();
                    sbToDate.append(year);sbToDate.append('/');
                    sbToDate.append(month);sbToDate.append('/');
                    sbToDate.append(day);
                    String description = spin.getSelectedItem().toString();
                    if(trip.equals("Monthly")) {
                        insertPayment = "insert into payment (paymentMethod,payment_date,paymentAmount,card_Nbr,card_exp_date)"
                                + "select d.paymentMethod_id ,'" + paymentDate.getText() + "' , " + totalPay  * 0.85 + "," + cardNbr.getText() + ",'"+sbToDate.toString()+"'"
                                + " from paymentmethod d where d.description='" + description + "'";
                    }else{
                        insertPayment = "insert into payment (paymentMethod,payment_date,paymentAmount,card_Nbr,card_exp_date)"
                                + "select d.paymentMethod_id ,'" + paymentDate.getText() + "' , " + totalPay + "," + cardNbr.getText() + ",'"+sbToDate.toString()+"'"
                                + " from paymentmethod d where d.description='" + description + "'";
                    }
                    PreparedStatement statement = conn.prepareStatement(insertPayment,Statement.RETURN_GENERATED_KEYS);
                    statement.executeUpdate();
                    rs = statement.getGeneratedKeys();
                    int paymentId = 0;
                    if(rs != null && rs.next()){
                        paymentId = rs.getInt(1);
                        System.out.println("Generated  Id: "+rs.getInt(1));
                    }
                    System.out.println("===========insertPayment============"+paymentId);
                    String insertTicket = null;
                    Statement stmtInsert = conn.createStatement();
                    if(trip.equals("Monthly")) {
                        for (int i = 0; i < zones.size(); i++) {
                            insertTicket = "insert into ticket (client,zone,category,Trip,TotalPrice,payment,date) " +
                                    "select 1,z.zone_id,c.category  _id,t.trip_id," + totalPay + "," + paymentId + ",'" + dateOfTrip + "' " +
                                    "from zone z ,category c,trip t where z.name = '" + zones.get(i) + "' and c.name = '" + category + "' and t.name = '" + trip + "' ";

                        }
                    }else{
                        insertTicket = "insert into ticket (client,category,Trip,TotalPrice,payment,date) " +
                                "select 1,c.category_id,t.trip_id," + totalPay + "," + paymentId + ",'" + dateOfTrip + "' " +
                                "from category c,trip t where c.name = '" + category + "' and t.name = '" + trip + "' ";

                    }
                    System.out.println("=====================insertTicket" + insertTicket);
                    finish();
                    stmtInsert.executeUpdate(insertTicket);
//                    ResultSet rs = statement.getGeneratedKeys();
//                    if (rs.next()) {
//                        int newId = rs.getInt(1);
//                        oid.setId(newId);
//                    }
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

            paymentAmount.setText(total);
            paymentAmount.setEnabled(false);
        }
    }
//    public static Date addDays(Date date, int days)
//    {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.add(Calendar.DATE, days); //minus number would decrement the days
//        return cal.getTime();
//    }
}
