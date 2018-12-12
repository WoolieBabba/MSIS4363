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

import org.w3c.dom.Text;

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
   // public String loginUser = getIntent().getStringExtra("LoginUser");
   public ArrayList<String> arrayPrereqs = new ArrayList<String>();
   public String course;
   public Integer sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        //Display CourseID, Title, and Description onto activity
        Intent intent =getIntent();
        //this is how you receive an arraylist of strings from intent.
        course = intent.getStringExtra("course");
        sID = intent.getIntExtra("sID", 0);
        TextView courseTitle = (TextView) findViewById(R.id.textView11);
        courseTitle.setText(courseTitle.getText() + " " + course);


        // Getting values from button, texts and progress bar

        MoreDetails moreDetails = new MoreDetails();// this is the Asynctask, which is used to process in background to reduce load on app process
        moreDetails.execute("");
        //End Setting up the function when button login is clicked
    }

    public class MoreDetails extends AsyncTask<String,String,ArrayList<String>>
    {
        String z = "";
        Boolean isSuccess = false;
        String prereq = "";


        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(ArrayList<String> r)
        {

            if(isSuccess)
            {
                //  message = (TextView) findViewById(R.id.textView2);
                //  message.setText(name1);

            }
        }
        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            ArrayList<String> courselist1 = new ArrayList<String>();
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


                    String detailsQuery = "select Title, Description, CreditHrs from course where CourseID = '" + course + "';";
                    String query = "select * from prerequisite;";
                    Log.i("query", query);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(detailsQuery);
                    rs.next();
                    Log.i("Description", rs.getString("Description"));
                    TextView description = (TextView) findViewById(R.id.textView12);
                    TextView prereqs = (TextView) findViewById(R.id.textView14);
                    description.setText(rs.getString("Title") + "\n\n"+ rs.getString("Description")
                        + "\n\n" + "Credit Hours: " + rs.getString("CreditHrs"));
                    rs = stmt.executeQuery(query);
                    while(rs.next())
                    {
                        /*we could not get results running something like
                        * select prerequisiteID from prerequisite where CourseID = course;
                        * so our solution is to loop through results and if courseID matches,
                        * store the corresponding prerequisiteID*/
                        Log.i("prereqcourse", rs.getString("CourseID"));
                        Log.i("prereqcourse", course);
                        Log.i("prereqcourse", String.valueOf(course.equals(rs.getString("CourseID"))));
                        if(course.equals(rs.getString("CourseID"))) {
                            prereqs.setText(prereqs.getText() + rs.getString("PrerequisiteID"));
                        }
                        z = "query successful";
                        isSuccess=true;
                        con.close();

                    }
                    if(rs.next()){ }
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

    public void AddPlan(View v) {

        Intent goToDegreePlan = new Intent(getApplicationContext(), DegreePlan.class);
        goToDegreePlan.putExtra("sID", sID);
        startActivity(goToDegreePlan);
    }
    public void btnHistory(View v) {

        Intent goToHistory = new Intent(getApplicationContext(), History.class);
       // goToHistory.putExtra("LoginUser", loginUser);
        goToHistory.putExtra("sID", sID);
        startActivity(goToHistory);
    }
}
//