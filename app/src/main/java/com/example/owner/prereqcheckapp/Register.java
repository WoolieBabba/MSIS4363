package com.example.owner.prereqcheckapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
        EditText enteredStudentID = (EditText) findViewById(R.id.studentID);
        EditText enteredFirst = (EditText) findViewById(R.id.firstName);
        EditText enteredLast = (EditText) findViewById(R.id.lastName);
        EditText enteredUser = (EditText) findViewById(R.id.userNameR);
        EditText enteredPass = (EditText) findViewById(R.id.passwordR);

        String sID = enteredStudentID.getText().toString();
        String fname = enteredFirst.getText().toString();
        String lname = enteredLast.getText().toString();
        String user = enteredUser.getText().toString();
        String pass = enteredPass.getText().toString();

        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

            Toast.makeText(getApplicationContext(), "You have successfully registered!", Toast.LENGTH_SHORT).show();
          //  if (isSuccess) {

                Intent goToLogin = new Intent(getApplicationContext(), Login.class);
                startActivity(goToLogin);
           // }
        }

        @Override
        protected String doInBackground(String... params) {

             userList = new ArrayList<String>();

            try {
                con = connectionclass();        // Connect to database
                if (con == null) {
                    z = "Check Your Internet Access!";
                } else {
                    String query = "Select StudentID from student";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        //sets the userList Array to all StudentID from the student table
                        userList.add(rs.getString("StudentID"));

                        //Check if a person with the same username exists
                        for (String i : userList) {
                            if (sID.equals(i)) {
                                doesStudentIDExist = true;
                            }

                        }
                        if (!doesStudentIDExist) {
                            // Change below query according to your own database.
                            String query2 = "Insert into student(StudentID, Username, Password, Firstname, Lastname) ";
                            query2 += " values('" + sID + "','" + user + "', '" + pass + "', '" + fname + "', '" + lname + "')";
                            Statement stmt2 = con.createStatement();
                            ResultSet rs2 = stmt2.executeQuery(query2);

                            if (rs2.next()) {

                                //sID = rs.getString("StudentID");
                                // user = rs.getString("Username");
                                // pass = rs.getString("Password");
                                // fname = rs.getString("Firstname");
                                // lname = rs.getString("Lastname");

                                z = "query successful";
                                isSuccess = true;
                                con.close();

                            } else {
                                z = "Invalid Query!";
                                isSuccess = false;
                            }
                        }
                        else{
                            doesStudentIDExist = false;
                            Toast.makeText(getApplicationContext(), "An account with this Student ID already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            catch(Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();

                    Log.d("sql error", z);
                }

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
