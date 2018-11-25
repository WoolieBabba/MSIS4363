package com.example.owner.prereqcheckapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    public void GoToCourseSelect(View v) {

        Intent goToCourseSelect = new Intent(getApplicationContext(), CourseSelection.class);
        startActivity(goToCourseSelect);
    }

    public void GoToDegreePlan(View v) {

        Intent goToDegreePlan = new Intent(getApplicationContext(), DegreePlan.class);
        startActivity(goToDegreePlan);
    }
}
