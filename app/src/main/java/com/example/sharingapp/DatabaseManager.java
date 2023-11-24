package com.example.sharingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;

public class DatabaseManager {
    private FirebaseDatabase database;

    // use a singleton design patter
    private static  DatabaseManager instance;

    private DatabaseManager() {
        this.database = FirebaseDatabase.getInstance();
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();

        return instance;
    }

    public void loadUsers(UserList userlist) {

        DatabaseReference users = this.database.getReference("users");
        // Listen for changes to the users collection
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the users from the data snapshot
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String id = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    User user = new User(username,email,id);
                    Log.i("User", user.toString());
                    userlist.addUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public void addUser(User user) {
        DatabaseReference users = this.database.getReference("users");
        users.child(user.getId()).child("email").setValue(user.getEmail());
        users.child(user.getId()).child("username").setValue(user.getUsername());
    }

    public void addItem(Item item) {
        DatabaseReference items = this.database.getReference("items");
        DatabaseReference newItem = items.child(item.getId());
        newItem.child("title").setValue(item.getTitle());
        newItem.child("ownerId").setValue(item.getOwnerId());
        newItem.child("maker").setValue(item.getMaker());
        newItem.child("description").setValue(item.getDescription());
        newItem.child("length").setValue(item.getLength());
        newItem.child("width").setValue(item.getWidth());
        newItem.child("height").setValue(item.getHeight());
        newItem.child("minBid").setValue(item.getMinBid());
        newItem.child("status").setValue(item.getStatus());
        newItem.child("borrowerId").setValue("none");

        Bitmap image = item.getImage();
        if (image != null)
            newItem.child("image").setValue("Yes");
        else
            newItem.child("image").setValue("No");

        // TODO: ADD Firebase Storage
        // if the image is not null save locally
        if (image != null) {
            // save image locally
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String fileName = "IMG_" + item.getId() + ".jpg";
            File imageFile = new File(storageDir, fileName);

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.e("addItem image: ", e.getMessage());
            }
        }
    }
}
