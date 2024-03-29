package com.example.sharingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Item class
 */
public class Item extends Observable {

    private String title;
    private String maker;
    private String description;
    private Dimensions dimensions;
    private String status;
    private Float minimum_bid;
    private User borrower;
    private String owner_id;
    protected transient Bitmap image;
    protected String image_base64;
    private String id;
    public static final String BIDDED_STATUS = "Bidded";
    public static final String AVAILABLE_STATUS = "Available";

    public static final String BORROWED_STATUS = "Borrowed";
    public Item(String title, String maker, String description, String owner_id, String minimum_bid, Bitmap image, String id) {
        this.title = title;
        this.maker = maker;
        this.description = description;
        this.dimensions = null;
        this.owner_id = owner_id;
        this.status = Item.AVAILABLE_STATUS;
        this.minimum_bid = Float.valueOf(minimum_bid);
        this.borrower = null;
        addImage(image);

        if (id == null){
            setId();
        } else {
            updateId(id);
        }
    }

    public String getId(){
        return this.id;
    }

    public void setId() {
        this.id = UUID.randomUUID().toString();
        notifyObservers();
    }

    public void updateId(String id){
        this.id = id;
        notifyObservers();
    }

    public void setTitle(String title) {
        this.title = title;
        notifyObservers();
    }

    public String getTitle() {
        return title;
    }

    public void setMaker(String maker) {
        this.maker = maker;
        notifyObservers();
    }

    public String getMaker() {
        return maker;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyObservers();
    }

    public String getDescription() {
        return description;
    }

    public Float getMinBid() {
        return this.minimum_bid;
    }

    public void setMinBid(Float minimum_bid) {
        this.minimum_bid = minimum_bid;
        notifyObservers();
    }

    public void setOwnerId(String owner_id) {
        this.owner_id = owner_id;
        notifyObservers();
    }

    public String getOwnerId() {
        return owner_id;
    }

    public void setDimensions(String length, String width, String height) {
        dimensions = new Dimensions(length, width, height);
        notifyObservers();
    }

    public String getLength(){
        return dimensions.getLength();
    }

    public String getWidth(){
        return dimensions.getWidth();
    }

    public String getHeight(){
        return dimensions.getHeight();
    }

    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    public String getStatus() {
        return status;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
        notifyObservers();
    }


    public String getBorrowerId() {
        if (this.borrower != null)
            return this.borrower.getId();
        else
            return "none";
    }

    public User getBorrower() {
        return borrower;
    }

    public String getBorrowerUsername() {
        if (borrower != null){
            return borrower.getUsername();
        }
        return null;
    }

    public void addImage(Bitmap new_image){
        if (new_image != null) {
            image = new_image;
            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            new_image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);

            byte[] b = byteArrayBitmapStream.toByteArray();
            image_base64 = Base64.encodeToString(b, Base64.DEFAULT);
        }
        notifyObservers();
    }
    public Bitmap removeImage() {
        Bitmap temp = this.image;
        this.image = null;
        return  temp;
    }
    public Bitmap getImage(){
        if (image == null && image_base64 != null) {
            byte[] decodeString = Base64.decode(image_base64, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            notifyObservers();
        }
        return image;
    }
}

