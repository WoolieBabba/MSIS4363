package com.example.owner.prereqcheckapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class DegreePlan extends AppCompatActivity {
    // Declaring layout button, edit texts
    private ArrayList<String> arrayListCourse;
    private ArrayAdapter<String> arrayAdapterCourse;
    public Button run;
    public TextView message;
    // End Declaring layout button, edit texts
    // Declaring connection variables
    public Connection con;
   //public String loginUser = getIntent().getStringExtra("LoginUser");
    public Integer sID;
    public ArrayList<String> arrayPrereq = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_degree_plan);
        sID = getIntent().getIntExtra("sID", 0);
        // Getting values from button, texts and progress bar
        run = (Button) findViewById(R.id.btnAddHistory);

       run.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AddHistory addToHistory = new AddHistory();// this is the Asynctask, which is used to process in background to reduce load on app process
                addToHistory.execute("");

            }
        });
        LoadPlan plan = new LoadPlan();// this is the Asynctask, which is used to process in background to reduce load on app process
        plan.execute("");
        arrayListCourse = new ArrayList<String>();
        arrayAdapterCourse = new ArrayAdapter<String>(this, R.layout.courserow, R.id.courseRow, arrayListCourse);
        ListView listView = (ListView) findViewById(R.id.degreelist);
        listView.setAdapter(arrayAdapterCourse);
    }
        //End Setting up the function when button login is clicked

    public class LoadPlan extends AsyncTask<String,String,ArrayList<String>>
    {
        String z = "";
        Boolean isSuccess = false;
        String name1 = "";

        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(ArrayList<String> r)
        {
            Iterator<String> interator =r.iterator();
            while (interator.hasNext()){
                arrayAdapterCourse.add(interator.next().toString());
            }

            //Toast.makeText(DegreePlan.this, r, Toast.LENGTH_LONG).show();
            if(isSuccess)
            {
                //  message = (TextView) findViewById(R.id.textView2);
                //  message.setText(name1);
                //Intent goToHistory = new Intent(getApplicationContext(), History.class);
                //startActivity(goToHistory);
            }
        }
        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            try
            {
                con = connectionclass();        // Connect to database
                if (con == null)
                {
                    z = "Check Your Internet Access!";
                }
                else {
                    // Change below query according to your own database.
                    String query = "select courseID from planitem where planid in(select planid from studentplan where studentID = " + Integer.toString(sID) + ");";
                    Log.i("query", query);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Log.i("prereqCourse", rs.getString("courseID"));
                        arrayPrereq.add(rs.getString("courseID"));
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = ex.getMessage();

                Log.d ("sql error", z);
            }

            return arrayPrereq;
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
    public class AddHistory extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String prereq = "";


        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(String z)
        {

            if(isSuccess)
            {
                Intent goToHistory = new Intent(getApplicationContext(), History.class);
                goToHistory.putExtra("sID", sID);
                startActivity(goToHistory);
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
                else {
                    //for each course in the plan, add to history,

                    for (Iterator<String> planCourses = arrayPrereq.iterator(); planCourses.hasNext();) {

                        String courseToAdd = planCourses.next();
                        String placeCourseIntoHistory = "insert into history (studentid, courseid) values (" +
                                sID.toString() + ", '" + courseToAdd + "');";
                        Log.i("details addtoplaninsert", placeCourseIntoHistory);
                        Statement plcoinhi = con.createStatement();
                        plcoinhi.executeUpdate(placeCourseIntoHistory);

                    }

                    //delete courses from the plan
                    String deletePlanCourses = "delete from planitem where planid " +
                            "in(select planid from studentplan where StudendID = " + sID.toString() + ");";
                    Statement deplco = con.createStatement();
                    deplco.executeUpdate(deletePlanCourses);
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = ex.getMessage();

                Log.d ("sql error", z);
            }
            Log.i("planz", z);
            return z;
        }
    }

    public void ViewHistory (View v) {
        Intent goToHistory = new Intent(getApplicationContext(), History.class);
        goToHistory.putExtra("sID", sID);
        startActivity(goToHistory);
    }

}

//