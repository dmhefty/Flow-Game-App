package edu.msu.heftydav.examheftydav;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onResetButton(View view){
        FlowView flowView = (FlowView) findViewById(R.id.flowView);
        flowView.Reset();
    }
}