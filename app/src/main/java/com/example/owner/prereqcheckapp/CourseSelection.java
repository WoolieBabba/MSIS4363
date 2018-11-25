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
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.Result;

public class CourseSelection extends AppCompatActivity {
    // Declaring layout button, edit texts

    private ArrayList<String> arrayListCourse;
    private ArrayAdapter<String> arrayAdapterCourse;
    private String selectedChoice = "";
    public Button run;
    public TextView message;
    public ArrayList<String> arrayDescriptions = new ArrayList<String>();
    public ArrayList<String> arrayCourseID = new ArrayList<String>();
    //private String description = "This is the course description";

    public String courseid, title1;
    // End Declaring layout button, edit texts
    // Declaring connection variables
    public Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection);

       arrayListCourse = new ArrayList<String>();
       arrayAdapterCourse = new ArrayAdapter<String>(this, R.layout.courserow, R.id.courseRow, arrayListCourse);
        ListView listView = (ListView) findViewById(R.id.courselist);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //this helps you add radio buttons to the list
        listView.setAdapter(arrayAdapterCourse);
       //  Getting values from button, texts and progress bar
        run = (Button) findViewById(R.id.btnGetCourses);



       // run.setOnClickListener(new MenuItem.OnMenuItemClickListener()
        // run.setOnClickListener(new MenuItem.OnMenuItemClickListener()
        run.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CourseDisplay courseDisplay = new CourseDisplay();// this is the Asynctask, which is used to process in background to reduce load on app process
                courseDisplay.execute("");
            }
        });
        //End Setting up the function when button login is clicked
    }

    public class CourseDisplay extends AsyncTask<String,String,ArrayList<String>>
    {
        String z = "";
        Boolean isSuccess = false;
        String name1 = "";
        String cID = "";
        String title1 = "";
        String description = "";



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
            if(isSuccess)
            {
             //     message = (TextView) findViewById(R.id.textView2);
              //    message.setText(name1);

           }
        }
        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            //create an ArrayList
            ArrayList<String> courselist1 = null;
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
                    String query = "select CourseID, Title, Description from course";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    //build an ArrayList to hold results
                    courselist1 = new ArrayList<String>();
                    while (rs.next()){

                        courselist1.add(rs.getString("CourseID") + " - " + rs.getString("Title"));
                        arrayDescriptions.add(rs.getString("Description"));
                        arrayCourseID.add(rs.getString("CourseID"));

                    }
                    if(rs.next())
                    {
                        //cID = rs.getString("CourseID"); //CourseID is the string label of a column in database, read through the select query
                        //title1 = rs.getString("Title"); //Title is the string label of a column in database, read through the select query
                        //description = rs.getString("Description");
                        z = "Click Get Courses Button to Begin";
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

            return courselist1;
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
    public void CourseDetails (View v){

        //Array for CourseID to used in the query for prerequisites
        ListView listView1 = (ListView) findViewById(R.id.courselist);
        ArrayList<String> toSend1 = new ArrayList<String>();
        String c ="";
        for(int i=0 ; i<arrayAdapterCourse.getCount() ; i++){
            if (listView1.isItemChecked(i)){
                c = c + arrayCourseID.get(i);
                toSend1.add(c);

            }
        }

        //Array for CourseID, Title, and Description to pass to CourseDetails for displaying onto new activity
        ListView listView = (ListView) findViewById(R.id.courselist);
        ArrayList<String> toSend = new ArrayList<String>();
        String a ="";
        for(int i=0 ; i<arrayAdapterCourse.getCount() ; i++){
            if (listView.isItemChecked(i)){
                a = a + arrayListCourse.get(i) + "\n\n" + arrayDescriptions.get(i);
                toSend.add(a);

            }
        }
       // Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
        Intent goToCourseDetails = new Intent(getApplicationContext(), CourseDetails.class);
        //this is how you send multiple strings to next activity
        goToCourseDetails.putStringArrayListExtra("toSend", toSend);
        goToCourseDetails.putStringArrayListExtra("toSend1", toSend1);
        startActivity(goToCourseDetails);
    }
    //View Plan button functionality will take you to Degree Plan to view courses you have added there
    public void ViewPlan(View v) {

        Intent goToDegreePlan = new Intent(getApplicationContext(), DegreePlan.class);
        startActivity(goToDegreePlan);
    }
}
