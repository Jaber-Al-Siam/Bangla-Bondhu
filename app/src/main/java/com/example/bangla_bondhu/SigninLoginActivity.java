package com.example.bangla_bondhu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SigninLoginActivity extends AppCompatActivity {

    Button signupButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signinlogin);
        signupButton = (Button) findViewById(R.id.btn_signup);

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(SigninLoginActivity.this, ImageActivity.class);
            startActivity(intent);
        });
    }
}