package com.example.carcom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;

    FirestoreRecyclerAdapter<User,NoteViewHolder> chatAdapter;

    RecyclerView mrecyclerview;
    private ArrayList<String> users = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    ArrayList<Messages> messagesArrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_chat_fragment,container,false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        mrecyclerview=v.findViewById(R.id.recyclerview);

        firebaseFirestore.collection("Users")
                .document(firebaseAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    users.addAll((ArrayList<String>) document.get("users_i_messaged"));
                }).addOnCompleteListener(task -> {

                    Query query=firebaseFirestore.collection("Users").whereIn("uid", users);
                    FirestoreRecyclerOptions<User> allusername=new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

                    chatAdapter=new FirestoreRecyclerAdapter<User, NoteViewHolder>(allusername) {
                        @Override
                        protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull User user) {
                            noteViewHolder.particularlicense.setText(user.getLicense());

                            noteViewHolder.itemView.setOnClickListener(view -> {

                                Intent intent=new Intent(getActivity(), SpecificChat.class);
                                intent.putExtra("userId",user.getUid());
                                startActivity(intent);
                            });
                        }

                        @NonNull
                        @Override
                        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
                            return new NoteViewHolder(view);
                        }
                    };


                    mrecyclerview.setHasFixedSize(true);
                    linearLayoutManager=new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    mrecyclerview.setLayoutManager(linearLayoutManager);
                    mrecyclerview.setAdapter(chatAdapter);
                    chatAdapter.startListening();
                });

        return v;




    }


    public class NoteViewHolder extends RecyclerView.ViewHolder
    {

        private TextView particularlicense;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particularlicense=itemView.findViewById(R.id.name_of_user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(chatAdapter!=null)
//        {
//            chatAdapter.stopListening();
//        }
    }
}