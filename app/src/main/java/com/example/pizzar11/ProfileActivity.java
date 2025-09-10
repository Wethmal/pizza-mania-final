package com.example.pizzar11;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Bitmap bitmap;

    private static final int IMAGE_PICK_CODE = 101;
    private static final int IMAGE_CAPTURE_CODE = 102;

    EditText tvName, tvPhone, etMessage;
    Button btnUpdateName, btnUpdatePhone, btnAddImage, btnLogout;
    ImageView ivProfile, ivSend;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        //this is for to the hanlde the nav bar
        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(this, LocationActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });


        // Views
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        etMessage = findViewById(R.id.et_contact_message);
        btnUpdateName = findViewById(R.id.btn_update_name);
        btnUpdatePhone = findViewById(R.id.btn_update_phone);
        btnAddImage = findViewById(R.id.btn_add_image);
        btnLogout = findViewById(R.id.btn_logout);
        ivProfile = findViewById(R.id.iv_profile);
        ivSend = findViewById(R.id.iv_send);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        // Load current user data
        loadUserData();

        // Update name
        btnUpdateName.setOnClickListener(v -> {
            String newName = tvName.getText().toString().trim();
            if(!newName.isEmpty()){
                db.collection("user_data").document(uid)
                        .update("name", newName)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Name updated", Toast.LENGTH_SHORT).show());
            }
        });

        // Update phone
        btnUpdatePhone.setOnClickListener(v -> {
            String newPhone = tvPhone.getText().toString().trim();
            if(!newPhone.isEmpty()){
                db.collection("user_data").document(uid)
                        .update("phone", newPhone)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Phone updated", Toast.LENGTH_SHORT).show());
            }
        });

        // Add image
        btnAddImage.setOnClickListener(v -> showImageOptions());

        // Send message
        ivSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if(!msg.isEmpty()){
                Map<String, Object> message = new HashMap<>();
                message.put("uid", uid);
                message.put("message", msg);
                message.put("timestamp", System.currentTimeMillis());

                db.collection("user_messages").add(message)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
                            etMessage.setText("");
                        });
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData(){
        db.collection("user_data").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        tvName.setText(documentSnapshot.getString("name"));
                        tvPhone.setText(documentSnapshot.getString("phone"));

                        String encodedImage = documentSnapshot.getString("profileImage");
                        if(encodedImage != null){
                            byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            ivProfile.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    private void showImageOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setMessage("App only supports images under 1MB")
                .setPositiveButton("Gallery", (dialog, which) -> pickFromGallery())
                .setNegativeButton("Camera", (dialog, which) -> pickFromCamera())
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void pickFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Bitmap bitmap = null;

            try{
                if(requestCode == IMAGE_PICK_CODE && data != null){
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } else if(requestCode == IMAGE_CAPTURE_CODE && data != null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                if(bitmap != null){
                    // Check size < 1MB
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    if(baos.toByteArray().length > 1024*1024){
                        Toast.makeText(this, "Image too large (>1MB)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    Bitmap finalBitmap = bitmap;
                    db.collection("user_data").document(uid)
                            .update("profileImage", encodedImage)
                            .addOnSuccessListener(aVoid -> {
                                ivProfile.setImageBitmap(finalBitmap);
                                Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();
                            });

                }

            } catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
