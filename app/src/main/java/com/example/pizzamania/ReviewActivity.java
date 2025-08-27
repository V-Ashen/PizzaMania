package com.example.pizzamania;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    private EditText editReview;
    private Button btnChooseImage, btnSubmit;
    private ImageView imgPreview;

    private Bitmap capturedPhoto;
    private FirebaseFirestore firestore;

    private String customerName = "Unknown";
    private String orderId = "unknown";

    // Activity Result launchers
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        editReview = findViewById(R.id.editReview);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        firestore = FirebaseFirestore.getInstance();

        // session + orderId
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        customerName = sp.getString("username", "Unknown");
        String passedId = getIntent().getStringExtra("orderId");
        if (passedId != null && !passedId.isEmpty()) orderId = passedId;

        // Register Activity Result launchers
        registerLaunchers();

        // Open camera on click
        btnChooseImage.setOnClickListener(view -> ensureCameraPermissionThenOpen());

        // Submit review (Base64 -> Firestore)
        btnSubmit.setOnClickListener(view -> uploadReview());
    }

    private void registerLaunchers() {
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) openCamera();
                    else Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                });

        takePhotoLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap photo = (Bitmap) extras.get("data"); // thumbnail
                            if (photo != null) {
                                capturedPhoto = photo;
                                imgPreview.setImageBitmap(photo);
                                return;
                            }
                        }
                        Toast.makeText(this, "Couldn't capture image. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ensureCameraPermissionThenOpen() {
        // For ACTION_IMAGE_CAPTURE via external camera app, many devices don't need CAMERA permission,
        // but on newer Android versions it's safer to request it.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            takePhotoLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "No camera app found on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadReview() {
        if (capturedPhoto == null) {
            Toast.makeText(this, "Please take a photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Bitmap -> Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        capturedPhoto.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        String reviewText = editReview.getText().toString().trim();

        Map<String, Object> review = new HashMap<>();
        review.put("orderId", orderId);
        review.put("customerName", customerName);
        review.put("reviewText", reviewText);
        review.put("photoBase64", encodedImage);
        review.put("createdAt", System.currentTimeMillis());

        firestore.collection("reviews").add(review)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Review Submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
