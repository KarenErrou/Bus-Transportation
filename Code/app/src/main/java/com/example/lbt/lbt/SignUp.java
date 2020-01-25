package com.example.lbt.lbt;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUp extends Activity{

        protected void onCreate(android.os.Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
            setContentView(R.layout.signup);

        }
    private void updateLabel() {
        String myFormat = "dd-MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText edittext= (EditText) findViewById(R.id.date);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
        public void date(View v){
            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        public void signUp(android.view.View v){
            EditText firstname = (EditText) findViewById(R.id.firstname);
            EditText lastname = (EditText) findViewById(R.id.lastname);
            EditText username = (EditText) findViewById(R.id.username);
            EditText emaill = (EditText) findViewById(R.id.email);
            EditText password = (EditText) findViewById(R.id.password);
            RadioGroup r = (RadioGroup) findViewById(R.id.gender);
            EditText edittext= (EditText) findViewById(R.id.date);

            if( edittext.getText().length() == 0  || emaill.getText().length()==0 || firstname.getText().length() == 0 || lastname.getText().length() == 0 || username.getText().length() == 0 || password.getText().length() == 0 ){
                Toast.makeText(getApplicationContext(), "Please all the fields are required", Toast.LENGTH_SHORT).show();
            }

            else{
               SelectDBUpdate db = new SelectDBUpdate();
                db.execute();
            }
           }

    class SelectDBUpdate extends AsyncTask<Void, Void, Void> {
        boolean error;
        @Override
        protected void onPreExecute() {
             error = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            EditText firstname = (EditText) findViewById(R.id.firstname);
            EditText lastname = (EditText) findViewById(R.id.lastname);
            EditText username = (EditText) findViewById(R.id.username);
            EditText emaill = (EditText) findViewById(R.id.email);
            EditText password = (EditText) findViewById(R.id.password);
            RadioButton male = (RadioButton) findViewById(R.id.male);
            EditText edittext= (EditText) findViewById(R.id.date);

            String gender;
            if (male.isSelected()) {
                gender = "male";
            } else {
                gender = "female";
            }
            try {
                Connection conn;
                Statement stmt = null;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                String sql2 = "SELECT * FROM employee where username= '" + username.getText().toString() + "'";
                stmt = conn.createStatement();
                ResultSet rs1 = stmt.executeQuery(sql2);
                if (rs1.next()) {
                }
                else{
                    String sql3 = "SELECT * FROM client where username= '" + username.getText().toString() + "'";
                    stmt = conn.createStatement();
                    ResultSet rs3 = stmt.executeQuery(sql3);
                    if (rs3.next()) {
                    }
                    else{
                        String sql = "INSERT INTO client(firstname, lastname, username,password,email, gender, dateOfBirth) VALUES ('" + firstname.getText().toString() + "','" + lastname.getText().toString() + "','" + username.getText().toString() + "','" + password.getText().toString() + "','" + emaill.getText().toString() + "','" + gender + "','" + edittext.getText().toString() + "')";
                        stmt = conn.createStatement();
                        System.out.println("KKKKKKKKKKK " + sql);
                        int rs = stmt.executeUpdate(sql);
                        error = false;
                    }
                }
            } catch (Exception e) {
                System.out.println("lOGINDB EXCEPTION" + e);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(error){
                Toast.makeText(SignUp.this,"Username already exist",Toast.LENGTH_LONG).show();
            }
            else{
                finish();
            }
            super.onPostExecute(aVoid);
        }
    }
}
