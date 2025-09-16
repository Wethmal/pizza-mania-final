package com.example.pizzar11;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class userwatch extends AppCompatActivity {

    RecyclerView recyclerUsers;
    UserDatabaseHelper dbHelper;
    List<User> userList;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userwatch);

        ImageView ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());

        recyclerUsers = findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new UserDatabaseHelper(this);
        userList = new ArrayList<>();

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(cursor.getColumnIndexOrThrow("uid"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                userList.add(new User(uid, name, email, phone, role));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new UserAdapter(this, userList);
        recyclerUsers.setAdapter(adapter);
    }
}
