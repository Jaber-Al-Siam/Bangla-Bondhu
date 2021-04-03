package com.example.bangla_bondhu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.bangla_bondhu.ui.main.SectionsPagerAdapter;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class InputActivity extends AppCompatActivity {
    private static final String TAG  = "PhoneNumber";
    Button camera;

    // Asif
    ImageView imgView;
    Button btnSelect;
    Button process;
    Button addbutton;

    private int REQUEST_STORAGE = 111;
    private int REQUEST_FILE = 222;

    private Uri uri;
    private String stringPath;
    private Intent iData;

    private static void onClick(View v) {

    }
    // Asif

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // Asif
        imgView = findViewById(R.id.imgView);
        btnSelect = findViewById(R.id.addimage);
        //safat
        addbutton = findViewById(R.id.addbutton);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(InputActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                }
                else{
                    selectImage();
                }
            }
        });
        //Asif

        camera = (Button)findViewById(R.id.addbutton);
        camera.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(InputActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_STORAGE);
            }
            else{
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                    Log.d("PhoneNumber", "onClick: camera");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.d("PhoneNumber", "onClick: camera exception" + e.getMessage());
                }

            }



        }
        );


        /*process = (Button) findViewById(R.id.process_btn);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational()){
                    Log.d(TAG, "onClick: Process Error");
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> sparseArray = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 0; i < sparseArray.size(); i++){
                        TextBlock text = sparseArray.valueAt(i);
                        stringBuilder.append(text.getValue());
                        stringBuilder.append("\n");
                    }
                    Log.d(TAG, "Image Text: " + stringBuilder.toString());
                }
            }
        });*/

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    

    // asif
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_FILE && resultCode == RESULT_OK){

            if (data != null){
                uri = data.getData();
                iData = data;

                /*String path = URIUtils.getPathFromUri(this, uri);
                try {
                    Detect.detectDocumentText(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onActivityResult: Process error " + e.getMessage());
                }
                Log.d("PhoneNumber", "onActivityResult: " + path);*/

                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);

                    //TextAnnotation textAnnotation = VisionAPI.call(inputStream);
                    /*Toast.makeText(getApplicationContext(),
                            textAnnotation.getText(), Toast.LENGTH_LONG).show();*/

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.demo);
                    imgView.setImageBitmap(bitmap);
                    Log.d(TAG, "onActivityResult: ");

                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if(!textRecognizer.isOperational()){
                        Log.d(TAG, "onClick: Image Process Error");
                    }
                    else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> sparseArray = textRecognizer.detect(frame);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < sparseArray.size(); i++) {
                            TextBlock text = sparseArray.valueAt(i);
                            stringBuilder.append(text.getValue());
                            stringBuilder.append("\n");
                        }
                        Log.d(TAG, "Image Text: " + stringBuilder.toString());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // asif
    public void backButtonClick(View view){
        this.finish();
    }
}