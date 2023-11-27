package com.example.sharingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DatabaseManager implements  ValueEventListener {
    private FirebaseDatabase database;
    private UserList userList;
    private ItemList itemList;
    private ArrayList<Item> itemsArrayList = new ArrayList<>();
    private ArrayList<User> userArrayList = new ArrayList<>();

    // use a singleton design pattern
    private static  DatabaseManager instance;

    private DatabaseManager() {
        this.database = FirebaseDatabase.getInstance();
        this.userList = new UserList();
        this.loadUsers();
        this.itemList = new ItemList();
        this.loadItems();
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();

        return instance;
    }

    public void loadUsers() {
        //ArrayList<User> userlist = new ArrayList<User>();
        userArrayList.clear();
        DatabaseReference users = this.database.getReference("users");
        // Listen for changes to the users collection
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the users from the data snapshot
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String id = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    User user = new User(username,email,id);
                    Log.i("User", user.toString());
                    userArrayList.add(user);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
        this.userList.setUsers(userArrayList);
    }

    public void addUser(User user) {
        DatabaseReference users = this.database.getReference("users");
        users.child(user.getId()).child("email").setValue(user.getEmail());
        users.child(user.getId()).child("username").setValue(user.getUsername());
    }

    public UserList getUserList() {
        this.userList.setUsers(this.userArrayList);
        return this.userList;
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


    public void loadItems() {
        DatabaseReference items = this.database.getReference("items");
        // Listen for changes to the users collection
        items.addValueEventListener(this);

        Log.d("DM itemList: ",this.itemList.getSize()+" "+this.itemList);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get the items from the data snapshot
       //ArrayList<Item> itemsArrayList = new ArrayList<>();
        itemsArrayList.clear();
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            String id = itemSnapshot.getKey();
            String title = itemSnapshot.child("title").getValue(String.class);
            String maker = itemSnapshot.child("maker").getValue(String.class);
            String description = itemSnapshot.child("description").getValue(String.class);
            String owner = itemSnapshot.child("ownerId").getValue(String.class);
            String minBid = itemSnapshot.child("minBid").getValue(Long.class).toString();
            String image = itemSnapshot.child("image").getValue(String.class);
            Bitmap bitmap = null;
            if (image.equals("Yes")) {
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                String fileName = "IMG_" + id + ".jpg";
                File imageFile = new File(storageDir, fileName);
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            }


            Item item = new Item(title,maker, description, owner,minBid,bitmap, id);

            String status = itemSnapshot.child("status").getValue(String.class);
            item.setStatus(status);

            String l = itemSnapshot.child("length").getValue(String.class);
            String w = itemSnapshot.child("width").getValue(String.class);
            String h = itemSnapshot.child("height").getValue(String.class);
            item.setDimensions(l,w,h);

            String borrowerId = itemSnapshot.child("borrowerId").getValue(String.class);
            if (!borrowerId.equals("none")) {
                item.setBorrower(this.userList.getUserByUserId(borrowerId));
            }
            itemsArrayList.add(item);
            Log.d("DM item onDataChanged: ", itemsArrayList.size() + " id: " + item.getId());
        //    this.itemList.setItems(items);
        } // end for
        //this.itemList.setItems(itemsArrayList);
        Log.d("DM itemList: ", this.itemList.getSize()+ " " + this.itemList);
    } // end method

    public ItemList getItems() {
        this.itemList.setItems(itemsArrayList);
        Log.d("DM getItmes ", itemsArrayList.size() + " " + this.itemsArrayList);
        Log.d("DM getItems" , this.itemList.getSize() + " " + this.itemList);
        return this.itemList;
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Handle the error
    }
}
