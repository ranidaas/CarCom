package com.example.carcom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    private ProgressBar bar;
    private TextView welcome;
    private FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bar = findViewById(R.id.home_page_bar);
        bar.setVisibility(View.VISIBLE);
        welcome = findViewById(R.id.home_page_welcome);


        //welcome
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference ref = db.collection("Users").document(user.getUid());
        ref.get().addOnSuccessListener(documentSnapshot -> {
            User userProfile = documentSnapshot.toObject(User.class);
            assert userProfile != null;
            String fullName = userProfile.fullName;
            welcome.setText("Welcome "+fullName+"!");
            bar.setVisibility(View.GONE);
        });
//

        //Assign variables
        MaterialButton logout = findViewById(R.id.home_page_logout);
        MaterialButton viaImage = findViewById(R.id.home_page_image);
        MaterialButton viaText = findViewById(R.id.home_page_text);
        MaterialButton chats = findViewById(R.id.home_page_chats);
        mAuth = FirebaseAuth.getInstance();




        //On click Listeners
        logout.setOnClickListener(view -> {
            //sign out
            mAuth.signOut();
            startActivity(new Intent(HomePage.this,MainActivity.class));
            finish();
        });

        viaImage.setOnClickListener(view -> {
            startActivity(new Intent(HomePage.this,Example.class));
            finish();
        });

        viaText.setOnClickListener(view -> {
            startActivity(new Intent(HomePage.this,ViaText.class));
            finish();
        });

        chats.setOnClickListener(view -> {
            db.collection("Users")
                    .document(mAuth.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot document = task.getResult();
                        if(((ArrayList<String>) document.get("users_i_messaged")).isEmpty()){
                            Toast.makeText(getApplicationContext(), "You don't have any messages", Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(new Intent(HomePage.this,Chat.class));
                            finish();
                        }
                    });

        });
    }
}