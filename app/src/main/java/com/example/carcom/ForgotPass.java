package com.example.carcom;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPass extends AppCompatActivity {
    private ProgressBar bar;
    private EditText editTextEmail;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        // Assign Variables
        TextView returnText = (TextView) findViewById(R.id.forgot_pass_return);
        bar = (ProgressBar) findViewById(R.id.forgot_pass_bar);
        editTextEmail = (EditText) findViewById(R.id.forgot_pass_mail);
        MaterialButton send = (MaterialButton) findViewById(R.id.forgot_pass_send);
        mAuth = FirebaseAuth.getInstance();


        // On Click Listeners
        returnText.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPass.this,MainActivity.class));
            finish();
        });

        send.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword(){
        String email = editTextEmail.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!!");
            editTextEmail.requestFocus();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
            if(isNewUser){
                editTextEmail.setError("Email does not exist!!");
                editTextEmail.requestFocus();
            }else{
                bar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Toast.makeText(ForgotPass.this,"Check your email for reset link",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ForgotPass.this,"ERROR, Try Again",Toast.LENGTH_SHORT).show();
                    }
                    bar.setVisibility(View.GONE);
                });
            }
        });

    }
}