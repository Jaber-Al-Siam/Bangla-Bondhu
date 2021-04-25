package com.example.bangla_bondhu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

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

import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;

public class OCRActivity extends AppCompatActivity {

    private final String TAG = "Debuggg";
    private Vision vision;
    private String data = "কোন পাঠ্য পাওয়া যায় নি";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBHObDQQEm9xxsyTLGj3prmvG45d-0C1WY"));

        vision = visionBuilder.build();

        textDetection();
    }

    private void textDetection() {
        AsyncTask.execute(() -> {
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.photo_text);
                byte[] photoData = IOUtils.toByteArray(inputStream);

                Image inputImage = new Image();
                inputImage.encodeContent(photoData);

                Feature desiredFeature = new Feature();
                desiredFeature.setType("DOCUMENT_TEXT_DETECTION");

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

                //Log.d(TAG, "textDetection:\n" + text.getText());
                /*data = text.getText();
                Intent intent = new Intent(this, TTSActivity.class);
                intent.putExtra("text", data);
                startActivity(intent);*/

            } catch(Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }
}