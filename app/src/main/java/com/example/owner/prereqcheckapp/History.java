package com.example.owner.prereqcheckapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class History extends AppCompatActivity {
    private ArrayList<String> arrayListCourse;
    private ArrayAdapter<String> arrayAdapterCourse;
    public ArrayList<String> arrayHist = new ArrayList<String>();
    public Connection con;
    public int sID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sID = getIntent().getIntExtra("sID", 0);
        LoadHistory history = new LoadHistory();// this is the Asynctask, which is used to process in background to reduce load on app process
        history.execute("");
        arrayListCourse = new ArrayList<String>();
        arrayAdapterCourse = new ArrayAdapter<String>(this, R.layout.courserow, R.id.courseRow, arrayListCourse);
        ListView listView = (ListView) findViewById(R.id.historylist);
        listView.setAdapter(arrayAdapterCourse);
    }

    public class LoadHistory extends AsyncTask<String,String,ArrayList<String>>
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
                else
                {
                    // Change below query according to your own database.
                    String query = "select courseID from history where studentID = " + Integer.toString(sID) + ";";
                    Log.i("query", query);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while(rs.next()) {
                        Log.i("historyCourse", rs.getString("courseID"));
                        arrayHist.add(rs.getString("courseID"));
                    }

                    if(rs.next())
                    {
                        // name1 = rs.getString("lastName"); //Name is the string label of a column in database, read through the select query
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

            return arrayHist;
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

    public void GoToCourseSelect(View v) {

        Intent goToCourseSelect = new Intent(getApplicationContext(), CourseSelection.class);
        goToCourseSelect.putExtra("sID", sID);
        startActivity(goToCourseSelect);
    }

    public void GoToDegreePlan(View v) {

        Intent goToDegreePlan = new Intent(getApplicationContext(), DegreePlan.class);
        goToDegreePlan.putExtra("sID", sID);
        startActivity(goToDegreePlan);
    }
}
