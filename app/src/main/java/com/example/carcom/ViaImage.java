package com.example.carcom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ViaImage extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap imageBitmap;
    private TextRecognizer textRecognizer;
    private String license;
    private FirebaseFirestore db;

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            if (result == RESULT_OK) {
                                assert data != null;
                                Bundle extras = data.getExtras();
                                //Get Capture Image
                                imageBitmap = (Bitmap) extras.get("data");
                                //Set Capture Image to ImageView
                                imageView.setImageBitmap(imageBitmap);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_image);

        //Assign variables
        ImageView home = (ImageView) findViewById(R.id.via_image_home);
        imageView = (ImageView) findViewById(R.id.via_image_image_view);
        Button captureImage = (Button) findViewById(R.id.via_image_capture_image);
        Button connect = (Button) findViewById(R.id.via_image_connect);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        db= FirebaseFirestore.getInstance();


        // Request camera use permission
        if(ContextCompat.checkSelfPermission(ViaImage.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            requestCameraPerm();
        }


        // On click Listeners

        home.setOnClickListener(view -> {
            startActivity(new Intent(ViaImage.this,HomePage.class));
            finish();
        });

        captureImage.setOnClickListener(view -> {
            try{
                dispatchTakePictureIntent();
            }catch (Exception e){
                Toast.makeText(ViaImage.this,"Need Permission",Toast.LENGTH_SHORT).show();
                requestCameraPerm();
            }
        });

        connect.setOnClickListener(view -> {
            if(imageView.getDrawable() == null){
                Toast.makeText(ViaImage.this,"Need to take a picture first",Toast.LENGTH_LONG).show();
            }else{
                detectText();
            }
        });

    }

    private void requestCameraPerm(){
        ActivityCompat.requestPermissions(ViaImage.this,new String[]{Manifest.permission.CAMERA}, 100);
    }

    private void dispatchTakePictureIntent(){
        //Open Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(takePictureIntent);
    }

    private void detectText(){
        try {
            InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
            textRecognizer.process(inputImage).addOnSuccessListener(text -> {
                license = text.getText();
                license=license.replaceAll("\\D","");
                if(license.length()<7 || license.length()>8){
                    Log.d("ERROR", "License is: "+license);
                    Toast.makeText(ViaImage.this,"Please take a clearer license plate number picture",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ViaImage.this,"License Number is: "+license,Toast.LENGTH_LONG).show();
                    //Check if License Exists in Database
                    db.collection("Users").whereEqualTo("license",license).get().addOnCompleteListener(task1 -> {
                        if(task1.getResult().size()==0){
                            // license does not exists
                            Toast.makeText(ViaImage.this,"License does not Exist",Toast.LENGTH_SHORT).show();

                        }else{
                            //license exists
                            String userId=task1.getResult().getDocuments().toString().split("Users/")[1].split(",")[0];
                            Log.d("RESULT", "output:"+userId);
                            connect(userId);
                        }
                    });
                }
            }).addOnFailureListener(e -> Log.d("ERROR", "detectText: ERROR : "+e.getMessage()));

        }catch (Exception e){
            Log.d("ERROR", "detectText: ERROR : "+e.getMessage());
        }
    }

    private void connect(String userId) {
        Intent intent=new Intent(ViaImage.this, SpecificChat.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }
}