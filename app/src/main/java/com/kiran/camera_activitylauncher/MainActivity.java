package com.kiran.camera_activitylauncher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.kiran.camera_activitylauncher.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicker();

            }
        });
    }

    private void showImagePicker() {
        String[] option = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Image Pic")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            checkCameraPermissions();
                        } else {
                            checkGallaryPermissions();
                        }

                    }
                });
        builder.show();

    }

    private void checkGallaryPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            // Request the permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},IMAGE_PICK_GALLERY_CODE);
        }
        else{
            openGallery();
        }
    }

    private void checkCameraPermissions() {
        // Check if the camera permission is not granted
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
       ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        else{
            openCamera();


        }

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        activityResultLauncher.launch(gallery);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(camera);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // Check if the result is from the camera or gallery
//                    in short check data is one URI
//                    data.getData()--DEVICE SE DATA GET karne ke liye uri (content provider hi use karte hai)
                    if (data.getData() != null) {
                        // Result from gallery
                        Uri selectedImageUri = data.getData();

                         binding.camera.setImageURI(selectedImageUri);
                    } else {
                        // Result from camera
                        // The image is in the extras of the intent
                         Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            "data" is a key for store thumbnail size image in camera activity
                            // Now you can use the imageBitmap to do something with the image

                        binding.camera.setImageBitmap(imageBitmap);
                        }
                    }
                }
            }
        }
    });
}
