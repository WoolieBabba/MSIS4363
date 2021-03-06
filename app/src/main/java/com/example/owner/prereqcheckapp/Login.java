package com.example.owner.prereqcheckapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Login extends AppCompatActivity {

    // Declaring layout button, edit texts
    public Button run;
    public TextView message;
    public String u,p;
    public Integer sID;

    // Declaring connection variables
    public Connection con;
    // Declaring ArrayLists
    public ArrayList<Integer> arrayStudentID = new ArrayList<Integer>();
    public ArrayList<String> arrayUsers = new ArrayList<String>();
    public ArrayList<String> arrayPass = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


// Getting values from button, texts and progress bar
        run = (Button) findViewById(R.id.btnLogin);
       // progressBar = (ProgressBar) findViewById(R.id.progressBar);


        run.setOnClickListener(new View.OnClickListener()
                {
@Override
public void onClick(View v)
        {
        CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
        checkLogin.execute("");
        }
        });
        //End Setting up the function when button login is clicked
        }

public class CheckLogin extends AsyncTask<String, String, String>
{
    String z = "";
    Boolean isSuccess = false;

    EditText userName1 = (EditText) findViewById(R.id.userName);
    EditText password1 = (EditText) findViewById(R.id.password);

    String user1 = userName1.getText().toString().toLowerCase();
    String pass = password1.getText().toString();




   protected void onPreExecute()
    {


    }

    @Override
    protected void onPostExecute(String r) {

        // progressBar.setVisibility(View.GONE);
        // Toast.makeText(Login.this, r, Toast.LENGTH_LONG).show();

        //if isSuccess was true (i.e. username found and password matches), intent to course list activity
        if (isSuccess) {
            Intent goToCourseSelection = new Intent(getApplicationContext(), CourseSelection.class);
            goToCourseSelection.putExtra("sID", sID);
            startActivity(goToCourseSelection);
        }
        //otherwise, toast the user why login was unsuccessful
        else {
            Toast.makeText(getApplicationContext(), z, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected String doInBackground(String... params)
    {

        try
        {
            con = connectionclass();        // Connect to database
            if (con == null)
            {
                z = "Check Your Internet Access!";
            }
            else
            {
                if (!user1.equals("")) {
                    String query = "select StudentID, Password from students where Username = '" + user1 + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        if(pass.equals(rs.getString("Password"))){
                            sID = rs.getInt("StudentID");
                            z = "login successful";
                            isSuccess = true;
                            con.close();
                        }

                    } else {
                        z = "Username " + user1 + " not found.";
                        isSuccess = false;
                    }
                }
                else {
                    z = "Username cannot be blank.";
                    isSuccess = false;
                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = ex.getMessage();

            Log.d ("sql error", z);
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

    //Registration button functionality will take you to the registration page
    public void btnClickRegister(View v){

        Intent goToRegisterPage = new Intent(getApplicationContext(), Register.class);
        startActivity(goToRegisterPage);

    }

}

