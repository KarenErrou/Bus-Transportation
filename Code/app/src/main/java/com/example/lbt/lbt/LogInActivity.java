package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LogInActivity extends Activity {

    Button btnLogin;
    TextView signup;
    EditText editUsername;
    EditText editPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editUsername.getText().toString() == " " ||  editUsername.getText().toString().length() == 0){
                    Toast.makeText(LogInActivity.this, "Enter username",
                            Toast.LENGTH_LONG).show();
                }
                else  if(editPassword.getText().toString() == " " || editPassword.getText().toString().length() == 0) {
                    Toast.makeText(LogInActivity.this, "Enter password",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    LoginDB.password = editPassword.getText().toString();
                    LoginDB.username = editUsername.getText().toString();
                    new LoginDB().execute();


                        Intent intent = new Intent(LogInActivity.this, LoginPage.class);
                        startActivityForResult(intent, 0);

                }
            }
        });

        signup = (TextView) findViewById(R.id.signUp);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUp.class);
                startActivityForResult(intent, 0);
            }
        });
    }
}

class LoginDB extends AsyncTask<Void, Void, Void> {
    static String username;
    static String password;
    static boolean exist = false;
    static String post ;
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Connection conn;
            Statement stmt = null;
            ResultSet rs = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
            String sql = "SELECT * FROM client where username='" + username + "' and password='" + password + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                exist = true;
                post = "client";
            }
            else{
                String sql2 = "SELECT * FROM employee where username='" + username + "' and password='" + password + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql2);
                if (rs.next()) {
                    exist = true;
                    post = "employee";
                }
            }
        } catch (Exception e) {
            System.out.println("lOGINDB EXCEPTION" + e);

        }
        return null;
    }
}
