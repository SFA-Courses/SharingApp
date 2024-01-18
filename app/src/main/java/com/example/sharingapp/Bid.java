package com.example.sharingapp;

import java.util.UUID;

/**
 * Bid Class
 */
public class Bid extends Observable {
    private String item_id;
    private String bid_id;
    private Float bid_amount;

    private String bidderId;
    private String bidder_username;

    public Bid(String item_id, Float bid_amount, String bidder_username) {
        this.item_id = item_id;
        this.bid_amount = bid_amount;
        this.bidder_username = bidder_username;
        setBidId();
    }

    public Bid(String bidId, String item_id, Float bid_amount, String bidderId, String bidderUsername) {
        this.item_id = item_id;
        this.bid_amount = bid_amount;
        this.bid_id = bidId;
        this.bidder_username = bidderUsername;
    }

    public String getItemId() {
        return this.item_id;
    }

    public void setItemId(String item_id) {
        this.item_id = item_id;
    }

    public String getBidId(){
        return this.bid_id;
    }

    public void setBidId() {
        this.bid_id = UUID.randomUUID().toString();
        notifyObservers();
    }

    public void setBidAmount(Float bid_amount) {
        this.bid_amount = bid_amount;
        notifyObservers();
    }

    public Float getBidAmount() {
        return bid_amount;
    }

    public void setBidderUsername(String bidder_username) {
        this.bidder_username = bidder_username;
        notifyObservers();
    }

    public String getBidderUsername() {
        return bidder_username;
    }
}
