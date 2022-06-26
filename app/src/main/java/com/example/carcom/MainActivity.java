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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail,editTextPassword;
    private ProgressBar bar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign Variables
        MaterialButton signUp = (MaterialButton) findViewById(R.id.signupbtn);
        MaterialButton signIn = (MaterialButton) findViewById(R.id.signinbtn);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        TextView forgotPass = (TextView) findViewById(R.id.forgotpass);
        bar = (ProgressBar) findViewById(R.id.bar);
        mAuth = FirebaseAuth.getInstance();

        //On click listeners
        signUp.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SignUp.class));
            finish();
        });

        signIn.setOnClickListener(view -> userLogin());

        forgotPass.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,ForgotPass.class));
            finish();
        });
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
            if(isNewUser){
                editTextEmail.setError("Email does not exist!!");
                editTextEmail.requestFocus();
            }else{
                bar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        if(user.isEmailVerified()){
                            startActivity(new Intent(MainActivity.this,HomePage.class));
                            bar.setVisibility(View.GONE);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this,"Check your email to verify your account!!",Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"Failed to Login, Check your credentials",Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(MainActivity.this,HomePage.class);
            startActivity(intent);
            finish();
        }
    }
}