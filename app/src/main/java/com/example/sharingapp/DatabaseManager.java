package com.example.sharingapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Environment;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DatabaseManager implements  ValueEventListener {
    private final FirebaseDatabase database;

    private ArrayList<Item> itemsArrayList = new ArrayList<>();
    private ArrayList<User> userArrayList = new ArrayList<>();

    private ArrayList<Bid> bidsArrayList = new ArrayList<>();

    // use a singleton design pattern
    private static  DatabaseManager instance;

    private DatabaseManager() {
        this.database = FirebaseDatabase.getInstance();
        this.loadInfo();
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();

        return instance;
    }

    private void loadInfo() {
        this.loadUsers();
        this.loadItems();
        this.loadBids();
    }
    public void loadUsers() {

        userArrayList.clear();
        DatabaseReference users = this.database.getReference("users");
        // Listen for changes to the users collection
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the users from the data snapshot
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String id = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    User user = new User(username,email,id);
                    userArrayList.add(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public void addUser(User user) {
        DatabaseReference users = this.database.getReference("users");
        users.child(user.getId()).child("email").setValue(user.getEmail());
        users.child(user.getId()).child("username").setValue(user.getUsername());
    }

    public ArrayList<User> getUsers() {
        return this.userArrayList;
    }

    public void addItem(Item item) {
        this.itemsArrayList.add(item);
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
        newItem.child("borrowerId").setValue(item.getBorrowerId());

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


    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get the items from the data snapshot
       //ArrayList<Item> itemsArrayList = new ArrayList<>();
       // itemsArrayList.clear();
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
                UserList users = new UserList();
                users.setUsers(this.getUsers());
                item.setBorrower(users.getUserByUserId(borrowerId));
            }
            itemsArrayList.add(item);
            Log.d("DM item onDataChanged: ", itemsArrayList.size() + " id: " + item.getId());
        //    this.itemList.setItems(items);
        } // end for
        //this.itemList.setItems(itemsArrayList);

    } // end method

    public ArrayList<Item> getItems() {
        return  this.itemsArrayList;
    }


    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // Handle the error
    }

    public void recordBid(Bid bid, String bidderId) {
        DatabaseReference items = this.database.getReference("items");

        DatabaseReference biddedItem = items.child(bid.getItemId());
        biddedItem.child("status").setValue(Item.BIDDED_STATUS);
        biddedItem.child("borrowerId").setValue("none");

        DatabaseReference bids = this.database.getReference("bids");
        DatabaseReference bidDB = bids.child(bid.getBidId());
        bidDB.child("amount").setValue(bid.getBidAmount().floatValue());
        bidDB.child("itemId").setValue(bid.getItemId());
        bidDB.child("bidderId").setValue(bidderId);
        bidDB.child("username").setValue(bid.getBidderUsername());
    }

    public void loadBids() {

        this.bidsArrayList.clear();
        DatabaseReference bids = this.database.getReference("bids");
        // Listen for changes to the users collection
        bids.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the users from the data snapshot
                for (DataSnapshot bidSnapshot : dataSnapshot.getChildren()) {
                    String bidId = bidSnapshot.getKey();
                    Float amount = bidSnapshot.child("amount").getValue(Float.class);
                    String itemId = bidSnapshot.child("itemId").getValue(String.class);
                    String bidderId = bidSnapshot.child("bidderId").getValue(String.class);
                    String bidderUserName = bidSnapshot.child("username").getValue((String.class));

                    Bid bid = new Bid(bidId, itemId, amount, bidderId, bidderUserName );
                    bidsArrayList.add(bid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public ArrayList<Bid> getBids() {
        return this.bidsArrayList;
    }

    public void removeBid(String bidId) {
        DatabaseReference bidRef = this.database.getReference().child("bids").child(bidId);
        bidRef.removeValue();
    }

    public void removeItem(String itemId) {
        DatabaseReference itemRef = this.database.getReference().child("items").child(itemId);
        itemRef.removeValue();
    }

}


