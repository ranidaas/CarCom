package com.example.carcom;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpecificChat extends AppCompatActivity {
    EditText mgetmessage;
    ImageButton msendmessagebutton;

    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;

    private String enteredmessage;
    Intent intent;
    String mrecieveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String senderroom,recieverroom;

    ImageButton mbackbuttonofspecificchat;

    RecyclerView mmessagerecyclerview;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;
    ArrayList<String> usersArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        //Assign Variables
        mgetmessage=findViewById(R.id.get_message);
        msendmessagecardview=findViewById(R.id.card_view_of_send_message);
        msendmessagebutton=findViewById(R.id.image_view_send_message);
        mtoolbarofspecificchat=findViewById(R.id.tool_bar_of_specific_chat);
        mbackbuttonofspecificchat=findViewById(R.id.back_button_of_specific_chat);


        usersArrayList = new ArrayList<>();
        messagesArrayList=new ArrayList<>();
        mmessagerecyclerview=findViewById(R.id.recycler_view_of_specific);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter=new MessagesAdapter(SpecificChat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);




        intent=getIntent();

        setSupportActionBar(mtoolbarofspecificchat);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");


        msenderuid=firebaseAuth.getUid();
        mrecieveruid=getIntent().getStringExtra("userId");



        senderroom=msenderuid+mrecieveruid;
        recieverroom=mrecieveruid+msenderuid;

        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter=new MessagesAdapter(SpecificChat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Messages messages=snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        mbackbuttonofspecificchat.setOnClickListener(view -> {
            startActivity(new Intent(SpecificChat.this,Chat.class));
            finish();
        });


        msendmessagebutton.setOnClickListener(view -> {

            enteredmessage=mgetmessage.getText().toString();
            if(enteredmessage.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Enter message first",Toast.LENGTH_SHORT).show();
            }

            else

            {
                Date date=new Date();
                currenttime=simpleDateFormat.format(calendar.getTime());
                Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);
                firebaseDatabase=FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats")
                        .child(senderroom)
                        .child("messages")
                        .push().setValue(messages).addOnCompleteListener(task -> firebaseDatabase.getReference()
                                .child("chats")
                                .child(recieverroom)
                                .child("messages")
                                .push()
                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //users_i_messaged
                                        firebaseFirestore.collection("Users")
                                                .document(firebaseAuth.getUid())
                                                .get().addOnCompleteListener(task1 -> {
                                                    DocumentSnapshot document = task1.getResult();
                                                    ArrayList<String> temp = (ArrayList<String>) document.get("users_i_messaged");
                                                    if(temp.size() != 0) {
                                                        usersArrayList.addAll(temp);
                                                    }
                                                    if(!usersArrayList.contains(mrecieveruid)) {
                                                        usersArrayList.add(0, mrecieveruid);
                                                    }
                                                }).addOnCompleteListener(task12 -> firebaseFirestore
                                                        .collection("Users")
                                                        .document(firebaseAuth.getUid())
                                                        .update("users_i_messaged",usersArrayList)).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        firebaseFirestore.collection("Users")
                                                                .document(mrecieveruid)
                                                                .get().addOnCompleteListener(task13 -> {
                                                                    DocumentSnapshot document = task13.getResult();
                                                                    ArrayList<String> temp = (ArrayList<String>) document.get("users_i_messaged");
                                                                    usersArrayList.clear();
                                                                    if(temp!=null) {
                                                                        if (temp.size() != 0) {
                                                                            usersArrayList.addAll(temp);
                                                                        }
                                                                    }
                                                                    if(!usersArrayList.contains(msenderuid)) {
                                                                        usersArrayList.add(0,msenderuid);
                                                                    }
                                                                }).addOnCompleteListener(task14 -> firebaseFirestore
                                                                        .collection("Users")
                                                                        .document(mrecieveruid)
                                                                        .update("users_i_messaged",usersArrayList));
                                                    }
                                                });
                                    }
                                }));

                mgetmessage.setText(null);




            }
            finish();
            startActivity(getIntent());
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }
}