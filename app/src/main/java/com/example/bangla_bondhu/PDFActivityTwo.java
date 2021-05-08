package com.example.bangla_bondhu;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
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
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class PDFActivityTwo extends AppCompatActivity implements PickiTCallbacks {

    private static final String TAG  = "DEBUG";
    private final int REQUEST_STORAGE = 111;
    private final int PICK_PDF = 222;

    Button btn_filePicker;
    PDFView pdfView;
    private Vision vision;
    private Bitmap bitmap;
    private PickiT pickiT;
    private String pdfText = "";
    private int processedPage = 0;
    private int totalPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_two);

        btn_filePicker = findViewById(R.id.btn_filePicker);
        pdfView = findViewById(R.id.pdfView);
        pickiT = new PickiT(this, this, this);

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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivityForResult(intent, PICK_PDF);
                startActivityForResult(Intent.createChooser(intent, "Choose a pdf file"), PICK_PDF);
            }
        });
    }

    // This method is used to extract all pages in image (PNG) format.
    private void getImagesFromPDF(File pdfFilePath) throws IOException {

        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFilePath, ParcelFileDescriptor.MODE_READ_ONLY);

        PdfRenderer renderer = new PdfRenderer(fileDescriptor);

        final int pageCount = renderer.getPageCount();
        totalPage = pageCount;

        // Iterating pages
        for (int i = 0; i < pageCount; i++) {
            Log.d(TAG, "getImagesFromPDF: processing " + i);

            PdfRenderer.Page page = renderer.openPage(i);

            bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),Bitmap.Config.ARGB_8888);

            // Creating Canvas from bitmap.
            Canvas canvas = new Canvas(bitmap);

            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, null);

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            textDetection();
            page.close();
        }
    }

    private void startGCloudTTS(){
        Intent intent = new Intent(PDFActivityTwo.this, GCloudTTSActivity.class);
        intent.putExtra("text", pdfText);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_PDF) {
                assert data != null;
                Uri uri = data.getData();
                pdfView.fromUri(uri).load();
                try {
                    pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                } catch (Exception e){
                    Log.d(TAG, "onActivityResultError: " + e.getStackTrace().toString());
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

                pdfText += text.getText();
                Log.d(TAG, "textDetection:\n" + text.getText());
                processedPage++;

                if(processedPage == totalPage){
                    startGCloudTTS();
                }

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

    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.d(TAG, "DEBUG" + path);

        try {
            processedPage = 0;
            getImagesFromPDF(new File(path));
        } catch (IOException e) {
            Log.d(TAG, "PickiTonCompleteListener: " + e.getMessage());
        }
    }
}