package com.example.bangla_bondhu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegisterNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);
    }

    public void continueClick(View view){
        EditText editText = (EditText) findViewById(R.id.get_phoneNumber);
        Editable phoneNumber = editText.getText();
        Log.i("phoneNumber", String.valueOf(phoneNumber));
        Intent intent = new Intent(this, VerificationActivity.class);
        startActivity(intent);
    }
}