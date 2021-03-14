package com.example.bangla_bondhu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class  MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
    }

    public void onClick(View view){
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }
}