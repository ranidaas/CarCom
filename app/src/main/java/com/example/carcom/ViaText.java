package com.example.carcom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;


public class ViaText extends AppCompatActivity {
    private EditText editText;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_text);

        // Assign Variables
        ImageView home = findViewById(R.id.via_text_home);
        editText = findViewById(R.id.via_text_license);
        MaterialButton btn = findViewById(R.id.via_text_connect);
        db = FirebaseFirestore.getInstance();

        // On Click Listeners
        home.setOnClickListener(view -> {
            startActivity(new Intent(ViaText.this,HomePage.class));
            finish();
        });

        btn.setOnClickListener(view -> {
            String license = editText.getText().toString().trim();
            if(license.isEmpty()){
                editText.setError("Need to enter license plate number");
                editText.requestFocus();
                return;
            }

            license =license.replaceAll("\\D","");

            if(license.length()<7 || license.length()>8){
                editText.setError("Wrong license number");
                editText.requestFocus();
            }else {
                db.collection("Users").whereEqualTo("license",license).get().addOnCompleteListener(task1 -> {
                    if(task1.getResult().size()==0){
                        // license does not exists
                        Toast.makeText(ViaText.this,"License does not Exist",Toast.LENGTH_SHORT).show();
                    }else{
                        //license exists
                        String userId=task1.getResult().getDocuments().toString().split("Users/")[1].split(",")[0];
                        Log.d("RESULT", "output:"+userId);
                        connect(userId);
                    }
                });
            }
        });
    }

    private void connect(String userId) {
        Intent intent=new Intent(ViaText.this, SpecificChat.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }
}