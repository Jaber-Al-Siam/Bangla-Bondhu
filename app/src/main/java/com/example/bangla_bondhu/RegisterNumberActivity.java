package com.example.bangla_bondhu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegisterNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);
    }

    public void continueClick(View view){
        Intent intent = new Intent(this, VerificationActivity.class);
        startActivity(intent);
    }
}