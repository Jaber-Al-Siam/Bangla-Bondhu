package com.example.bangla_bondhu;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
    }

    public void onClick(View view){
        // Button button = (Button) findViewById(R.id.btn_getStarted);
        setContentView(R.layout.activity_input);
    }
}