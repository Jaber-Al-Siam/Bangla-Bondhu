package com.example.bangla_bondhu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bangla_bondhu.model.gcptts.GCloudTTSActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ImageButton imageToText = findViewById(R.id.imagetotext);
        ImageButton pdfToText = findViewById(R.id.pdftotext);
        ImageButton tts = findViewById(R.id.tts);

        imageToText.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ImageActivity.class);
            startActivity(intent);
        });

        pdfToText.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PDFActivity_.class);
            startActivity(intent);
        });

        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, GCloudTTSActivity.class);
                intent.putExtra("text", "");
                startActivity(intent);
            }
        });
    }
}