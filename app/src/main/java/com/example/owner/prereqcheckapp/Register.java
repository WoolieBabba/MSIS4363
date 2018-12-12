package com.example.owner.prereqcheckapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class Register extends AppCompatActivity {
    // Declaring layout button, edit texts
    public Button run;
    public TextView message;
    ArrayList<String> userList;
    public boolean doesStudentIDExist = false;
    // End Declaring layout button, edit texts
    // Declaring connection variables
    public Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Getting values from button, texts and progress bar
        run = (Button) findViewById(R.id.btnregisterStudent);


        run.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InsertRegister insertRegister = new InsertRegister();// this is the Asynctask, which is used to process in background to reduce load on app process
                insertRegister.execute("");
            }
        });
        //End Setting up the function when button login is clicked
    }

    public class InsertRegister extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        TextView usernameLabel = (TextView) findViewById(R.id.usernameLabel);
        EditText enteredFirst = (EditText) findViewById(R.id.firstName);
        EditText enteredLast = (EditText) findViewById(R.id.lastName);
        EditText enteredUser = (EditText) findViewById(R.id.userNameR);
        EditText enteredPass = (EditText) findViewById(R.id.passwordR);

        //String sID = enteredStudentID.getText().toString();
        String fname = enteredFirst.getText().toString();
        String lname = enteredLast.getText().toString();
        String user = enteredUser.getText().toString();
        String pass = enteredPass.getText().toString();

        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                Intent goToLogin = new Intent(getApplicationContext(), Login.class);
                startActivity(goToLogin);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            // userList = new ArrayList<String>();
            //Intent goToRegisterPage = new Intent(getApplicationContext(), Login.class);
            //startActivity(goToRegisterPage);

            try {
                con = connectionclass();        // Connect to database
                if (con == null) {
                    z = "Check Your Internet Access!";
                }
                else {

                    String query3 = "Select Username from students where Username = '" + user + "'";
                    Statement stmt3 = con.createStatement();
                    ResultSet rs = stmt3.executeQuery(query3);
                    //if there is something in resultset, that username already exists.
                    if (rs.next()) {
                        // Let user know that the username is unavailable
                        usernameLabel.setTextColor(Color.RED);
                        usernameLabel.setText("USERNAME TAKEN");
                    }
                    else {
                        Log.i("registernewuser", "username free");
                        usernameLabel.setTextColor(Color.parseColor("#f17a0a"));
                        usernameLabel.setText("USERNAME");
                        // Insert the user into the database

                        String insertStudentQuery = "Insert into students(Username, Password, FirstName, LastName) values ('" + user + "', '" + pass + "', '" + fname + "', '" + lname + "');";
                        Statement stmt2 = con.createStatement();
                        stmt2.executeUpdate(insertStudentQuery);
                        Log.i("register studentcreated", "student created");

                        //once new user inserted, need to create corresponding record in studentplan table
                        String selectNewStudentID = "select StudentID from students where Username = '" + user + "'";
                        Statement seNeStID = con.createStatement();
                        ResultSet rs3 = seNeStID.executeQuery(selectNewStudentID);
                        if(rs3.next()) {
                            Log.i("register newStudentID", String.valueOf(rs3.getInt("StudentID")));
                            String createPlanForNewStudent = "insert into studentplan(StudentID) values (" +
                                    String.valueOf(rs3.getInt("StudentID")) + ");";
                            Statement crplfonest = con.createStatement();
                            crplfonest.executeUpdate(createPlanForNewStudent);
                            isSuccess = true;
                            z = "account created successfully";
                        }
                        con.close();
                    }
                }
            }
            catch(Exception ex)
            {
                isSuccess = false;
                z = ex.getMessage();

                Log.d("sql error", z);
            }
            Log.i("registerz", z);
            return z;
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionclass()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //your database connection string goes below
            ConnectionURL = "jdbc:jtds:sqlserver://msis4363.database.windows.net:1433;databasename=prereq_check;user=teammb@msis4363;password=cwtiyxWgRLH5;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}