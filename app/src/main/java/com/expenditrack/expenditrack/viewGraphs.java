package com.expenditrack.expenditrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class viewGraphs extends AppCompatActivity {

    Button pie_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graphs);

   }


    public void viewPieCharts(View view){
        Intent intent = new Intent(this,view_pie_charts.class);
        startActivity(intent);
    }

}
