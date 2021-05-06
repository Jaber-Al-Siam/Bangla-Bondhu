package com.example.bangla_bondhu;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bangla_bondhu.model.gcptts.GCloudTTSActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;

public class PDFActivityTwo extends AppCompatActivity {

    private static final String TAG  = "DEBUG";
    private final int REQUEST_STORAGE = 111;
    private final int PICK_PDF = 222;

    Button btn_filePicker;
    Intent myFileIntent;
    PDFView pdfView;
    private Vision vision;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_two);

        btn_filePicker = (Button) findViewById(R.id.btn_filePicker);
        pdfView = findViewById(R.id.pdfView);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBHObDQQEm9xxsyTLGj3prmvG45d-0C1WY"));

        vision = visionBuilder.build();

        btn_filePicker.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(PDFActivityTwo.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
            else{
                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent, PICK_PDF);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_PDF) {
                Uri uri = data.getData();
                pdfView.fromUri(uri).load();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    textDetection();
                } catch (Exception e){
                    Log.d(TAG, "onActivityResultError: " + e.getMessage());
                }
            }
        }
    }

    private void textDetection() {
        AsyncTask.execute(() -> {
            try {
                InputStream inputStream = bitmap2InputStream(bitmap);
                byte[] photoData = IOUtils.toByteArray(inputStream);

                Image inputImage = new Image();
                inputImage.encodeContent(photoData);

                Feature desiredFeature = new Feature();
                desiredFeature.setType("TEXT_DETECTION");

                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Collections.singletonList(desiredFeature));

                BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Collections.singletonList(request));

                BatchAnnotateImagesResponse batchResponse =
                        vision.images().annotate(batchRequest).execute();

                final TextAnnotation text = batchResponse.getResponses()
                        .get(0).getFullTextAnnotation();

                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        text.getText(), Toast.LENGTH_LONG).show());

                //speak(text.getText());
                Intent intent = new Intent(this, GCloudTTSActivity.class);
                intent.putExtra("text", text.getText());
                startActivity(intent);
                Log.d(TAG, "textDetection:\n" + text.getText());

            } catch(Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }

    private InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}