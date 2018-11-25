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

public class CourseDetails extends AppCompatActivity {
    // Declaring layout button, edit texts
    public Button run;
    public TextView message;
    // End Declaring layout button, edit texts
    // Declaring connection variables
    public Connection con;
    public String b = "";
    public String c = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        //Display CourseID, Title, and Description onto activity
        TextView a = (TextView) findViewById(R.id.textView12);
        Intent intent =getIntent();
        //this is how you receive an arraylist of strings from intent.
        ArrayList<String> rec = intent.getStringArrayListExtra("toSend");
        for(int i=0 ; i<rec.size() ; i++){
            b = b + rec.get(i);
        }
        a.setText(b);

        //CourseID pulled from CourseSelection to be used in query for prerequisites
       TextView d = (TextView) findViewById(R.id.textView14);
        Intent intent1 =getIntent();
        //this is how you receive an arraylist of strings from intent.
        ArrayList<String> rec1 = intent1.getStringArrayListExtra("toSend1");
        for(int i=0 ; i<rec1.size() ; i++){
            c = c + rec1.get(i);
        }
       d.setText(c);


        // Getting values from button, texts and progress bar
        run = (Button) findViewById(R.id.btnMoreDetails);


        run.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MoreDetails moreDetails = new MoreDetails();// this is the Asynctask, which is used to process in background to reduce load on app process
                moreDetails.execute("");
            }
        });
        //End Setting up the function when button login is clicked
    }

    public class MoreDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String prereq = "";


        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(String r)
        {

            Toast.makeText(CourseDetails.this, r, Toast.LENGTH_LONG).show();
            if(isSuccess)
            {
                //  message = (TextView) findViewById(R.id.textView2);
                //  message.setText(name1);

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
                    // Change below query according to your own database.


                    String query = "select PrerequisiteID from prerequisite where CourseID ='"+ c +"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        prereq = rs.getString("PrerequisiteID"); //Name is the string label of a column in database, read through the select query
                        z = "query successful";
                        isSuccess=true;
                        con.close();

                    }
                    else
                    {
                        z = "Invalid Query!";
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

    public void AddPlan(View v) {

        Intent goToDegreePlan = new Intent(getApplicationContext(), DegreePlan.class);
        startActivity(goToDegreePlan);
    }
    public void btnHistory(View v) {

        Intent goToHistory = new Intent(getApplicationContext(), History.class);
        startActivity(goToHistory);
    }
}
//