package com.example.bangla_bondhu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.bangla_bondhu.model.gcptts.GCloudTTSActivity;
import com.example.bangla_bondhu.ui.main.SectionsPagerAdapter;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;

public class InputActivity extends AppCompatActivity {

    private static final String TAG  = "DEBUG";
    private  ImageView imgView;
    private final int REQUEST_STORAGE = 111;
    private final int REQUEST_FILE = 222;
    private Vision vision;
    private TextToSpeech mTTS;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        imgView = findViewById(R.id.imgView);
        Button addImage = findViewById(R.id.addimage);
        Button takeImage = findViewById(R.id.addbutton);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBHObDQQEm9xxsyTLGj3prmvG45d-0C1WY"));

        vision = visionBuilder.build();

        addImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(InputActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
            else{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_FILE);
            }
        });


        takeImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(InputActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_STORAGE);
            }
            else{
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Log.d("PhoneNumber", "onClick: camera exception" + e.getMessage());
                }

            }
        });

        mTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                int result = mTTS.setLanguage(new Locale("bn_IN"));
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "onInit: TTS Initialized successfully");
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "TTS initialization Failed", Toast.LENGTH_LONG).show();
            }
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_FILE && resultCode == RESULT_OK){

            if (data != null){
                Uri uri = data.getData();
                /*try {
                    Detect.detectDocumentText(URIUtils.getPathFromUri(this, uri));
                } catch (IOException e) {
                    Log.d(TAG, "on Detect call: " + e.getMessage());
                }*/
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imgView.setImageBitmap(bitmap);
                    textDetection();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "onActivityResult: " + e.getMessage());
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

    private void speak(String text){
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void backButtonClick(View view){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    private InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}