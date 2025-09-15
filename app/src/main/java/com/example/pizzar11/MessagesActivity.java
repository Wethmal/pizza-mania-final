package com.example.pizzar11;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerMessages = findViewById(R.id.recyclerMessages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerMessages.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Load messages
        db.collection("user_messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Message msg = dc.getDocument().toObject(Message.class);

                        switch (dc.getType()) {
                            case ADDED:
                                messageList.add(0, msg); // newest first
                                break;
                            case MODIFIED:
                                // update if needed
                                break;
                            case REMOVED:
                                messageList.remove(msg);
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
