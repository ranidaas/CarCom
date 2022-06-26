package com.example.carcom;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Example extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        MaterialButton btn = (MaterialButton) findViewById(R.id.example_btn);

        btn.setOnClickListener(view -> {
            startActivity(new Intent(Example.this,ViaImage.class));
            finish();
        });
    }
}