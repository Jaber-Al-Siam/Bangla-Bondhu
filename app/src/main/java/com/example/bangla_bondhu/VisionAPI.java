package com.example.bangla_bondhu;

import android.util.Log;

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
import java.util.Arrays;

public class VisionAPI {
    private static final String TAG  = "PhoneNumber";
    public static TextAnnotation call(InputStream inputStream ){

        Log.d(TAG, "call: API call");

        TextAnnotation text = null;

        try {
            Vision.Builder visionBuilder = new Vision.Builder(
                    new NetHttpTransport(),
                    new AndroidJsonFactory(),
                    null);

            visionBuilder.setVisionRequestInitializer(
                    new VisionRequestInitializer("3cfa793acba10a812cf2a08b59c06a340e9ee649"));

            Vision vision = visionBuilder.build();

            Log.d(TAG, "call: API instance created successfully");

            byte[] photoData = IOUtils.toByteArray(inputStream);

            Image inputImage = new Image();
            inputImage.encodeContent(photoData);

            Log.d(TAG, "call: Image processed successfully");

            Feature desiredFeature = new Feature();
            desiredFeature.setType("DOCUMENT_TEXT_DETECTION");

            AnnotateImageRequest request = new AnnotateImageRequest();
            request.setImage(inputImage);
            request.setFeatures(Arrays.asList(desiredFeature));

            Log.d(TAG, "call: Image and feature set successfully");

            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();

            batchRequest.setRequests(Arrays.asList(request));

            Log.d(TAG, "call: Request set successfully");

            BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();

            Log.d(TAG, "call: API call execute successfully");

            text = batchResponse.getResponses()
                    .get(0).getFullTextAnnotation();

            Log.d(TAG, "call: Text retrieve successfully");

        } catch (Exception e) {
            Log.d(TAG, "API call error: " + e.getMessage());
        }
        return  text;
    }
}
