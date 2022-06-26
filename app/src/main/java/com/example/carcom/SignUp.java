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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail,editTextPassword,editTextFullName,editTextLicense;
    private ProgressBar bar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Assign Variables
        mAuth = FirebaseAuth.getInstance();
        editTextEmail=(EditText) findViewById(R.id.sign_up_email);
        editTextPassword=(EditText) findViewById(R.id.sign_up_password);
        editTextFullName=(EditText) findViewById(R.id.sign_up_fullname);
        editTextLicense=(EditText) findViewById(R.id.sign_up_license);
        TextView returnMsg = (TextView) findViewById(R.id.sign_up_return);
        MaterialButton signup = (MaterialButton) findViewById(R.id.sign_up_signupbtn);
        bar = (ProgressBar) findViewById(R.id.sign_up_bar);
        db = FirebaseFirestore.getInstance();

        //On click listeners
        returnMsg.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
        });

        //register user
        signup.setOnClickListener(view -> register());
    }

    private void register() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String license = editTextLicense.getText().toString().trim();

        //Checks
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

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password should be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is required!!");
            editTextFullName.requestFocus();
            return;
        }

        if(license.isEmpty()){
            editTextLicense.setError("License is required!!");
            editTextLicense.requestFocus();
            return;
        }

        if(license.length() <7 || license.length()>8){
            editTextLicense.setError("License plate number should be 7 or 8 digits");
            editTextLicense.requestFocus();
            return;
        }

        if(!license.matches("[0-9]+")){
            editTextLicense.setError("License plate number should be numbers only");
            editTextLicense.requestFocus();
            return;
        }

        bar.setVisibility(View.VISIBLE);
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
            if(isNewUser){
                //Check if license plate number exists
                //using FireStore database
                db.collection("Users").whereEqualTo("license",license).get().addOnCompleteListener(task1 -> {
                    if(task1.getResult().size()==0){
                        // new user with new license plate
                        createUser(email,password,fullName,license);
                        startActivity(new Intent(SignUp.this,MainActivity.class));
                        finish();
                    }else{
                        //license plate is used
                        editTextLicense.setError("License plate number is already used");
                        editTextLicense.requestFocus();
                        bar.setVisibility(View.GONE);
                    }
                });
            }else{
                editTextEmail.setError("Email already used!!");
                editTextEmail.requestFocus();
                bar.setVisibility(View.GONE);
            }
        });

    }

    private void createUser(String email, String password, String fullName, String license) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User user = new User (email,fullName,license,mAuth.getUid());
                //using FireStore
                db.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).set(user).addOnCompleteListener(task1 -> {
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                    Toast.makeText(SignUp.this,"User has been registered successfully",Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.GONE);
                });
            }
        });
    }
}